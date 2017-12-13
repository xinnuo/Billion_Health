/**
 * created by 小卷毛, 2017/01/12
 * Copyright (c) 2017, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.ruanmeng.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.ruanmeng.share.Const;

import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.UserInfo;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-01-12 15:04
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private String TAG = this.getClass().getSimpleName();

    private static String TABLE = "message.db";
    private static int VERSION = 1;

    private static DatabaseHelper dataHelper;

    /**
     * 数据库的构造方法  用来定义数据库的名称  数据库查询的结果集 数据库的版本
     */
    public DatabaseHelper(Context context) {
        super(context, TABLE, null, VERSION);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public static DatabaseHelper getInstance(Context context) {
        if (dataHelper == null) {
            //创建DatabaseHelper辅助类对象
            DatabaseContext dbContext = new DatabaseContext(context, Const.SAVE_FILE);
            dataHelper = new DatabaseHelper(dbContext);
        }
        return dataHelper;
    }

    /**
     * 当数据库首次创建时执行该方法，一般将创建表等初始化操作放在该方法中执行.
     * 重写onCreate方法，调用execSQL方法创建表
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists message_info("
                + "id integer primary key autoincrement,"
                + "userId varchar,"
                + "userHead varchar,"
                + "nikeName varchar,"
                + "userType varchar)");
    }

    /**
     * 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * 添加一条记录到数据库
     */
    public void add(String userId,
                    String userHead,
                    String nikeName,
                    String userType) {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        db.execSQL(
                "insert into message_info(userId,userHead,nikeName,userType) values(?,?,?,?)",
                new String[]{userId, userHead, nikeName, userType});
        db.close();
    }

    /**
     * 判断数据库是否存在 userId 的数据
     */
    public boolean check(String userId) {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from message_info where userId=?", new String[]{userId});
        boolean result = cursor.moveToNext();
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 修改一条记录
     */
    public void update(String userId,
                       String userHead,
                       String nikeName,
                       String userType) {
        SQLiteDatabase db = dataHelper.getWritableDatabase();
        db.execSQL(
                "update message_info set userHead=?,nikeName=?,userType=? where userId=?",
                new String[]{userHead, nikeName, userType, userId});
        db.close();
    }

    /**
     * 查询数据库中 userId 的数据
     */
    public UserData checkUser(String userId) {
        String userHead = "";
        String nikeName = "";
        String userType = "";

        SQLiteDatabase db = dataHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from message_info where userId=?", new String[]{userId});
        if (cursor.moveToNext()) {
            userHead = cursor.getString(cursor.getColumnIndex("userHead"));
            nikeName = cursor.getString(cursor.getColumnIndex("nikeName"));
            userType = cursor.getString(cursor.getColumnIndex("userType"));
        }
        cursor.close();
        db.close();
        return new UserData(userType, new UserInfo(userId, nikeName, Uri.parse(userHead)));
    }

    /**
     * 查询数据库中所有的数据
     */
    public List<UserInfo> checkAll() {
        ArrayList<UserInfo>  list = new ArrayList<>();

        SQLiteDatabase db = dataHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from message_info", null);
        while (cursor.moveToNext()) {
            String userId = cursor.getString(cursor.getColumnIndex("userId"));
            String nikeName = cursor.getString(cursor.getColumnIndex("nikeName"));
            String userHead = cursor.getString(cursor.getColumnIndex("userHead"));

            list.add(new UserInfo(userId, nikeName, Uri.parse(userHead)));
        }
        cursor.close();
        db.close();
        return list;
    }
}
