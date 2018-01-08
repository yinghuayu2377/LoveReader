package com.example.lenovo.lovereader;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.lenovo.lovereader.NetWork.NetUtil2;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2016/12/25.
 */
public class html_content extends Activity {
    TextView tv;
    Handler handler;
    private ArrayList<String> result=null;
    private String url = null;
    private String title=null;
    private String content_text=null;
    private LinearLayout layout_htmlcontent;  //这个代表的是htmlcontent_layout
    private TextView text_title;
    private ImageView image;
    private static final int ITEM0=Menu.FIRST;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htmlcontent_layout);
        tv = (TextView) findViewById(R.id.text_content);
        Context context=html_content.this;
        layout_htmlcontent=(LinearLayout)findViewById(R.id.htmlcontent_layout);   //这个代表的是htmlcontent_layout

        text_title=(TextView)findViewById(R.id.title_text_11);

        image=(ImageView)findViewById(R.id.return_select_11);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //有网络
        if(NetUtil2.getNetworkState(this)!=NetUtil2.NETWORK_NONE){

            //根据news_url来解析相应的网页
            SharedPreferences sharedPreferences = context.getSharedPreferences("config_2",MODE_PRIVATE);
            url=sharedPreferences.getString("news_url",null);  //从news_url中获取选中的新闻url
            title=sharedPreferences.getString("news_title",null);
            text_title.setText(title);
            getCsdnNetDate();
            handler=new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message message) {
                    for(int i=0;i<result.size();i++)
                    {
                        content_text=content_text+result.get(i);
                    }
                    content_text=content_text.replace("null","");
                    tv.setMovementMethod(ScrollingMovementMethod.getInstance());  //内容过多时加滚动条
                    //设置文字大小
                    SharedPreferences sharedPreferences = getSharedPreferences("config_3",MODE_PRIVATE);
                    int text_size_2= Integer.parseInt(sharedPreferences.getString("text_size","18"));
                    tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,text_size_2);
                    //设置背景颜色
                    SharedPreferences sharedPreferences1 = getSharedPreferences("config_4",MODE_PRIVATE);
                    int night_type_2= Integer.parseInt(sharedPreferences1.getString("night_type","0"));
                    if(night_type_2==0){
                        layout_htmlcontent.setBackgroundColor(Color.parseColor("#ffffff"));
                    }else if(night_type_2==1){
                        layout_htmlcontent.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    }

                    tv.setText("        "+content_text);
                    return false;
                }
            });
        }else
        {
            Toast.makeText(html_content.this,"没有网络",Toast.LENGTH_LONG).show();;
            tv.setText("        ");
        }

       this.registerForContextMenu(tv);
    }

    //注册上下文菜单
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,ITEM0,0,"查看原文");
    }

    //上下文菜单回调函数
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case ITEM0:
                Uri uri= Uri.parse(url);
                Intent intent2=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent2);
                break;
        }
        return true;
    }

    //解析网页的内容
    private ArrayList<String> getCsdnNetDate() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                result=new ArrayList<String>();
                Document doc = http_get(url);   //连接uri
                Elements links = doc.select("div#Cnt-Main-Article-QQ>p");  //id为div#Cnt-Main-Article-QQ的p标签内容
                for (Element link : links)
                {
                    result.add(link.text());
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
        return result;
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

}