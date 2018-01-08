package com.example.lenovo.lovereader;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by lenovo on 2016/12/26.
 */
public class MyApplication extends Application {
    private static Application mApplication;
    private ArrayList<String> mNewsUrl=new ArrayList<String>();   //存放新闻列表
    private ArrayList<String> mNewsTitle=new ArrayList<String>();  //存放新闻的标题
    private ArrayList<String> mNewsContent=new ArrayList<String>();  //存放新闻解析过的内容
    private ArrayList<String> mNewsImage=new ArrayList<String>();  //存放图片
    private ArrayList<String> mNewsTalk=new ArrayList<String>();  //存放评论内容
    private ArrayList<String> mNewsFrom=new ArrayList<String>();  //存放新闻来源
    private ArrayList<String> result=null;
    private String url=null;
    private String content_text_1=null;
    private Handler handler;
    private ArrayList<String> mNewsUrl2=new ArrayList<String>();
    private ArrayList<String> mNewsContent2=new ArrayList<String>();

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication=this;
    }

    //url列表
    public ArrayList<String> getmNewsUrl(){
        SharedPreferences sharedPreferences3=getSharedPreferences("config_5",MODE_PRIVATE);
        mNewsUrl.clear();

        int size=sharedPreferences3.getInt("title_list_size",0);
        for(int i=0;i<size;i++)
        {
            mNewsUrl.add(sharedPreferences3.getString("url_list_"+i,null));
        }
        return mNewsUrl;
    }

    //title列表
    public ArrayList<String> getmNewsTitle(){
        SharedPreferences sharedPreferences3=getSharedPreferences("config_5",MODE_PRIVATE);
        mNewsTitle.clear();

        int size=sharedPreferences3.getInt("title_list_size",0);
        for(int i=0;i<size;i++)
        {
            mNewsTitle.add(sharedPreferences3.getString("title_list_"+i,null));
        }
        return mNewsTitle;
    }

    //image列表
    public ArrayList<String> getmNewsImage(){
        SharedPreferences sharedPreferences3=getSharedPreferences("config_5",MODE_PRIVATE);
        mNewsImage.clear();

        int size=sharedPreferences3.getInt("title_list_size",0);
        for(int i=0;i<size;i++)
        {
            mNewsImage.add(sharedPreferences3.getString("image_list_"+i,null));
        }
        return mNewsImage;
    }

    //talk列表
    public ArrayList<String> getmNewsTalk() {
        SharedPreferences sharedPreferences3=getSharedPreferences("config_5",MODE_PRIVATE);
        mNewsTalk.clear();

        int size=sharedPreferences3.getInt("title_list_size",0);
        for(int i=0;i<size;i++)
        {
            mNewsTalk.add(sharedPreferences3.getString("talk_list_"+i,null));
        }
        return mNewsTalk;
    }

    //from列表
    public ArrayList<String> getmNewsFrom() {
        SharedPreferences sharedPreferences3=getSharedPreferences("config_5",MODE_PRIVATE);
        mNewsFrom.clear();

        int size=sharedPreferences3.getInt("title_list_size",0);
        for(int i=0;i<size;i++)
        {
            mNewsFrom.add(sharedPreferences3.getString("from_list_"+i,null));
        }
        return mNewsFrom;
    }
}
