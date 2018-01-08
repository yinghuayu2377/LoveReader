package com.example.lenovo.lovereader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by lenovo on 2016/12/26.
 */
public class UpdateHtmlContent extends Activity {
    private LinearLayout htmlcontent_layout_2;
    private ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htmlcontent_layout_2);

        htmlcontent_layout_2=(LinearLayout)findViewById(R.id.htmlcontent_layout_2);   //这个代表的是htmlcontent_layout

        image=(ImageView)findViewById(R.id.return_select_12);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("config_6",MODE_PRIVATE);
        String str=sharedPreferences.getString("update_content","无");
        String title=sharedPreferences.getString("update_title","无");
        TextView tv=(TextView)findViewById(R.id.text_content_2);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());  //内容过多时加滚动条
        tv.setText(str);
        TextView tv_title=(TextView)findViewById(R.id.title_text_12);
        tv_title.setText(title);

        //设置文字大小
        SharedPreferences sharedPreferences1 = getSharedPreferences("config_3",MODE_PRIVATE);
        int text_size_2= Integer.parseInt(sharedPreferences1.getString("text_size","18"));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,text_size_2);
        //设置背景颜色
        SharedPreferences sharedPreferences2 = getSharedPreferences("config_4",MODE_PRIVATE);
        int night_type_2= Integer.parseInt(sharedPreferences2.getString("night_type","0"));
        if(night_type_2==0){
            htmlcontent_layout_2.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(night_type_2==1){
            htmlcontent_layout_2.setBackgroundColor(Color.parseColor("#aaaaaa"));
        }

    }
}
