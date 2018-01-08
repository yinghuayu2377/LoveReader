package com.example.lenovo.lovereader;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.lenovo.lovereader.DB.myDB;

/**
 * Created by lenovo on 2016/12/26.
 */
public class UpdateContent extends Activity{

    private Cursor cursor;
    SQLiteDatabase db;
    private myDB mydb=new myDB(this);
    private ListView listView;
    private SimpleCursorAdapter adapter;
    private ImageView imageView;
    private MyApplication myApplication;
    private LinearLayout htmlcontent_layout;
    private long _id;
    private static final int ITEM0= Menu.FIRST;

    @Override
    public void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_layout);

        htmlcontent_layout=(LinearLayout)findViewById(R.id.update_layout);

        SharedPreferences sharedPreferences2 = getSharedPreferences("config_4",MODE_PRIVATE);
        int night_type_2= Integer.parseInt(sharedPreferences2.getString("night_type","0"));
        if(night_type_2==0){
            htmlcontent_layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(night_type_2==1){
            htmlcontent_layout.setBackgroundColor(Color.parseColor("#aaaaaa"));
        }

        imageView=(ImageView)findViewById(R.id.return_select_1);
        myApplication=(MyApplication)this.getApplication();
        db=mydb.getWritableDatabase();
        //查询数据库
        cursor=db.rawQuery("select _id,_Title,_Froms,_Talking,_Image,_Content from news_table order by _id",null);
        cursor.moveToFirst();  //要定位到第一列

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView=(ListView)findViewById(R.id.update_list);
        adapter=new SimpleCursorAdapter(this,R.layout.update_list_layout,
                cursor,new String[] {myDB.Title,myDB.Froms,myDB.Talking},
                new int[]{R.id.title_content_1,R.id.from_content_1,R.id.talk_content_1});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                cursor.moveToPosition(i);   //移动cursor到某条记录
                String str=cursor.getString(cursor.getColumnIndex(myDB.Content));
                String title=cursor.getString(cursor.getColumnIndex(myDB.Title));

                SharedPreferences sharedPreferences = getSharedPreferences("config_6",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("update_content", str);
                editor.putString("update_title",title);
                editor.commit();

                Intent intent=new Intent();
                intent.setClass(UpdateContent.this,UpdateHtmlContent.class);
                startActivity(intent);
            }
        });

        this.registerForContextMenu(listView);

    }

    //上下文菜单相关内容
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0,ITEM0,0,"点击删除此条新闻");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo adapterContextMenuInfo= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        _id=adapterContextMenuInfo.id;   //确定点击的是哪个内容对应的id,若直接用点击的id的话只是定位置，内容会混乱

        switch (item.getItemId()){
            case ITEM0:
                Delete();
                break;
        }
        return true;
    }

    //删除指定联系人
    private void Delete() {
        db.delete(myDB.table_name,"_id="+_id,null);
        cursor.requery();  //游标重新查询
    }

    @Override
    protected void onResume() {
        super.onResume();

        setContentView(R.layout.update_layout);

        myApplication=(MyApplication)this.getApplication();
        db=mydb.getWritableDatabase();
        //查询数据库
        cursor=db.rawQuery("select _id,_Title,_Froms,_Talking,_Image,_Content from news_table order by _id",null);
        cursor.moveToFirst();  //要定位到第一列

        htmlcontent_layout=(LinearLayout)findViewById(R.id.update_layout);

        SharedPreferences sharedPreferences2 = getSharedPreferences("config_4",MODE_PRIVATE);
        int night_type_2= Integer.parseInt(sharedPreferences2.getString("night_type","0"));
        if(night_type_2==0){
            htmlcontent_layout.setBackgroundColor(Color.parseColor("#ffffff"));
        }else if(night_type_2==1){
            htmlcontent_layout.setBackgroundColor(Color.parseColor("#aaaaaa"));
        }

        imageView=(ImageView)findViewById(R.id.return_select_1);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView=(ListView)findViewById(R.id.update_list);
        adapter=new SimpleCursorAdapter(this,R.layout.update_list_layout,
                cursor,new String[] {myDB.Title,myDB.Froms,myDB.Talking},
                new int[]{R.id.title_content_1,R.id.from_content_1,R.id.talk_content_1});

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                cursor.moveToPosition(i);   //移动cursor到某条记录
                String str=cursor.getString(cursor.getColumnIndex(myDB.Content));
                String title=cursor.getString(cursor.getColumnIndex(myDB.Title));

                SharedPreferences sharedPreferences = getSharedPreferences("config_6",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("update_content", str);
                editor.putString("update_title",title);
                editor.commit();

                Intent intent=new Intent();
                intent.setClass(UpdateContent.this,UpdateHtmlContent.class);
                startActivity(intent);
            }
        });
        this.registerForContextMenu(listView);
    }

}
