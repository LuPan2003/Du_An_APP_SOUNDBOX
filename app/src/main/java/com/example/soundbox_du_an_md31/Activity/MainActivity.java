package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Fragment.AlbumFragment;
import com.example.soundbox_du_an_md31.Fragment.AllSongsFragment;
import com.example.soundbox_du_an_md31.Fragment.AppFeedbackFragment;
import com.example.soundbox_du_an_md31.Fragment.ChangeInformationFragment;
import com.example.soundbox_du_an_md31.Fragment.ChangePasswordFragment;
import com.example.soundbox_du_an_md31.Fragment.ChinesemusicFragment;
import com.example.soundbox_du_an_md31.Fragment.DangkinhacFragment;
import com.example.soundbox_du_an_md31.Fragment.DownLoadFragment;
import com.example.soundbox_du_an_md31.Fragment.EdmmusicFragment;
import com.example.soundbox_du_an_md31.Fragment.FeedbackFragment;
import com.example.soundbox_du_an_md31.Fragment.HomeFragment;
import com.example.soundbox_du_an_md31.Fragment.HousemusicFragment;
import com.example.soundbox_du_an_md31.Fragment.KoreanmusicFragment;
import com.example.soundbox_du_an_md31.Fragment.LibraryFragment;
import com.example.soundbox_du_an_md31.Fragment.LibraryLoginFragment;
import com.example.soundbox_du_an_md31.Fragment.ListSongFavoriteFragment;
import com.example.soundbox_du_an_md31.Fragment.MusicrapFragment;
import com.example.soundbox_du_an_md31.Fragment.NewSongsFragment;
import com.example.soundbox_du_an_md31.Fragment.NhacVipFragment;
import com.example.soundbox_du_an_md31.Fragment.PlaySongFragment;
import com.example.soundbox_du_an_md31.Fragment.PopularSongsFragment;
import com.example.soundbox_du_an_md31.Fragment.PremiumFragment;
import com.example.soundbox_du_an_md31.Fragment.ProfileFragment;
import com.example.soundbox_du_an_md31.Fragment.UsukFragment;
import com.example.soundbox_du_an_md31.Fragment.VietnamesemusicFragment;
import com.example.soundbox_du_an_md31.Fragment.YoungmusicFragment;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.Model.SongDown;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.Service.MyServiceDown;
import com.example.soundbox_du_an_md31.databinding.ActivityMainBinding;
import com.example.soundbox_du_an_md31.utils.GlideUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements View.OnClickListener  {
    private AdView mAdView;
    InterstitialAd mInterstitialAd;
    public static final int TYPE_HOME = 1;
    public static final int MY_REQUEST_CODE= 10;

    public static final int TYPE_ALL_SONGS = 2;
    public static final int TYPE_FEATURED_SONGS = 3;
    public static final int TYPE_POPULAR_SONGS = 4;
    public static final int TYPE_ALBUM_SONGS = 8;
    public static final int TYPE_NEW_SONGS = 5;
    public static final int TYPE_FEEDBACK = 6;
    public static final int TYPE_CONTACT = 7;
    public static final int TYPE_VIETNAM_SONGS = 9;
    public static final int TYPE_TRUNGQUOC_SONGS = 10;
    public static final int TYPE_HANQUOC_SONGS = 11;
    public static final int TYPE_USUK_SONGS = 12;
    public static final int TYPE_TRE_SONGS = 13;
    public static final int TYPE_RAP_SONGS = 14;
    public static final int TYPE_HOUSE_SONGS = 15;
    public static final int TYPE_EDM_SONGS = 15;
//
    private int mTypeScreen = TYPE_HOME;
   private Fragment currentFragment;
//
    private ActivityMainBinding mActivityMainBinding;

      private ProfileFragment mProfileFragment = new ProfileFragment();
    private static final int PICK_IMAGE_REQUEST = 1;

    final private ActivityResultLauncher<Intent> mActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
             new ActivityResultCallback<ActivityResult>() {
         @Override
         public void onActivityResult(ActivityResult result) {
             if(result.getResultCode()==RESULT_OK){
                 Intent intent = result.getData();
                 if(intent == null){
                     return;
                 }
                 Uri uri = intent.getData();
                 mProfileFragment.setUri(uri);
                 try {
                     Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
//                     mProfileFragment.setImageBitmap(bitmap);
                 } catch (IOException e) {
                     throw new RuntimeException(e);
                 }
             }
         }
     });
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

        mAdView = mActivityMainBinding.adView;
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        checkVip();

        FirebaseMessaging.getInstance().subscribeToTopic("News")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Done";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }

                        // Thực hiện một hành động nào đó với kết quả, ví dụ: hiển thị Toast
                    }
                });


        setContentView(mActivityMainBinding.getRoot());

        mActivityMainBinding.bottomNavView.setBackground(null);



        replaceFragment(new HomeFragment());


        mActivityMainBinding.bottomNavView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home){

                replaceFragment(new HomeFragment());
            }else if(item.getItemId() == R.id.search){
                replaceFragment(new AllSongsFragment());
            }else if(item.getItemId() == R.id.library){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    replaceFragment(new LibraryFragment());
                    switchToOtherScreen();
//                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.add(R.id.frame_layout,new LibraryFragment());
//                    fragmentTransaction.commit();
                }else{
                    replaceFragment(new LibraryLoginFragment());
                    switchToOtherScreen();
                }
            }else if(item.getItemId() == R.id.premimum){
                replaceFragment(new FeedbackFragment());
            }
            return true;
        });
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constant.CHANGE_LISTENER));
//
        // Tạo một đối tượng `MediaController`



        initListener();
        displayLayoutBottom();
        displayLayoutBottomD();
    }

    private void switchToOtherScreen() {
        // Kết thúc fragment hiện tại
        if (currentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(currentFragment).commit();
            currentFragment = null;
        }

        // Chuyển đến màn hình khác
        // ...
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
    public void openTremusic() {
        replaceFragment(new YoungmusicFragment());
        mTypeScreen = TYPE_TRE_SONGS;

    }
    public void openRapmusic() {
        replaceFragment(new MusicrapFragment());
        mTypeScreen = TYPE_RAP_SONGS;

    }
    public void openHousemusic() {
        replaceFragment(new HousemusicFragment());
        mTypeScreen = TYPE_HOUSE_SONGS;

    }
    public void openEdmmusic() {
        replaceFragment(new EdmmusicFragment());
        mTypeScreen = TYPE_EDM_SONGS;

    }
    public void openVietnammusic() {
        replaceFragment(new VietnamesemusicFragment());
        mTypeScreen = TYPE_VIETNAM_SONGS;

    }
    public void openChinesemusic() {
        replaceFragment(new ChinesemusicFragment());
        mTypeScreen = TYPE_TRUNGQUOC_SONGS;

    }
    public void openKoreanmusic() {
        replaceFragment(new KoreanmusicFragment());
        mTypeScreen = TYPE_HANQUOC_SONGS;
    }
    public void openUsukmusic() {
        replaceFragment(new UsukFragment());
        mTypeScreen = TYPE_USUK_SONGS;
    }

    public void openPopularSongsScreen() {
        replaceFragment(new PopularSongsFragment());
        mTypeScreen = TYPE_POPULAR_SONGS;

    }
    public void openAlbumSongsScreen() {
        replaceFragment(new AlbumFragment());
        mTypeScreen = TYPE_ALBUM_SONGS;

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

    private void displayLayoutBottomD() {
        if (MyServiceDown.mPlayer == null) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSongD();
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
    private void showInforSongD() {
        if (MyServiceDown.mListSongPlaying1 == null || MyServiceDown.mListSongPlaying1.isEmpty()) {
            return;
        }
        SongDown currentSong = MyServiceDown.mListSongPlaying1.get(MyServiceDown.mSongPosition);
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


    private void checkUserIsVIP(final Callback<Boolean> callback) {
        currentFragment= new PlaySongFragment();
        switchToOtherScreen();
        Log.d("close", "có vào ");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference booleanRef = databaseRef.child("users/" + user.getUid() + "/isVIP");
            booleanRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isVIP = dataSnapshot.getValue(Boolean.class);
                    if (isVIP != null) {
                        callback.onResult(isVIP);
                    } else {
                        callback.onResult(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    callback.onResult(false);
                }
            });
        } else {
            // Người dùng chưa đăng nhập, hiển thị quảng cáo
            callback.onResult(false);
        }
    }

    private interface Callback<T> {
        void onResult(T result);
    }

    private void clickOnCloseButton() {
        checkUserIsVIP(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean isVIP) {
                if (isVIP) {
                    // Nếu tài khoản là VIP, không hiển thị quảng cáo
                    // Bắt đầu dịch vụ âm nhạc
                    GlobalFuntion.startMusicService(MainActivity.this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition);
                } else {
                    // Nếu tài khoản không phải VIP, hiển thị quảng cáo
                    MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                        }
                    });
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                    InterstitialAd.load(MainActivity.this, "ca-app-pub-8801498166910444/8422167536", adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    // Biến tham chiếu mInterstitialAd sẽ null cho đến khi
                                    // quảng cáo được tải.
                                    mInterstitialAd = interstitialAd;
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd.show(MainActivity.this);
                                    } else {
                                        Log.d("TAG", "Quảng cáo toàn màn hình chưa sẵn sàng.");
                                    }
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Xử lý lỗi
                                    mInterstitialAd = null;
                                }
                            });

                    // Bắt đầu dịch vụ âm nhạc
                    GlobalFuntion.startMusicService(MainActivity.this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition);
                }
            }
        });
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

        fragmentTransaction.replace(R.id.frame_layout,mProfileFragment);
        fragmentTransaction.addToBackStack(ProfileFragment.TAG);
        fragmentTransaction.commit();
    }
    public void gotoAlBum(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AlbumFragment changeInformationFragment = new AlbumFragment();
        fragmentTransaction.replace(R.id.frame_layout,changeInformationFragment);
        fragmentTransaction.addToBackStack(ChangeInformationFragment.TAG);
        fragmentTransaction.commit();
    }
    public void gotoDown(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DownLoadFragment changeInformationFragment = new DownLoadFragment();
        fragmentTransaction.replace(R.id.frame_layout,changeInformationFragment);
        fragmentTransaction.addToBackStack(ChangeInformationFragment.TAG);
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
    public void gotoFavoriteSongs(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        ListSongFavoriteFragment listSongFavoriteFragment = new ListSongFavoriteFragment();
        fragmentTransaction.replace(R.id.frame_layout,listSongFavoriteFragment);
        fragmentTransaction.addToBackStack(ListSongFavoriteFragment.TAG);
        fragmentTransaction.commit();
    }
    public void gotoFeedback(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        AppFeedbackFragment feedbackAppFragment = new AppFeedbackFragment();
        fragmentTransaction.replace(R.id.frame_layout,feedbackAppFragment);
        fragmentTransaction.addToBackStack(AppFeedbackFragment.TAG);
        fragmentTransaction.commit();
    }

    public void gotoNhacVip(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DangkinhacFragment nhacVipFragment = new DangkinhacFragment();
        fragmentTransaction.replace(R.id.frame_layout,nhacVipFragment);
        fragmentTransaction.addToBackStack(NhacVipFragment.TAG);
        fragmentTransaction.commit();
    }
    public void gotoPremium(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        PremiumFragment premiumFragment = new PremiumFragment();
        fragmentTransaction.replace(R.id.frame_layout,premiumFragment);
        fragmentTransaction.addToBackStack(PremiumFragment.TAG);
        fragmentTransaction.commit();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQUEST_CODE){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }
    public void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent,"Select Picture"));
    }

    public void changeScreenBackgroundColor() {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setBackgroundColor(Color.BLUE);
    }
    public void checkVip(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Log.d("zzz","Khách");
        }else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("users").child(user.getUid());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Boolean isVIP = snapshot.child("isVIP").getValue(Boolean.class);
                        Log.d("data", String.valueOf(isVIP));
                        if(isVIP == true){

                            Log.d("zzz","tài khoản vip");
                            Object value = snapshot.getValue();
                            if (value instanceof HashMap) {
                                HashMap<String, Object> hashMapValue = (HashMap<String, Object>) value;
                                String endTimeStr = (String) hashMapValue.get("endTime");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                try {
                                    Date date = dateFormat.parse(endTimeStr);

                                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                                    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                                    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

                                    int day = Integer.parseInt(dayFormat.format(date));
                                    int month = Integer.parseInt(monthFormat.format(date));
                                    int year = Integer.parseInt(yearFormat.format(date));
                                    LocalDate currentDate = LocalDate.now();
                                    LocalDate endDate = LocalDate.of(year, month, day);
                                    long remainingDays = ChronoUnit.DAYS.between(currentDate, endDate);
                                    Log.d("time1","Ngày: " + day);
                                    Log.d("time2","Ngày: " + month);
                                    Log.d("time3","Ngày: " + year);
                                    Log.d("time4", String.valueOf(remainingDays));
                                    if(remainingDays <= 0){
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("isVIP", false);
                                    reference.updateChildren(data);
                                }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            Log.d("zzz","tài khoản thường");
                        }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}
