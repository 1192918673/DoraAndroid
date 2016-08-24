package coms.geeknewbee.doraemon.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by chen on 2016/3/17
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public DbOpenHelper(Context context) {
        super(context, DbConstants.DB_NAME, null, DbConstants.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //数据库创建时调用，初始化操作 建表 插入数据
        db.execSQL(DbConstants.SQL_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //数据库版本升级时调用
    }
}
