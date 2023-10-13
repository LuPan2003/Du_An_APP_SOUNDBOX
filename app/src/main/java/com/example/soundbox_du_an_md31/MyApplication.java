package com.example.soundbox_du_an_md31;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "channel_music_sounbox_id";
    private static final String CHANNEL_NAME = "channel_music_sounbox_name";
    private FirebaseDatabase mFirebaseDatabase;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    //khởi tạo Firebase App và tạo một kênh thông báo
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance(Constant.FIREBASE_URL);
        createChannelNotification();
    }
    //tạo một kênh thông báo để hiển thị thông báo cho người dùng.
    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_MIN);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    //trả về một tham chiếu đến cơ sở dữ liệu Firebase lưu trữ các bài hát.
    public DatabaseReference getSongsDatabaseReference() {
        return mFirebaseDatabase.getReference("/songs");
    }
    //trả về một tham chiếu đến cơ sở dữ liệu Firebase lưu trữ phản hồi của người dùng.
    public DatabaseReference getFeedbackDatabaseReference() {
        return mFirebaseDatabase.getReference("/feedback");
    }
    //trả về một tham chiếu đến cơ sở dữ liệu Firebase lưu trữ số lượt xem của một bài hát.
    public DatabaseReference getCountViewDatabaseReference(int songId) {
        return FirebaseDatabase.getInstance().getReference("/songs/" + songId + "/count");
    }
}
