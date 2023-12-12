package com.example.soundbox_du_an_md31.Activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.soundbox_du_an_md31.Adapter.MusicViewPagerAdapter;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.databinding.ActivityPlayMusicBinding;


public class PlayMusicActivity extends BaseActivity implements SensorEventListener {

    private ActivityPlayMusicBinding mActivityPlayMusicBinding;
    private float lastX = 0.0f;
    private static final float SHAKE_THRESHOLD = 5.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityPlayMusicBinding = ActivityPlayMusicBinding.inflate(getLayoutInflater());
        setContentView(mActivityPlayMusicBinding.getRoot());

        initToolbar();
        initUI();
    }

    private void initToolbar() {
        mActivityPlayMusicBinding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        mActivityPlayMusicBinding.toolbar.tvTitle.setText(R.string.music_player);
        mActivityPlayMusicBinding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        mActivityPlayMusicBinding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initUI() {
        MusicViewPagerAdapter musicViewPagerAdapter = new MusicViewPagerAdapter(this);
        mActivityPlayMusicBinding.viewpager2.setAdapter(musicViewPagerAdapter);
        mActivityPlayMusicBinding.indicator3.setViewPager(mActivityPlayMusicBinding.viewpager2);
        mActivityPlayMusicBinding.viewpager2.setCurrentItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
            // Tính toán độ chênh lệch giữa giá trị x hiện tại và giá trị x trước đó
            float deltaX = x - lastX;

            // Lưu giá trị x hiện tại để sử dụng cho lần lắc tiếp theo
            lastX = x;

            // Kiểm tra độ chênh lệch x và thực hiện điều chỉnh âm lượng tương ứng
            if (Math.abs(deltaX) > SHAKE_THRESHOLD) {
                if (deltaX > 0) {
                    // Lắc từ trái qua phải, tăng âm lượng
                    increaseVolume();
                } else {
                    // Lắc từ phải qua trái, giảm âm lượng
                    decreaseVolume();
                }
            }
        }
    }

    private void increaseVolume() {
        // Thực hiện các hành động để tăng âm lượng
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume < maxVolume) {
            audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    private void decreaseVolume() {
        // Thực hiện các hành động để giảm âm lượng (nếu cần thiết)
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume > 0) {
            audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Xử lý sự kiện thay đổi độ chính xác của cảm biến
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // Xử lý theo độ chính xác mới của cảm biến gia tốc
            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    Log.d("Accuracy", "Unreliable");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    Log.d("Accuracy", "Low");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    Log.d("Accuracy", "Medium");
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    Log.d("Accuracy", "High");
                    break;
            }
        }
    }
}