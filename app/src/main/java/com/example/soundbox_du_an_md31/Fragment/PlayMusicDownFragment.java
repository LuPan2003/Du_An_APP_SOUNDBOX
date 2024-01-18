package com.example.soundbox_du_an_md31.Fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import com.example.soundbox_du_an_md31.Dao.Dao;
import com.example.soundbox_du_an_md31.Model.SongDown;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.Service.MyServiceDown;
import com.example.soundbox_du_an_md31.databinding.FragmentPlayMusicDownBinding;
import com.example.soundbox_du_an_md31.utils.AppUtil;
import com.example.soundbox_du_an_md31.utils.GlideUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

@SuppressLint("NonConstantResourceId")


public class PlayMusicDownFragment extends Fragment  {

    private static final String AUDIO_URL = "";
    private FragmentPlayMusicDownBinding mFragmentPlaySongBinding;
    private Timer mTimer;
    private int mAction;
    private String currentSongId;
    private Dao mDao;
    private List<SongDown> mSongDown;

    private BroadcastReceiver musicShutdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Nhận thông báo khi nhạc tắt và cập nhật giao diện người dùng
            updateUIOnMusicShutdown();
        }
    };
    // Phương thức để cập nhật giao diện người dùng
    private void updateUIOnMusicShutdown() {
        if (mFragmentPlaySongBinding != null) {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaySongBinding = FragmentPlayMusicDownBinding.inflate(inflater, container, false);

        this.mDao = new Dao(getActivity());
        this.mSongDown = this.mDao.getListSongDown();

        mFragmentPlaySongBinding.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAnimationPlayMusic();
                showStatusButtonPlay();
                stopMusic();
                showSeekBar();
            }
        });
        showInforSong();
        initControl();


        downloadAudio();
        // Phát âm thanh từ file đã tải xuống



        return mFragmentPlaySongBinding.getRoot();
    }

    private void initControl() {
        mTimer = new Timer();

        mFragmentPlaySongBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }
    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            MyServiceDown.isPlay = false;
        }

        else{
            downloadAudio();
        }
    }
    private void showStatusButtonPlay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_pause_black);
        else {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }
    public void showSeekBar() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    if (mediaPlayer == null) {
                        return;
                    }
                    mFragmentPlaySongBinding.tvTimeCurrent.setText(AppUtil.getTime(mediaPlayer.getCurrentPosition()));
                    mFragmentPlaySongBinding.tvTimeMax.setText(AppUtil.getTime(mediaPlayer.getDuration()));
                    mFragmentPlaySongBinding.seekbar.setMax(mediaPlayer.getDuration());
                    mFragmentPlaySongBinding.seekbar.setProgress(mediaPlayer.getCurrentPosition());
                });
            }
        }, 0, 1000);
    }





    private void showInforSong() {
        SongDown currentSong = MyServiceDown.mListSongPlaying1.get(MyServiceDown.mSongPosition);
        mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
        mFragmentPlaySongBinding.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mFragmentPlaySongBinding.imgSong);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

    }

    private void startAnimationPlayMusic() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(this).setDuration(15000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(15000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnimationPlayMusic() {
        mFragmentPlaySongBinding.imgSong.animate().cancel();
    }



    MediaPlayer mediaPlayer = new MediaPlayer();


    private void downloadAudio() {
        SongDown currentSong = MyServiceDown.mListSongPlaying1.get(MyServiceDown.mSongPosition);
//        downloadMusicFromUrl(currentSong.getUrl(), currentSong.getTitle());
        File musicFile = new File(getActivity().getExternalFilesDir(null) + "/music/"+currentSong.getTitle()); // Thay đổi đường dẫn và tên tệp nhạc tương ứng
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(musicFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            MyServiceDown.isPlay = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        startAnimationPlayMusic();
        showSeekBar();
    }



    private void playAudio() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    // Phương thức để tạm dừng âm thanh
    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    // Phương thức để dừng âm thanh
    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
//    private void playSong() {
//        mediaPlayer = MediaPlayer.create(getContext(), Uri.parse(localFilePath));
//        mediaPlayer.start();
//    }

    private void downloadMusicFromUrl(String url, String fileName) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream inputStream = null;
                OutputStream outputStream = null;

                try {
                    File directory = new File(getActivity().getExternalFilesDir(null), "music"); // Thay "music" bằng tên thư mục bạn muốn lưu trữ nhạc
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    File file = new File(directory, fileName);

                    inputStream = response.body().byteStream();
                    outputStream = new FileOutputStream(file);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

}