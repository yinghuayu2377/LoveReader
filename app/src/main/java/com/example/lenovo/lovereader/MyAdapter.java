package com.example.lenovo.lovereader;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.lovereader.News.News;

import java.util.ArrayList;

/**
 * Created by lenovo on 2016/12/22.
 */
public class MyAdapter extends BaseAdapter {   //自定义的adapter
    LayoutInflater inflater;
    private ArrayList<News> listItem;
    private int text_size;

    public MyAdapter(Context context, ArrayList<News> listItems,int text_size)
    {
        inflater=LayoutInflater.from(context);  //所使用的布局是哪一个
        this.listItem=listItems;
        this.text_size=text_size;
    }

    //返回listItem的数量
    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //将写好的listcontent_layout.xml文件转化为一个view
        View view1=inflater.inflate(R.layout.listcontent_layout,null);
        ImageView image=(ImageView)view1.findViewById(R.id.list_image);
        TextView tv_title=(TextView)view1.findViewById(R.id.title_content);
        TextView tv_from=(TextView)view1.findViewById(R.id.from_content);
        TextView tv_talk=(TextView)view1.findViewById(R.id.talk_content);

        tv_title.setText(listItem.get(i).getTitle());
        tv_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP,text_size);  //设置字体大小
        tv_from.setText(listItem.get(i).getFrom());
        tv_talk.setText("评论数 "+listItem.get(i).getTalk());
        image.setImageBitmap(listItem.get(i).getImage());

        return view1;
    }
}
