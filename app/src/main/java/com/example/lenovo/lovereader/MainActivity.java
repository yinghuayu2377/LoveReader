package com.example.lenovo.lovereader;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.lenovo.lovereader.DB.myDB;
import com.example.lenovo.lovereader.NetWork.NetUtil2;
import com.example.lenovo.lovereader.News.News;
import com.example.lenovo.lovereader.SlidingMenu.SlidingMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MyAdapter adapter;   //一个自定义的Adapter
    private ListView listview;
    private TextView textview12;
    private ArrayList<News> listItems;   //listItem数组
    public ArrayList<News> list=new ArrayList<News>();   //保存News的所有信息
    private ImageView image_menu;
    private ImageView image_select;
    private Handler handler;
    final String url_iniv="http://news.qq.com/world_index.shtml";
    private SlidingMenu slidingMenu;   //侧滑的效果
    private ListView listview_menu;  //这个是menu中的listview
    private ArrayList<String> url_list;  //uri的数组
    private RelativeLayout layout;  //这个代表的是activity_main_layout
    private LinearLayout layout_menu;  //这个代表的是menu_layout
    private ArrayList<String> titles_list=new ArrayList<String>();  //标题数组
    private ArrayList<String> froms_list=new ArrayList<String>();  //标题数组
    private ArrayList<String> talks_list=new ArrayList<String>();  //标题数组
    private ArrayList<String> image_list=new ArrayList<String>();  //标题数组
    private String night_type_2;

    private int text_size=18;  //字体大小
    private int night_type=0;   //夜间模式
    private SQLiteDatabase db;
    private myDB mydb=new myDB(this);
    private MyApplication myApplication1;

    private ArrayList<String> result=new ArrayList<String>();   //新闻文本的每一个p
    private ArrayList<String> mNewsContent2=new ArrayList<String>();   //新闻的具体文本内容

    //定位相关
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new MyLocationListener();

    //上下文菜单
    private static final int ITEM1= Menu.FIRST;    //夜间模式
    private static final int ITEM2= Menu.FIRST+1;   //字体大小
    private static final int ITEM3= Menu.FIRST+2;    //下载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = mydb.getWritableDatabase();   //得到数据库实例
        myApplication1 = (MyApplication) this.getApplication();


        //手指滑动菜单
        slidingMenu = new SlidingMenu(this, LayoutInflater.from(this).inflate(R.layout.activity_main, null),
                LayoutInflater.from(this).inflate(R.layout.menu_layout, null), night_type);  //建立主页layout与menu之间的关系
        setContentView(slidingMenu);

        layout = (RelativeLayout) findViewById(R.id.main_content_layout);  //这个代表的是activity_main_layout
        layout_menu = (LinearLayout) findViewById(R.id.menu_layout);   //这个代表的是menu_layout

        //动态设置字体大小
        SharedPreferences sharedPreferences3 = getSharedPreferences("config_3", MODE_PRIVATE);
        int text_size_2 = Integer.parseInt(sharedPreferences3.getString("text_size", "18"));
        text_size = text_size_2;

        //设置夜间模式
        //夜间模式
        SharedPreferences sharedPreferences1 = getSharedPreferences("config_4", MODE_PRIVATE);
        int nightype = Integer.parseInt(sharedPreferences1.getString("night_type", "0"));
        night_type = nightype;

        if (nightype == 0) {
            layout.setBackgroundColor(Color.parseColor("#ffffff"));
            night_type_2="夜间模式";
            layout_menu.setBackgroundColor(Color.parseColor("#ffffff"));
        } else if (nightype == 1) {
            layout.setBackgroundColor(Color.parseColor("#aaaaaa"));
            night_type_2="日间模式";
            layout_menu.setBackgroundColor(Color.parseColor("#aaaaaa"));
        }

        url_list = new ArrayList<String>();
        listItems = new ArrayList<News>();

        //网络连接
        if (NetUtil2.getNetworkState(this) != NetUtil2.NETWORK_NONE) {
            Toast.makeText(MainActivity.this, "已连接网络", Toast.LENGTH_LONG).show();
            ;
            Log.e("jiexi", "2");
        } else {
            Toast.makeText(MainActivity.this, "没有网络", Toast.LENGTH_LONG).show();
            ;
        }

        textview12=(TextView)findViewById(R.id.title_text12);
        Context context12 = MainActivity.this;
        SharedPreferences sharedPreferences12 = context12.getSharedPreferences("config",MODE_PRIVATE);
        int news_type= Integer.parseInt(sharedPreferences12.getString("news_type","0"));
        if(news_type==0)
        {
            textview12.setText("国际新闻");
        }else if(news_type==1){
            textview12.setText("社会热点");
        }
        textview12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setBackgroundColor(Color.parseColor("#777777"));  //为mainView改变背景颜色，menu打开时为灰
                slidingMenu.open();   //打开menu页面
            }
        });

        listview = (ListView) findViewById(R.id.list_content);
        this.registerForContextMenu(listview);   //为listview注册上下文菜单
        //与多线程有关
        handler = new Handler(new Handler.Callback() {//用来在线程之间通信的机制
            @Override
            public boolean handleMessage(Message message) {
                Log.e("jiexi", "4");

                //点击listview
                listItems = new ArrayList<News>();
                listItems = list;
                adapter = new MyAdapter(MainActivity.this, listItems, text_size);   //构造一个自定义的adapter
                listview.setAdapter(adapter);

                Log.e("society_1", "1");

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        //读取新闻,activity的传递
                        String url_click = url_list.get(i);
                        String url_title=titles_list.get(i);


                        //将数据保存到SharedPreferences中,如果用intent传递的话可能会有问题
                        Context context = MainActivity.this;
                        SharedPreferences sharedPreferences = context.getSharedPreferences("config_2", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("news_url", url_click);   //将选择的新闻url放入config_2.xml文件中
                        editor.putString("news_title",url_title);
                        Log.e("news_url", url_click);
                        editor.commit();

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, html_content.class);
                        startActivity(intent);

                    }
                });


                //因为在acitivity中添加touchlistener不成功，所以改到这里,滑动事件
                listview.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:  //点击
                                slidingMenu.onTouchEvent(motionEvent);
                                break;

                            case MotionEvent.ACTION_MOVE:  //移动
                                slidingMenu.onTouchEvent(motionEvent);
                                break;

                            case MotionEvent.ACTION_UP:  //抬起
                                slidingMenu.setNightType(night_type);  //传递夜间模式参数
                                slidingMenu.onTouchEvent(motionEvent);
                                break;
                        }
                        return false;   //可以与单击事件区别开
                    }
                });

                return false;
            }
        });

        doParse(url_iniv);

        //点击搜索按钮
        image_select=(ImageView)findViewById(R.id.title_select);
        image_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent3=new Intent();
                intent3.setClass(MainActivity.this,selectNews.class);
                startActivity(intent3);

            }
        });


        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                new String[]{"国际新闻","社会热点","下载内容","我的位置"});

        listview_menu=(ListView)findViewById(R.id.menu_list);
        listview_menu.setAdapter(adapter);
        listview_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //将数据保存到SharedPreferences中,如果用intent传递的话可能会有问题
                Context context=MainActivity.this;
                SharedPreferences sharedPreferences = context.getSharedPreferences("config",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                if(i==1||i==0){
                    editor.putString("news_type", String.valueOf(i));   //将选择的新闻类型放入config.xml文件中
                    Log.e("news_type",String.valueOf(i));
                    editor.commit();
                }

                slidingMenu.close();  //关闭menu页面

                //根据news_type的类型来解析相应的网页
                String news_type=sharedPreferences.getString("news_type","0");  //从news_type中获取选中的新闻类型
                Log.e("news_type_main",news_type);

                switch (i) {
                    case 0:
                        String url1 = "http://news.qq.com/world_index.shtml";
                        layout.setBackgroundColor(Color.parseColor("#ffffff"));
                        textview12.setText("国际新闻");
                        doParse(url1);//更改背景颜色
                        break;

                    case 1:
                        String url2 = "http://news.qq.com/society_index.shtml";
                        layout.setBackgroundColor(Color.parseColor("#ffffff"));  //更改背景颜色
                        textview12.setText("社会热点");
                        doParse(url2);
                        break;

                    case 2:  //下载内容
                        Intent intent5 = new Intent();
                        intent5.setClass(MainActivity.this, UpdateContent.class);
                        startActivity(intent5);
                        break;

                    case 3:   //定位
                        mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
                        mLocationClient.registerLocationListener(myListener); // 注册监听函数
                        setLocationOption();
                        mLocationClient.start();// 开始定位
                        break;
                }

            }
        });

    }

    //解析网页
    private void doParse(String Url) {
        //国际和社会部分
        titles_list.clear();
        froms_list.clear();
        image_list.clear();
        talks_list.clear();
        list.clear();  //要先清空里边原有的内容
        url_list.clear();
        final String url=Url;
        Log.e("jiexi","3");
        //new一个新线程，不能放在主线程中，否则会有问题
        new Thread(new Runnable() {
            @Override
            public void run() {

                try{
                    Document doc= Jsoup.connect(url).timeout(6000).get();
                    Elements elements=doc.getElementsByAttributeValue("class","list first");
                    Element element=elements.get(0);  //第一个list first
                    Elements qelements=element.getElementsByAttributeValue("class","Q-tpList");  //所有的Q-tpList，也就是所有新闻开始的class

                    for(int i=0;i<qelements.size();i++) {
                        Elements aelements = qelements.get(i).select("a");  //每一个新闻的a标签
                        Element a1 = aelements.get(0);  //第一个a是url
                        Element a2 = aelements.get(1);   //第二个a是标题
                        Element a3 = aelements.get(2);  //第三个a是评论数
                        Elements imgelements = a1.select("img");  //第一个 a的img标签
                        Element imgelement = imgelements.get(0);  //第一张图的标签
                        Elements spanelements = qelements.get(i).select("span");  //span标签
                        Element spanelement = spanelements.get(0);  //第一个span标签
                        String from1 = spanelement.text();  //第一个span标签的内容是来源
                        String img = imgelement.attr("src");  //第一张图
                        if (img == "") {
                            img = imgelement.attr("_src");  //因为有的图片是src，有的是_src
                        }
                        String urlTail = a1.attr("href");  //uri是a1的href属性
                        String title1 = a2.text();   //a2的内容是标题
                        String talk1 = a3.text();  //a3的内容是评论数

                        Bitmap bitmap = doImage(img);  //调用解析图片函数

                        News item = new News(urlTail, title1, bitmap, talk1, from1);
                        url_list.add(urlTail);
                        list.add(item);
                        titles_list.add(title1);
                        froms_list.add(from1);
                        image_list.add(img);
                        talks_list.add(talk1);

                        Log.e("society" + i, urlTail + "  " + title1 + "  " + img + "  " + talk1 + "  " + from1);

                    }

                    SharedPreferences sharedPreferences3=getSharedPreferences("config_5",MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences3.edit();
                    editor.clear();
                    editor.putInt("title_list_size",titles_list.size());
                    for(int i=0;i<titles_list.size();i++)
                    {
                        editor.putString("title_list_"+i,titles_list.get(i));
                        editor.putString("url_list_"+i,url_list.get(i));
                        editor.putString("from_list_"+i,froms_list.get(i));
                        editor.putString("image_list_"+i,image_list.get(i));
                        editor.putString("talk_list_"+i,talks_list.get(i));
                    }
                    editor.commit();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(0);
            }
        }).start();

    }

    //解析图片
    private Bitmap doImage(String img) {

        HttpURLConnection conn=null;
        URL uri = null;
        Bitmap bitmap= null;
        //显示网络上的URI图片，先将uri转换为位图bitmap格式，然后构造一个新的news，在MyAdapter中直接就可以调用
        try{
            uri=new URL(img);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            conn=(HttpURLConnection)uri.openConnection();
            conn.setDoInput(true);
            InputStream is=conn.getInputStream();  //用流的方式
            bitmap=BitmapFactory.decodeStream(is);
            is.close();  //关闭流
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
        }

        return bitmap;
    }

    //连接uri
    private Document http_get(String url) {
        Document doc=null;
        try {
            doc = Jsoup.connect(url)
                    .timeout(50000)
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    //上下文菜单相关内容
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if(v==findViewById(R.id.list_content)) {
            menu.add(0, ITEM1, 0, night_type_2);
            menu.add(0, ITEM2, 0, "字体大小");
            menu.add(0, ITEM3, 0, "下载");
        }
    }

    //上下文菜单回调函数
    @Override
    public boolean onContextItemSelected(MenuItem item) {


        switch(item.getItemId()){

            case ITEM1:    //夜间模式
                if(night_type==0){
                    night_type=1;
                    layout.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    layout_menu.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    night_type_2="日间模式";
                }else if(night_type==1){
                    night_type=0;
                    layout.setBackgroundColor(Color.parseColor("#ffffff"));
                    layout_menu.setBackgroundColor(Color.parseColor("#ffffff"));
                    night_type_2="夜间模式";
                }
                SharedPreferences sharedPreferences = getSharedPreferences("config_4",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("night_type", String.valueOf(night_type));   //将选择的夜间模式放入config_3.xml文件中
                editor.commit();

                break;

            case ITEM2:   //字体大小

                //弹出一个选择选择字体大小的对话框
                LayoutInflater usinglayoutxml=LayoutInflater.from(MainActivity.this);
                final View text_size_view=usinglayoutxml.inflate(R.layout.dialog_text_size_layout,null);
                final RadioGroup rg=(RadioGroup)text_size_view.findViewById(R.id.rg);
                final RadioButton r1=(RadioButton)text_size_view.findViewById(R.id.rbutton_small);
                final RadioButton r2=(RadioButton)text_size_view.findViewById(R.id.rbutton_middle);
                final RadioButton r3=(RadioButton)text_size_view.findViewById(R.id.rbutton_big);
                SharedPreferences sharedPreferences3 = getSharedPreferences("config_3",MODE_PRIVATE);
                int textSize= Integer.parseInt(sharedPreferences3.getString("text_size","18"));
                Log.e("rbutton1", String.valueOf(textSize));
                //Radiobutton的选中状态
                if(textSize==12) {
                    r1.setChecked(true);
                }else if(textSize==18){
                    r2.setChecked(true);
                }
                else if(textSize==24){
                    r3.setChecked(true);
                }
                //对rg监听
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        if(R.id.rbutton_small==i){   //选中第一项
                            text_size = 12;
                        }else if (R.id.rbutton_middle==i) {   //选中第二项
                            text_size =18;
                        }
                        else if (R.id.rbutton_big==i) {   //选中第三项
                            text_size =24;
                        }
                    }
                });

                AlertDialog text_size_dialog=new AlertDialog.Builder(MainActivity.this)
                        .setTitle("选择字体大小")
                        .setView(text_size_view)
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        })
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //将数据保存到SharedPreferences中,如果用intent传递的话可能会有问题
                                SharedPreferences sharedPreferences3 = getSharedPreferences("config_3",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedPreferences3.edit();
                                editor.putString("text_size", String.valueOf(text_size));   //将选择的字体大小放入config_3.xml文件中
                                Log.e("text_size", String.valueOf(text_size));
                                editor.commit();

                                doParse(url_iniv);
                            }
                        })
                        .create();
                text_size_dialog.show();  //显示对话框
                break;

            case ITEM3:   //下载
                //弹出一个对话框
                AlertDialog text_size_dialog2=new AlertDialog.Builder(MainActivity.this)
                        .setTitle("下载当前内容需要消耗流量，是否继续")
                        .setPositiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                return;
                            }
                        })
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //解析新闻网页的具体文本内容，要在解析完形成列表之后解析
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNewsContent2.clear();
                                        for(int i=0;i<url_list.size();i++) {
                                            result.clear();
                                            String str_newscontent = "        ";

                                            String talk_ins = talks_list.get(i);
                                            String title_ins = titles_list.get(i);
                                            String image_ins = image_list.get(i);
                                            String from_ins = froms_list.get(i);

                                            String url3 = url_list.get(i);
                                            Document doc = http_get(url3);   //连接uri
                                            Elements links = doc.select("div#Cnt-Main-Article-QQ>p");  //id为div#Cnt-Main-Article-QQ的p标签内容
                                            for (Element link : links) {
                                                result.add(link.text());
                                            }
                                            for (int j = 0; j < result.size(); j++) {
                                                str_newscontent = str_newscontent + result.get(j);
                                            }
                                            mNewsContent2.add(str_newscontent);

                                            insert(title_ins, from_ins, Integer.parseInt(talk_ins), image_ins, str_newscontent);
                                        }
                                    }
                                }).start();

                            }
                        })
                        .create();
                text_size_dialog2.show();  //显示对话框
                break;
        }
        return true;
    }

    //下载新闻,将数据加入数据库中
    private void insert(String title, String from, int talk, String image, String content){
        ContentValues contentValues=new ContentValues();
        contentValues.put(myDB.Title,title);
        contentValues.put(myDB.Froms,from);
        contentValues.put(myDB.Talking,talk);
        contentValues.put(myDB.Image,image);
        contentValues.put(myDB.Content,content);
        db.insert(myDB.table_name,null,contentValues);
    }


    //定位相关
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        option.setAddrType("all");// 返回的定位结果包含地址信息
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
        option.disableCache(true);// 禁止启用缓存定位
        mLocationClient.setLocOption(option);
    }

    //与定位相关的类
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            StringBuffer sb = new StringBuffer(256);
            sb.append("当前时间 : ");
            sb.append(location.getTime());
            sb.append("\n错误码 : ");
            sb.append(location.getLocType());
            sb.append("\n纬度 : ");
            sb.append(location.getLatitude());
            sb.append("\n经度 : ");
            sb.append(location.getLongitude());
            sb.append("\n半径 : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\n速度 : ");
                sb.append(location.getSpeed());
                sb.append("\n卫星数 : ");
                sb.append(location.getSatelliteNumber());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\n地址 : ");
                sb.append(location.getAddrStr());
            }
            if(location.getTime()!=null) {
                Toast.makeText(MainActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
                Log.e("city",sb.toString());
            }
            if(location.getCity()!=null)
            {
                Toast.makeText(MainActivity.this, "城市为：" + location.getCity(), Toast.LENGTH_LONG).show();
                Log.e("city",location.getCity());

                mLocationClient.stop();
            }
        }

    }

}
