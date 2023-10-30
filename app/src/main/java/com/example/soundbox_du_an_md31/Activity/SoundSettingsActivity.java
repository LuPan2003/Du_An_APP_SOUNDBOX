package com.example.soundbox_du_an_md31.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.R;

import java.util.ArrayList;
import java.util.List;

public class SoundSettingsActivity extends AppCompatActivity {
    private SeekBar seekBarVolume;
    private RadioGroup radioGroupQuality;
    private MediaPlayer soundPlayer;
    private LinearLayout linearLayoutAdvanced;
    private TextView textViewAdvanced;
    private SeekBar seekBarEqualizer;
    private Switch switchNoGap;
    List<Song> songList = new ArrayList<>();
    private AudioManager audioManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_settings);

        soundPlayer = new MediaPlayer();
        soundPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        findViews();
        setListeners();
    }
    private void findViews() {
        seekBarVolume = findViewById(R.id.seekbar_volume);
        radioGroupQuality = findViewById(R.id.radio_group_quality);
        linearLayoutAdvanced = findViewById(R.id.linear_layout_advanced);
        textViewAdvanced = findViewById(R.id.text_view_advanced);
        seekBarEqualizer = findViewById(R.id.seekbarequalizer);
        switchNoGap = findViewById(R.id.switch_no_gap);
    }
    private void setListeners() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Set the volume of the sound player
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Lưu giá trị âm lượng
                SharedPreferences sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("volume", seekBarVolume.getProgress());
                editor.apply();
            }
        });


        // Đặt sự kiện cho RadioGroup chất lượng âm thanh
        radioGroupQuality.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Xử lý thay đổi chất lượng âm thanh ở đây
                if (checkedId == R.id.radio_button_low) {
                    // Chọn chất lượng thấp
                } else if (checkedId == R.id.radio_button_medium) {
                    // Chọn chất lượng trung bình
                } else if (checkedId == R.id.radio_button_high) {
                    // Chọn chất lượng cao
                }
            }
        });


        // Đặt sự kiện cho SeekBar Equalizer
//        seekBarEqualizer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                // Xử lý thay đổi cài đặt Equalizer ở đây
//                updateEqualizerSettings(progress, songList.get(progress));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // Đoạn mã xử lý khi bắt đầu giữ SeekBar
//                // Dừng phát âm thanh (nếu đang phát)
//                if (soundPlayer.isPlaying()) {
//                    soundPlayer.pause();
//                }
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                if (!soundPlayer.isPlaying()) {
//                    soundPlayer.start();
//                }
//            }
//        });
        // Phương thức để xử lý cài đặt Equalizer dựa trên giá trị progress

        // Đặt sự kiện cho Switch không khoảng cách
        switchNoGap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Xử lý thay đổi cài đặt không khoảng cách ở đây
            }
        });
    }

//    private void updateEqualizerSettings(int progress, Song song) {
//        if (song != null) {
//            Equalizer equalizer = new Equalizer(0, soundPlayer.getAudioSessionId());
//            equalizer.setEnabled(true);
//            // Điều chỉnh cân bằng âm thanh
//            int balanceSetting = (int) song.getBalance(); // Giá trị balance từ đối tượng Song
//            // Tính toán băng tần trái và băng tần phải
//            int numBands = equalizer.getNumberOfBands();
//            short leftBand = 0;
//            short rightBand = (short) (numBands - 1);
//            if (balanceSetting > 0) {
//                equalizer.setBandLevel(leftBand, (short) 0);
//                equalizer.setBandLevel(rightBand, (short) balanceSetting);
//            } else if (balanceSetting < 0) {
//                equalizer.setBandLevel(leftBand, (short) -balanceSetting);
//                equalizer.setBandLevel(rightBand, (short) 0);
//            } else {
//                equalizer.setBandLevel(leftBand, (short) 0);
//                equalizer.setBandLevel(rightBand, (short) 0);
//            }
//            // Bạn cũng có thể điều chỉnh các băng tần Equalizer khác ở đây theo nhu cầu
//            // Đừng quên tắt Equalizer khi bạn đã hoàn thành
//            equalizer.release();
//
//        }
//    }
}