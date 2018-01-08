package com.example.lenovo.lovereader.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lenovo on 2016/12/26.
 */
public class myDB extends SQLiteOpenHelper {
    public static String Title="_Title";
    public static String Froms="_Froms";
    public static String Talking="_Talking";
    public static String Image="_Image";
    public static String Content="_Content";
    public static String table_name="news_table";
    private static final String database_name="lovereader_1.db";

    public myDB(Context context) {
        super(context,database_name,null,1);  //创建数据库
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sql = "create table if not exists news_table(_id integer primary key autoincrement," +
                "_Title text,_Froms text,_Talking integer,_Image text,_Content text)";   //创建一张表,_id会自增
        sqLiteDatabase.execSQL(sql); //执行sql语句
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists constants");  //删除表
        onCreate(sqLiteDatabase);  //并没有更新，只是重新建了一张表
    }
}
