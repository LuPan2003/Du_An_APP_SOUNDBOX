package com.example.soundbox_du_an_md31.DbHepler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.Service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class SongDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "song_database";
    public static final String TABLE_SONGS = "songs";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_COUNT = "count";
    public static final String KEY_GENRE = "genre";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LATEST = "latest";
    public static final String KEY_URL = "url";


    public SongDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng songs trong cơ sở dữ liệu
        String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_URL + " TEXT,"
                + KEY_ARTIST + " TEXT,"
                + KEY_LATEST + " BOOLEAN,"
                + KEY_GENRE + " TEXT,"
                + KEY_COUNT + " INTEGER"
                 + ")";
        db.execSQL(CREATE_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Xóa bảng cũ nếu đã tồn tại và tạo lại
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    // Phương thức để thêm một bài hát vào cơ sở dữ liệu

}
