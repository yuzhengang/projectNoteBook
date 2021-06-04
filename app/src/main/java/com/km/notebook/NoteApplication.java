package com.km.notebook;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

public class NoteApplication  extends Application {
    private static DaoSession daoSession;
    public static Context mContext;

    public static int num;

    @Override
    public void onCreate() {
        super.onCreate();
        //配置数据库
        setupDatabase();
        mContext=this;
        CrashReport.UserStrategy  strategy=new CrashReport.UserStrategy(this);
        strategy.setAppVersion("1.0.0");
        strategy.setAppPackageName("com.km.notebook");
        CrashReport.initCrashReport(this,"17cf11325c",false,strategy);
    }



    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库shop.db
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "note.db", null);
        //获取可写数据库
        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(db);
        //获取dao对象管理者
        daoSession = daoMaster.newSession();
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
