package com.example.soundbox_du_an_md31.Dao;



import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_ARTIST;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_COUNT;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_GENRE;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_ID;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_IMAGE;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_LATEST;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_TITLE;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.KEY_URL;
import static com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper.TABLE_SONGS;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.soundbox_du_an_md31.DbHepler.SongDatabaseHelper;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.Model.SongDown;

import java.util.ArrayList;
import java.util.List;

public class Dao {

    SongDatabaseHelper mMySqlHeper;
    SQLiteDatabase mSQLiteDatabase;
    public Dao(Context context){
        mMySqlHeper = new SongDatabaseHelper(context);
    }

    public boolean themSong(SongDown mode) {
        this.mSQLiteDatabase = mMySqlHeper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, mode.getIdSong());
        values.put(KEY_TITLE, mode.getTitle());
        values.put(KEY_IMAGE, mode.getImage());

        values.put(KEY_URL, mode.getUrl());

        values.put(KEY_ARTIST, mode.getArtist());
        values.put(KEY_LATEST, mode.isLatest());

        values.put(KEY_GENRE, mode.getGenre());

        values.put(KEY_COUNT, mode.getCount());
        long result = this.mSQLiteDatabase.insert(TABLE_SONGS, null, values);
        if (result <= 0) {
            return false;
        }
        return true;
    }
    public List<SongDown> getListSongDown(){
        List<SongDown> lisst = new ArrayList<SongDown>();
        this.mSQLiteDatabase = mMySqlHeper.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_SONGS;

        Cursor cursor = this.mSQLiteDatabase.rawQuery(sql, null);
        SongDown user ;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String id = cursor.getString(0);
                String title = cursor.getString(1);
                String image = cursor.getString(2);
                String url = cursor.getString(3);
                String artist = cursor.getString(4);
                boolean latest = Boolean.parseBoolean(cursor.getString(5));
                String genre = cursor.getString(6);
                int count = Integer.parseInt(cursor.getString(7));
                user = new SongDown(id , title,image,url,artist,latest,genre,count);
                lisst.add(user);
                cursor.moveToNext();
            }
        }
        cursor.close();
        this.mSQLiteDatabase.close();
        return lisst;
    }

}
