package com.example.soundbox_du_an_md31.Activity;

import android.os.Bundle;
import android.view.View;

import com.example.soundbox_du_an_md31.Adapter.MusicViewPagerAdapter;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.databinding.ActivityPlayMusicBinding;


public class PlayMusicActivity extends BaseActivity {

    private ActivityPlayMusicBinding mActivityPlayMusicBinding;

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
}