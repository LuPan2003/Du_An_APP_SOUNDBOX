package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundbox_du_an_md31.R;
import com.google.firebase.database.DatabaseReference;

public class volumeSeekBar extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private AudioManager audioManager;

    private SeekBar volumeSeekBar;
    private SeekBar bassSeekBar;
    private SeekBar trebleSeekBar;
    private float lastAcceleration = 0.0f;
    private Equalizer equalizer;
    private MediaPlayer mediaPlayer;
    private DatabaseReference databaseReference;
    private ImageView backleft;
    private static final short BASS_BAND_INDEX = 0;
    private static final int BASS_MIN_LEVEL = 0;
    private static final int BASS_MAX_LEVEL = 100;

    private static final int TREBLE_MIN_LEVEL = -1000;
    private static final int TREBLE_MAX_LEVEL = 1000;
    private static final short TREBLE_BAND_INDEX = 1;
    private SeekBar soundBalanceSeekBar;
    private float currentBalance;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_seek_bar);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        volumeSeekBar = findViewById(R.id.volume_seek_bar);
        bassSeekBar = findViewById(R.id.bass_seek_bar);
        trebleSeekBar = findViewById(R.id.treble_seek_bar);
        backleft = findViewById(R.id.back_left);
        soundBalanceSeekBar = findViewById(R.id.sound_balance);
        soundBalanceSeekBar.setMax(100);
        // Đọc giá trị từ SharedPreferences và thiết lập cho soundBalanceSeekBar
        currentBalance = getBalanceFromPreferences();
        soundBalanceSeekBar.setProgress((int) (currentBalance * 100));
        backleft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        soundBalanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentBalance = progress / 100f;
                adjustBalance();
                // Lưu giá trị mới vào SharedPreferences khi giá trị thay đổi
                saveBalanceToPreferences(currentBalance);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Không cần xử lý
            }
        });


        if (!initializeEqualizer()) {
            // Xử lý khi equalizer không khả dụng
            return;
        }

        setupBassSeekBar();
        setupTrebleSeekBar();

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private boolean initializeEqualizer() {
        equalizer = new Equalizer(0, 0);
        if (equalizer.getNumberOfBands() == 0) {
            // Thiết bị không hỗ trợ equalizer
            // Xử lý tương ứng ở đây
            return false;
        }
        equalizer.setEnabled(true);
        return true;
    }

    private void setupBassSeekBar() {
        if (equalizer != null) {
            bassSeekBar.setMax(BASS_MAX_LEVEL - BASS_MIN_LEVEL);
            bassSeekBar.setProgress(equalizer.getBandLevel(BASS_BAND_INDEX) - BASS_MIN_LEVEL);

            bassSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int level = progress + BASS_MIN_LEVEL;
                    equalizer.setBandLevel(BASS_BAND_INDEX, (short) level);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    private void setupTrebleSeekBar() {
        if (equalizer != null) {
            int trebleMinLevel = equalizer.getBandLevelRange()[0];
            int trebleMaxLevel = equalizer.getBandLevelRange()[1];
            int trebleRange = trebleMaxLevel - trebleMinLevel;

            trebleSeekBar.setMax(trebleRange);

            // Chuyển đổi giá trị từ int sang short ở đây
            short trebleLevel = (short) equalizer.getBandLevel(TREBLE_BAND_INDEX);
            trebleSeekBar.setProgress(trebleLevel - trebleMinLevel);

            trebleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int level = progress + trebleMinLevel;
                    equalizer.setBandLevel(TREBLE_BAND_INDEX, (short) level);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    private void adjustBalance() {
        // Xoay vòng giữa các giá trị (0.2, 0.5, 0.8)
        currentBalance = (currentBalance + 0.3f) % 1.0f;

        Log.d("AdjustBalance", "Adjusting balance to: " + currentBalance);

        if (equalizer != null) {
            short bands = equalizer.getNumberOfBands();

            for (short i = 0; i < bands; i++) {
                int level = (int) (currentBalance * equalizer.getBandLevelRange()[1]);
                equalizer.setBandLevel(i, (short) level);
            }

            // Lưu giá trị cân bằng mới vào SharedPreferences
            saveBalanceToPreferences(currentBalance);


            // Hiển thị giá trị cân bằng âm thanh thông qua Toast
            showToast("Cân bằng âm thanh được điều chỉnh thành: " + currentBalance);
        }
    }
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        // Khôi phục giá trị khi vào lại
        currentBalance = getBalanceFromPreferences();
        adjustBalance(); // Đảm bảo cập nhật cân bằng âm thanh

        // Đặt lại giá trị cân bằng cho equalizer khi ứng dụng bắt đầu
        resetEqualizerBalance();
    }

    private void resetEqualizerBalance() {
        if (equalizer != null) {
            short bands = equalizer.getNumberOfBands();
            for (short i = 0; i < bands; i++) {
                int level = (int) (currentBalance * equalizer.getBandLevelRange()[1]);
                equalizer.setBandLevel(i, (short) level);
            }
        }
    }
    private float getBalanceFromPreferences() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        return preferences.getFloat("balance", 0.0f);
    }
    @Override
    protected void onPause() {
        saveBalanceToPreferences(currentBalance);
        sensorManager.unregisterListener(this);
        super.onPause();
    }

    private void saveBalanceToPreferences(float balance) {
        // Lưu giá trị vào SharedPreferences hoặc cơ sở dữ liệu nhỏ
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat("balance", balance);
        editor.apply();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Xử lý sự kiện khi cảm biến gia tốc thay đổi
    }
}
