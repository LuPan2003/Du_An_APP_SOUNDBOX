package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Fragment.AllSongsFragment;
import com.example.soundbox_du_an_md31.Fragment.ChangeInformationFragment;
import com.example.soundbox_du_an_md31.Fragment.ChangePasswordFragment;
import com.example.soundbox_du_an_md31.Fragment.FeaturedSongsFragment;
import com.example.soundbox_du_an_md31.Fragment.FeedbackFragment;
import com.example.soundbox_du_an_md31.Fragment.HomeFragment;
import com.example.soundbox_du_an_md31.Fragment.LibraryFragment;
import com.example.soundbox_du_an_md31.Fragment.NewSongsFragment;
import com.example.soundbox_du_an_md31.Fragment.PopularSongsFragment;
import com.example.soundbox_du_an_md31.Fragment.ProfileFragment;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.ActivityMainBinding;
import com.example.soundbox_du_an_md31.utils.GlideUtils;


@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements View.OnClickListener  {
    public static final int TYPE_HOME = 1;
    public static final int TYPE_ALL_SONGS = 2;
    public static final int TYPE_FEATURED_SONGS = 3;
    public static final int TYPE_POPULAR_SONGS = 4;
    public static final int TYPE_NEW_SONGS = 5;
    public static final int TYPE_FEEDBACK = 6;
    public static final int TYPE_CONTACT = 7;
//
    private int mTypeScreen = TYPE_HOME;
//
    private ActivityMainBinding mActivityMainBinding;
    private TextView tv_header;
    private int mAction;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());

        mActivityMainBinding.bottomNavView.setBackground(null);

        replaceFragment(new HomeFragment());

        mActivityMainBinding.bottomNavView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){
                replaceFragment(new HomeFragment());
            }else if(item.getItemId() == R.id.search){
                replaceFragment(new AllSongsFragment());
            }else if(item.getItemId() == R.id.library){
                replaceFragment(new LibraryFragment());
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.frame_layout,new LibraryFragment());
                fragmentTransaction.commit();
            }else if(item.getItemId() == R.id.premimum){
                replaceFragment(new FeedbackFragment());
            }
            return true;
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constant.CHANGE_LISTENER));
//



        initListener();
        displayLayoutBottom();
    }



    private void initListener() {
//        mActivityMainBinding.header.imgLeft.setOnClickListener(this);
//        mActivityMainBinding.header.layoutPlayAll.setOnClickListener(this);
//

        mActivityMainBinding.layoutBottom.imgPrevious.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgPlay.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgNext.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgClose.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.layoutText.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgSong.setOnClickListener(this);
    }


    public void openPopularSongsScreen() {
        replaceFragment(new PopularSongsFragment());
        mTypeScreen = TYPE_POPULAR_SONGS;

    }

    public void openNewSongsScreen() {
        replaceFragment(new NewSongsFragment());
        mTypeScreen = TYPE_NEW_SONGS;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_previous:
                clickOnPrevButton();
                break;

            case R.id.img_play:
                clickOnPlayButton();
                break;

            case R.id.img_next:
                clickOnNextButton();
                break;

            case R.id.img_close:
                clickOnCloseButton();
                break;

            case R.id.layout_text:
            case R.id.img_song:
                openPlayMusicActivity();

                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment).commitAllowingStateLoss();
    }

    //Sự kiện khi nhấn nút thoát
    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.name_app))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }
    ///////////////




    private void displayLayoutBottom() {
        if (MusicService.mPlayer == null) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }




    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }


    //Show thông tin bài hát hình ảnh tên bài hát vào tác giả
    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mActivityMainBinding.layoutBottom.tvSongName.setText(currentSong.getTitle());
        mActivityMainBinding.layoutBottom.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mActivityMainBinding.layoutBottom.imgSong);
    }

    //trạng thái chuyển bài trên thông báo khi phát nhạc
    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }


    //Thực hiện các sự kiện khi click tại layout bottom
    private void clickOnPrevButton() {
        GlobalFuntion.startMusicService(this, Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFuntion.startMusicService(this, Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(this, Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFuntion.startMusicService(this, Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void clickOnCloseButton() {
        GlobalFuntion.startMusicService(this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition);
    }

    private void openPlayMusicActivity() {
        GlobalFuntion.startActivity(this, PlayMusicActivity.class);
    }

    public ActivityMainBinding getActivityMainBinding() {
        return mActivityMainBinding;
    }
////
    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    public void gotoProfile(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ProfileFragment profileFragment = new ProfileFragment();
        fragmentTransaction.replace(R.id.frame_layout,profileFragment);
        fragmentTransaction.addToBackStack(ProfileFragment.TAG);
        fragmentTransaction.commit();
    }
    public void gotoChangeInformation(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ChangeInformationFragment changeInformationFragment = new ChangeInformationFragment();
        fragmentTransaction.replace(R.id.frame_layout,changeInformationFragment);
        fragmentTransaction.addToBackStack(ChangeInformationFragment.TAG);
        fragmentTransaction.commit();
    }
    public void gotoChangePassword(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
        fragmentTransaction.replace(R.id.frame_layout,changePasswordFragment);
        fragmentTransaction.addToBackStack(ChangeInformationFragment.TAG);
        fragmentTransaction.commit();
    }
}
