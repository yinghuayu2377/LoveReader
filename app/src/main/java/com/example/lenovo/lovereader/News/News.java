package com.example.lenovo.lovereader.News;

import android.graphics.Bitmap;

import java.net.URL;

/**
 * Created by lenovo on 2016/12/23.
 */
public class News {
    private String url;  //url
    private String title;
    private Bitmap image;
    private String talk;  //评论数
    private String from;  //来源

    public News(String url, String title, Bitmap image, String talk, String from)
    {
        this.url=url;
        this.title=title;
        this.image=image;
        this.talk=talk;
        this.from=from;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
