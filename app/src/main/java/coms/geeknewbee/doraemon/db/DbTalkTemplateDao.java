package coms.geeknewbee.doraemon.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import coms.geeknewbee.doraemon.entity.ItemTalkTemplate;

/**
 * Created by chen on 2016/3/17
 * <p/>
 * CRUD
 * <p/>
 * 学说话模板的添加功能。
 */
public class DbTalkTemplateDao {

    private DbOpenHelper mOpenHelper;

    public DbTalkTemplateDao(Context ctx) {
        mOpenHelper = new DbOpenHelper(ctx);
    }

    /**
     * 学说话模板插入数据
     *
     * @return
     */
    public boolean insert(String que, String ans) {
        // insert into talk_templates (question, answer) values ("asd","asdasd")
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //列：值
        ContentValues values = new ContentValues();
        values.put(DbConstants.COLUMN_QUE, que);
        values.put(DbConstants.COLUMN_ANS, ans);
        //参数1：表名
        //返回值：添加行的id
        long result = db.insert(DbConstants.TABLE_NAME, null, values);
        //关闭数据库
        db.close();
        return result != -1;
    }

    /**
     * 查询所有数据
     * @return
     */
    public ArrayList<ItemTalkTemplate> queryAll() {
        // select question, answer from talk_templates;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String[] columns = new String[]{DbConstants.COLUMN_QUE,
                DbConstants.COLUMN_ANS};
        Cursor cursor = db.query(DbConstants.TABLE_NAME, columns, null, null, null, null,
                null);
        ArrayList<ItemTalkTemplate> list = new ArrayList<ItemTalkTemplate>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String que = cursor.getString(0);
                String ans = cursor.getString(1);
                ItemTalkTemplate info = new ItemTalkTemplate(que, ans);
                list.add(info);
            }
            cursor.close();
        }
        db.close();
        return list;
    }
}
