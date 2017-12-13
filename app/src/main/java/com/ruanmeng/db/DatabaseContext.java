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
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * 项目名称：Billion_Health
 * 创建人：小卷毛
 * 创建时间：2017-01-12 15:44
 * <p>
 * 用于支持对存储在SD卡上的数据库的访问
 */
public class DatabaseContext extends ContextWrapper {

    private Context mContext;
    private String dirName;

    /**
     * 构造函数
     *
     * @param base 上下文环境
     */
    public DatabaseContext(Context base) {
        super(base);
        this.mContext = base;
    }

    /**
     * 构造函数
     *
     * @param base    上下文环境
     * @param dirName 文件夹路径
     */
    public DatabaseContext(Context base, String dirName) {
        super(base);
        this.mContext = base;
        this.dirName = dirName;
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     *
     * @param name 文件名称
     */
    @Override
    public File getDatabasePath(String name) {
        //判断是否存在sd卡
        boolean sdExist = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (!sdExist) {// 如果不存在,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
            return null;
        } else {
            //如果存在，获取sd卡路径
            String dbDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            dbDir += "/" + ((dirName == null || "".equals(dirName)) ? mContext.getPackageName() : dirName); //数据库所在目录
            String dbPath = dbDir + "/" + name; //数据库路径
            //判断目录是否存在，不存在则创建该目录
            File dirFile = new File(dbDir);
            if (!dirFile.exists()) dirFile.mkdirs();

            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建该文件　
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                try {
                    isFileCreateSuccess = dbFile.createNewFile(); //创建文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                isFileCreateSuccess = true;

            //返回数据库文件对象
            return isFileCreateSuccess ? dbFile : null;
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name,
                                               int mode,
                                               SQLiteDatabase.CursorFactory factory) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @see ContextWrapper#openOrCreateDatabase(String, int, SQLiteDatabase.CursorFactory, DatabaseErrorHandler)
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name,
                                               int mode,
                                               SQLiteDatabase.CursorFactory factory,
                                               DatabaseErrorHandler errorHandler) {
        return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
    }
}
