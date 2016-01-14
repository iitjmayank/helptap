package com.helptap.interview.part1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by maagarwa on 1/14/2016.
 */
public class NewsFeedDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "newsfeed.db";

    String INDEX = "id";
    String TABLE = "news";
    String URL = "url";
    String TITLE = "title";
    String CONTENT = "content";
    String LINK = "link";

    public NewsFeedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "create table if not exists "+TABLE+" ("+INDEX+" Integer PRIMARY KEY AUTOINCREMENT,"+URL+" text, "+CONTENT+" text, "+TITLE+" text, "+LINK+" text)";
        db.execSQL(query);
        //db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String query = "drop table if exists "+ TABLE;
        db.execSQL(query);
        onCreate(db);
    }

    public boolean insertNewsFeed(List<NewsFeedModel> newsFeeds) {
        SQLiteDatabase database = getWritableDatabase();
        long id = 1;

        for (NewsFeedModel model : newsFeeds) {
            ContentValues values = new ContentValues();
            values.put(TITLE,model.title);
            values.put(CONTENT,model.contentSnippet);
            values.put(LINK,model.link);
            values.put(URL,model.url);
            id = database.insert(TABLE, null, values);
            if (id < -1) {
                break;
            }
        }
        database.close();
        return (id != -1);
    }

    public NewsFeedModel getNewsFeed(int position) {
        SQLiteDatabase db = getReadableDatabase();

        String query = "select * from " + TABLE + " where " + INDEX + "=" + position;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        db.close();
        NewsFeedModel model = new NewsFeedModel();
        model.url = cursor.getString(cursor.getColumnIndex(URL));
        model.contentSnippet = cursor.getString(cursor.getColumnIndex(CONTENT));
        model.link = cursor.getString(cursor.getColumnIndex(LINK));
        model.title = cursor.getString(cursor.getColumnIndex(TITLE));
        cursor.close();
        return model;
    }

    public long getTableSize() {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE);
    }
}
