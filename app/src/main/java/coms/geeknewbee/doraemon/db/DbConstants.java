package coms.geeknewbee.doraemon.db;

/**
 * Created by chen on 2016/3/17
 */
public interface DbConstants {
    String DB_NAME = "Talk_Template.db";
    int VERSION = 1;
    String TABLE_NAME = "talk_templates";
    String COLUMN_ID = "_id";
    String COLUMN_QUE = "question";
    String COLUMN_ANS = "answer";

    String SQL_CREATE_TABLE = "create table " + TABLE_NAME + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_QUE + " varchar(50) , "
            + COLUMN_ANS + " varchar(50)"
            + ") ";
}
