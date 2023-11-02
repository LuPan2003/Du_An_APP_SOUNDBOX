package com.example.soundbox_du_an_md31.Fragment;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.FragmentPlaySongBinding;
import com.example.soundbox_du_an_md31.utils.AppUtil;
import com.example.soundbox_du_an_md31.utils.GlideUtils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NonConstantResourceId")
public class PlaySongFragment extends Fragment implements View.OnClickListener {
    InterstitialAd mInterstitialAd;
    private FragmentPlaySongBinding mFragmentPlaySongBinding;
    private Timer mTimer;

    private AdView mAdView;
    private List<Song> mListSong;
    private int mAction;
    private ImageView heart_song,menuMusic,heart_play;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false);
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        initControl();
        heart_song = mFragmentPlaySongBinding.heartPlay;
        menuMusic = mFragmentPlaySongBinding.menuMusic;
        heart_play = mFragmentPlaySongBinding.heartPlay;
        showInforSong();
        mAction = MusicService.mAction;
        handleMusicAction();
        checkIsFavorite();
        // Banner QC
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        menuMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                if (user1 == null) {
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet, (LinearLayout) bottomSheetDialog.findViewById(R.id.bottomsheetcontainer));
                    TextView txtaddAlbum = (TextView) bottomSheetView.findViewById(R.id.txt_addAlbum);
                    txtaddAlbum.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
                            
                        }
                    });
                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();

                }
                else{
                    final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet, (LinearLayout) bottomSheetDialog.findViewById(R.id.bottomsheetcontainer));
                    TextView txtaddAlbum = (TextView) bottomSheetView.findViewById(R.id.txt_addAlbum);
                    Song currentSong1 = MusicService.mListSongPlaying.get(MusicService.mSongPosition);

                    DatabaseReference databaseRef1 = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference parentRef1 = databaseRef1.child("album"); // Đường dẫn đến bảng cha
                    DatabaseReference itemRef1 = parentRef1.child(user1.getUid()).child(currentSong1.getId() + "");

                    itemRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Phần tử tồn tại
                                txtaddAlbum.setText("Xóa khỏi danh sách phát");
                            } else {
                                // Phần tử không tồn tại
                                txtaddAlbum.setText("Thêm danh sách phát");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Xử lý khi có lỗi xảy ra
                        }
                    });
                    txtaddAlbum.setOnClickListener(view1 -> {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {
                            Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
                        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference parentRef = databaseRef.child("album"); // Đường dẫn đến bảng cha
                        DatabaseReference itemRef = parentRef.child(user.getUid()).child(currentSong.getId() + ""); // Đường dẫn đến bảng con và phần tử cần kiểm tra
                        mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
                        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    // Phần tử tồn tại
                                    itemRef.removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(getActivity(), "Bỏ thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // Xóa thất bại
                                                }
                                            });
                                } else {
                                    // Phần tử không tồn tại
                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                    DatabaseReference ref = db.getReference("album/" + user.getUid() + "/" + currentSong.getId());
//                DatabaseReference parentRef = db.getReference("song/"+ currentSong.getId());
//                DatabaseReference childRef = parentRef.child("userid");
//                childRef.setValue(""+user.getUid());

                                    Map<String, Object> data = new HashMap<>();
                                    DatabaseReference childRef = ref.child("" + user.getUid());
                                    data.put("artist", currentSong.getArtist());
                                    data.put("count", currentSong.getCount());
                                    data.put("genre", currentSong.getGenre());
                                    data.put("id", currentSong.getId());
                                    data.put("image", currentSong.getImage());
                                    data.put("latest", currentSong.isLatest());
                                    data.put("title", currentSong.getTitle());
                                    data.put("url", currentSong.getUrl());
                                    childRef.setValue(data);
                                    ref.setValue(data);
                                    Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Xử lý khi có lỗi xảy ra
                            }
                        });


                        bottomSheetDialog.cancel();
                    });


                    bottomSheetDialog.setContentView(bottomSheetView);
                    bottomSheetDialog.show();
                }







            }
        });
        mAdView = mFragmentPlaySongBinding.adView;
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //
        mFragmentPlaySongBinding.sharePlay.setOnClickListener(v -> sharePlay());

//        heart_song.setOnClickListener(view -> {
//            Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
//            addFavoriteSong(currentSong);
//        });
        heart_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("favoritesongs").child(user.getUid());
                            Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
                            Map<String, Object> data = new HashMap<>();
                            data.put("artist", currentSong.getArtist());
                            data.put("count", currentSong.getCount());
                            data.put("genre", currentSong.getGenre());
                            data.put("id", currentSong.getId());
                            data.put("image", currentSong.getImage());
                            data.put("latest", currentSong.isLatest());
                            data.put("title", currentSong.getTitle());
                            data.put("url", currentSong.getUrl());
                            myRef.child(String.valueOf(currentSong.getId())).setValue(data, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    Toast.makeText(getActivity(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), "Bạn chưa login", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return mFragmentPlaySongBinding.getRoot();

    }

    private void sharePlay() {
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        // Chia sẻ bài hát thông qua ứng dụng chia sẻ mặc định
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("audio/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(currentSong.getUrl()));
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài hát"));
    }


    private void initControl() {
        mTimer = new Timer();

        mFragmentPlaySongBinding.imgPrevious.setOnClickListener(this);
        mFragmentPlaySongBinding.imgPlay.setOnClickListener(this);
        mFragmentPlaySongBinding.imgNext.setOnClickListener(this);
        mFragmentPlaySongBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
        mFragmentPlaySongBinding.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mFragmentPlaySongBinding.imgSong);
    }

    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return;
        }
        switch (mAction) {
            case Constant.PREVIOUS:
            case Constant.NEXT:
                stopAnimationPlayMusic();
                showInforSong();
                break;

            case Constant.PLAY:
                showInforSong();
                if (MusicService.isPlaying) {
                    startAnimationPlayMusic();
                }
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.PAUSE:
                stopAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.RESUME:
                startAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;
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

    public void showSeekBar() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    if (MusicService.mPlayer == null) {
                        return;
                    }
                    mFragmentPlaySongBinding.tvTimeCurrent.setText(AppUtil.getTime(MusicService.mPlayer.getCurrentPosition()));
                    mFragmentPlaySongBinding.tvTimeMax.setText(AppUtil.getTime(MusicService.mLengthSong));
                    mFragmentPlaySongBinding.seekbar.setMax(MusicService.mLengthSong);
                    mFragmentPlaySongBinding.seekbar.setProgress(MusicService.mPlayer.getCurrentPosition());
                });
            }
        }, 0, 1000);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying)
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_pause_black);
        else {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
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

            default:
                break;
        }
    }

    private void clickOnPrevButton() {
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        InterstitialAd.load(getActivity(),"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        mInterstitialAd = null;
                    }
                });
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
        GlobalFuntion.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        InterstitialAd.load(getActivity(),"ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error

                        mInterstitialAd = null;
                    }
                });
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
        GlobalFuntion.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(getActivity(), Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFuntion.startMusicService(getActivity(), Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void checkIsFavorite(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("favoritesongs").child(user.getUid());
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
//                            check yêu thích
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Bạn chưa login", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
