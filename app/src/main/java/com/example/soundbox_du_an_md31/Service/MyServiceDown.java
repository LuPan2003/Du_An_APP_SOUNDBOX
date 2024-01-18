package com.example.soundbox_du_an_md31.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.Model.SongDown;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyServiceDown extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{
    public static boolean isPlaying;
    public static List<SongDown> mListSongPlaying1;
    public static int mSongPosition;
    public static MediaPlayer mPlayer;
    public static boolean  isPlay ;
    public static int mLengthSong;
    public static int mAction = -1;
    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //khởi tạo đối tượng MediaPlayer, được sử dụng để phát nhạc.
    @Override
    public void onCreate() {
        super.onCreate();
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }


    }
    //Phương thức này được gọi khi dịch vụ được khởi động.
// Phương thức này nhận một đối số là Intent,
// chứa các thông tin về cách dịch vụ được khởi động.
// Phương thức này cũng trả về một giá trị int, cho biết dịch vụ có nên tiếp tục chạy hay không.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constant.MUSIC_ACTION)) {
                mAction = bundle.getInt(Constant.MUSIC_ACTION);
            }
            if (bundle.containsKey(Constant.SONG_POSITION)) {
                mSongPosition = bundle.getInt(Constant.SONG_POSITION);
            }

            handleActionMusic(mAction);
        }


        return START_NOT_STICKY;
    }

    // Phương thức này xử lý các hành động khác nhau của dịch vụ,
    // như phát, tạm dừng, tiếp tục,
    // bài hát trước, bài hát tiếp theo và hủy thông báo.
    private void handleActionMusic(int action) {
        switch (action) {
            case Constant.PLAY:
                playSong();
                break;

            case Constant.PREVIOUS:
                prevSong();
                break;

            case Constant.NEXT:
                nextSong();
                break;

            case Constant.PAUSE:
                pauseSong();
                break;

            case Constant.RESUME:
                resumeSong();
                break;

            case Constant.CANNEL_NOTIFICATION:
                cannelNotification();
                break;

            default:
                break;
        }
    }
    //    Phương thức này phát bài hát hiện tại.
    private void playSong() {
        String songUrl = mListSongPlaying1.get(mSongPosition).getUrl();
        Log.d("nhacdown", "playSong: " + songUrl);
        if (!StringUtil.isEmpty(songUrl)) {
            playMediaPlayer(songUrl);
        }
    }
    //    Phương thức này tạm dừng bài hát hiện tại.
    private void pauseSong() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPlaying = false;
            sendMusicNotification();
            sendBroadcastChangeListener();
        }
    }
    //    Phương thức này hủy thông báo của dịch vụ.
    private void cannelNotification() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPlaying = false;
        }
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        sendBroadcastChangeListener();
        stopSelf();
    }
    //Phương thức này tiếp tục phát bài hát hiện tại.
    private void resumeSong() {
        if (mPlayer != null) {
            mPlayer.start();
            isPlaying = true;
            sendMusicNotification();
            sendBroadcastChangeListener();
        }
    }


    //    Phương thức này phát bài hát trước.
    public void prevSong() {
        if (mListSongPlaying1.size() > 1) {
            if (mSongPosition > 0) {
                mSongPosition--;
            } else {
                mSongPosition = mListSongPlaying1.size() - 1;
            }
        } else {
            if (mSongPosition == 0) {
                mSongPosition = mListSongPlaying1.size() - 1;
            }
        }
        sendMusicNotification();
        sendBroadcastChangeListener();
        playSong();
    }

    //Phương thức này phát bài hát tiếp theo.
    private void nextSong() {
        if (mListSongPlaying1.size() > 1 && mSongPosition < mListSongPlaying1.size() - 1) {
            mSongPosition++;
        } else {
            mSongPosition = 0;
        }
        sendMusicNotification();
        sendBroadcastChangeListener();
        playSong();
    }

    //    Phương thức này phát bài hát được truyền vào dưới dạng đối số.
    public void playMediaPlayer(String songUrl) {
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.setDataSource(songUrl);
            mPlayer.prepareAsync();
            initControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //    Phương thức này thiết lập các listener cho trình phát nhạc,
//    chẳng hạn như listener sự kiện hoàn thành bài hát và listener sự kiện chuẩn bị phát bài hát.
    public void initControl() {
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
    }
    //Phương thức này gửi thông báo cho người dùng về bài hát hiện đang phát.
    private void sendMusicNotification() {
        SongDown song = mListSongPlaying1.get(mSongPosition);

        int pendingFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        } else {
            pendingFlag = PendingIntent.FLAG_UPDATE_CURRENT;
        }
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingFlag);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_push_notification_music);
        remoteViews.setTextViewText(R.id.tv_song_name, song.getTitle());
        remoteViews.setTextViewText(R.id.tv_artist, song.getArtist());

        // Set listener
        remoteViews.setOnClickPendingIntent(R.id.img_previous, GlobalFuntion.openMusicReceiver(this, Constant.PREVIOUS));
        remoteViews.setOnClickPendingIntent(R.id.img_next, GlobalFuntion.openMusicReceiver(this, Constant.NEXT));
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_pause_gray);
            remoteViews.setOnClickPendingIntent(R.id.img_play, GlobalFuntion.openMusicReceiver(this, Constant.PAUSE));
        } else {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_play_gray);
            remoteViews.setOnClickPendingIntent(R.id.img_play, GlobalFuntion.openMusicReceiver(this, Constant.RESUME));
        }
        remoteViews.setOnClickPendingIntent(R.id.img_close, GlobalFuntion.openMusicReceiver(this, Constant.CANNEL_NOTIFICATION));

        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_small_push_notification)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification);
    }

    public static void clearListSongPlaying() {
        if (mListSongPlaying1 != null) {
            mListSongPlaying1.clear();
        } else {
            mListSongPlaying1 = new ArrayList<>();
        }
    }

    //Phương thức này được gọi khi bài hát hiện tại kết thúc. Phương thức này sẽ phát bài hát tiếp theo.
    @Override
    public void onCompletion(MediaPlayer mp) {
//        showAd();
        mAction = Constant.NEXT;
        nextSong();
    }
    //Phương thức này được gọi khi bài hát hiện tại đã được chuẩn bị để phát. Phương thức này sẽ phát bài hát hiện tại.
    @Override
    public void onPrepared(MediaPlayer mp) {
        mLengthSong = mPlayer.getDuration();
        mp.start();
        isPlaying = true;
        mAction = Constant.PLAY;
        sendMusicNotification();
        sendBroadcastChangeListener();
        // Lưu thời điểm bắt đầu nghe bài hát vào Firebase
    }
    private String getCurrentUserId() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }

    //    Phương thức này gửi thông báo cho các đối tượng đang lắng nghe sự kiện thay đổi bài hát.
    private void sendBroadcastChangeListener() {
        Intent intent = new Intent(Constant.CHANGE_LISTENER);
        intent.putExtra(Constant.MUSIC_ACTION, mAction);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    //Phương thức này tăng số lượt xem của bài hát hiện đang phát.



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
