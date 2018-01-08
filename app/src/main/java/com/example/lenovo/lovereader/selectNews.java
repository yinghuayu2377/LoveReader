package com.example.lenovo.lovereader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lenovo.lovereader.News.News;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/12/25.
 */
public class selectNews extends Activity {
    private EditText edit_select;
    private ArrayList<String> selectlist;    //不变的列表
    private ArrayList<String> selectlist2;  //变化的列表
    private ArrayList<String> selecturllist;    //不变的列表
    private ArrayList<String> selecturllist2;  //变化的列表
    private ArrayAdapter<String> adapter;
    private ListView select_lv;
    private LinearLayout select_layout;
    private String str_edit=null;
    private TextView text_select;
    private ImageView image;
    MyApplication myApplication;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_layout);

        myApplication=(MyApplication)this.getApplication();
        selectlist=new ArrayList<String>();
        selectlist2=new ArrayList<String>();
        selecturllist=new ArrayList<String>();
        selecturllist2=new ArrayList<String>();
        select_layout=(LinearLayout)findViewById(R.id.select_layout);

        //取出titles
        selectlist.clear();
        selectlist2.clear();
        selecturllist.clear();
        selecturllist2.clear();
        selectlist=myApplication.getmNewsTitle();
        selecturllist=myApplication.getmNewsUrl();
        for(int i=0;i<selectlist.size();i++)
        {
            selectlist2.add(selectlist.get(i));;
            selecturllist2.add(selecturllist.get(i));
        }

        text_select=(TextView)findViewById(R.id.title_text_1);
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        int news_type= Integer.parseInt(sharedPreferences.getString("news_type","0"));  //从news_type中获取选中的新闻类型
        if(news_type==0) {
            text_select.setText("国际新闻");
        }else if(news_type==1){
            text_select.setText("社会热点");
        }

        //单击listview
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,selectlist2);
        select_lv=(ListView)findViewById(R.id.select_list);
        select_lv.setAdapter(adapter);
        select_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url_click=selecturllist2.get(i);
                String url_title=selectlist2.get(i);
                //将数据保存到SharedPreferences中,如果用intent传递的话可能会有问题
                SharedPreferences sharedPreferences = getSharedPreferences("config_2",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("news_url", url_click);   //将选择的新闻url放入config_2.xml文件中
                editor.putString("news_title", url_title);
                Log.e("news_url",url_click);
                editor.commit();

                Intent intent=new Intent();
                intent.setClass(selectNews.this,html_content.class);
                startActivity(intent);
            }
        });

        //edittext输入内容
        edit_select=(EditText)findViewById(R.id.title_select_1);
        edit_select.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                selectlist2.clear();
                selecturllist2.clear();
                int t=0;
                str_edit=edit_select.getText().toString();
                for(int k=0;k<selectlist.size();k++)
                {
                    if(selectlist.get(k).indexOf(str_edit)!=-1)  //输入的内容是标题的一部分
                    {
                        selectlist2.add(t,selectlist.get(k));
                        selecturllist2.add(t,selecturllist.get(k));
                        t++;
                    }
                }
                adapter.notifyDataSetChanged();  //更新适配器
            }
        });

        //设置背景颜色
        SharedPreferences sharedPreferences1 = getSharedPreferences("config_4",MODE_PRIVATE);
        int night_type_2= Integer.parseInt(sharedPreferences1.getString("night_type","0"));
        if(night_type_2==0){
            select_layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(night_type_2==1){
            select_layout.setBackgroundColor(Color.parseColor("#aaaaaa"));
        }

        image=(ImageView)findViewById(R.id.return_select);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
