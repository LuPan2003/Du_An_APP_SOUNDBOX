package com.example.soundbox_du_an_md31.Fragment;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
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

import com.example.soundbox_du_an_md31.Activity.CommentActivity;
import com.example.soundbox_du_an_md31.Activity.LoginActivity;
import com.example.soundbox_du_an_md31.Activity.volumeSeekBar;
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
    public static int mSongPosition;
    private int mAction;
    private String currentSongId;
    public static boolean isPlaying;
    private ImageView heart_song, menuMusic, heart_play, heartred;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };
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
    // Cung cấp phương thức để lấy binding
    public FragmentPlaySongBinding getFragmentPlaySongBinding() {
        return mFragmentPlaySongBinding;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false);
        // Kiểm tra trạng thái VIP của người dùng
        boolean isVIP = checkUserIsVIP1();

        if (!isVIP) {
            // Người dùng không phải VIP, hiển thị quảng cáo
            MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                }
            });
            mAdView = mFragmentPlaySongBinding.adView;
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        } else {
            // qNgười dùng là VIP, ẩn uảng cáo
            mFragmentPlaySongBinding.adView.setVisibility(View.GONE);
        }


        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }

        initControl();
        heart_song = mFragmentPlaySongBinding.heartPlay;
        menuMusic = mFragmentPlaySongBinding.menuMusic;
        heart_play = mFragmentPlaySongBinding.heartPlay;
        heartred = mFragmentPlaySongBinding.heartredPlay;
        FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
        Song currentSong1 = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mFragmentPlaySongBinding.commentPlay.setOnClickListener(v -> openComment());
        mFragmentPlaySongBinding.imgBlocked.setOnClickListener(v -> openvolum());
        mFragmentPlaySongBinding.imgStopwatch.setOnClickListener(v -> showTimerDialog());
        mFragmentPlaySongBinding.btnDownload.setOnClickListener(v -> download());
        mFragmentPlaySongBinding.sharePlay.setOnClickListener(v -> shareCurrentSong());
        // Đăng ký BroadcastReceiver
        IntentFilter filter = new IntentFilter("MUSIC_SHUTDOWN_ACTION");
        requireActivity().registerReceiver(musicShutdownReceiver, filter);
        // Banner QC


        if (user1 == null) {
            return mFragmentPlaySongBinding.getRoot();
        }

        DatabaseReference databaseRef1 = FirebaseDatabase.getInstance().getReference();
        DatabaseReference parentRef1 = databaseRef1.child("favoritesongs"); // Đường dẫn đến bảng cha
        DatabaseReference itemRef1 = parentRef1.child(user1.getUid()).child(currentSong1.getId() + "");

        itemRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Phần tử tồn tại
                    heartred.setVisibility(View.VISIBLE);
                    heart_play.setVisibility(View.GONE);
                } else {
                    // Phần tử không tồn tại
                    heartred.setVisibility(View.GONE);
                    heart_play.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
            }
        });

        // Nhận dữ liệu từ Bundle
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentSongId = arguments.getString("currentSongId");

            // Log currentSongId
            Log.d("PlaySongFragment", "Current Song ID: " + currentSongId);
        }
//        mFragmentPlaySongBinding.commentPlay.setOnClickListener(v -> showCommentBottomSheet());

        showInforSong();
        mAction = MusicService.mAction;
        handleMusicAction();


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

                } else {
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



        heartred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
                    return;
                }
                Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference parentRef = databaseRef.child("favoritesongs"); // Đường dẫn đến bảng cha
                DatabaseReference itemRefF = parentRef.child(user.getUid()).child(currentSong.getId() + ""); // Đường dẫn đến bảng con và phần tử cần kiểm tra
                mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
                itemRefF.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Phần tử tồn tại
                            itemRefF.removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            heartred.setVisibility(View.GONE);
                                            heart_play.setVisibility(View.VISIBLE);
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
                                                heart_play.setVisibility(View.GONE);
                                                heartred.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "Bạn chưa login", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        heart_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
                    return;
                }
                Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference parentRef = databaseRef.child("favoritesongs"); // Đường dẫn đến bảng cha
                DatabaseReference itemRefF = parentRef.child(user.getUid()).child(currentSong.getId() + ""); // Đường dẫn đến bảng con và phần tử cần kiểm tra
                mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
                itemRefF.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Phần tử tồn tại
                            itemRefF.removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            heartred.setVisibility(View.GONE);
                                            heart_play.setVisibility(View.VISIBLE);
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
                                                heart_play.setVisibility(View.GONE);
                                                heartred.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "Bạn chưa login", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        return mFragmentPlaySongBinding.getRoot();

    }

    private void openvolum() {
        // Tiếp tục với việc mở màn hình bình luận
        Intent intent = new Intent(getActivity(), volumeSeekBar.class);
        startActivity(intent);
    }

    private void openComment() {
        // Kiểm tra đăng nhập
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity(), "Vui lòng đăng nhập để bình luận!", Toast.LENGTH_SHORT).show();
            // Người dùng chưa đăng nhập, chuyển họ đến màn hình đăng nhập/đăng ký
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        // Kiểm tra kết nối internet
        if (!isOnline()) {
            Toast.makeText(getActivity(), "Không có kết nối internet. Vui lòng kiểm tra lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra và gán giá trị cho currentSong từ nguồn dữ liệu phù hợp
        Song currentSong = getSongFromSomeSource();
        if (currentSong == null) {
            // Nếu không thể lấy được bài hát, hiển thị thông báo hoặc thực hiện xử lý phù hợp
            return;
        }

        // Tiếp tục với việc mở màn hình bình luận
        Intent intent = new Intent(getActivity(), CommentActivity.class);

        // Sử dụng phương thức để tạo SONG_ID từ Artist và Title
        String songId = createSongId(currentSong.getArtist(), currentSong.getTitle());
        intent.putExtra("SONG_ID", songId);
        Log.d("openComment", "songId: " + songId);
        startActivity(intent);
    }

    // Phương thức kiểm tra kết nối internet
    private boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    // Phương thức tạo SONG_ID từ Artist và Title
    private String createSongId(String artist, String title) {
// Đơn giản là kết hợp Artist và Title, có thể bạn cần chuẩn hóa chúng trước khi kết hợp
        artist = (artist != null) ? artist : "";
        title = (title != null) ? title : "";
        return artist + "_" + title;
    }

    // Phương thức để lấy đối tượng Song từ nguồn dữ liệu phù hợp
    private Song getSongFromSomeSource() {
        // Thực hiện logic để lấy đối tượng Song từ nguồn dữ liệu, ví dụ: danh sách bài hát
        // Nếu không thể lấy được, trả về null hoặc thực hiện xử lý phù hợp
        return MusicService.mListSongPlaying.get(MusicService.mSongPosition);
    }

//    private void showCommentBottomSheet() {
//        CommentBottomSheetFragment commentFragment = new CommentBottomSheetFragment();
//        commentFragment.show(getChildFragmentManager(), commentFragment.getTag());
//    }
private void shareCurrentSong() {
    Song currentSong = MusicService.mListSongPlaying.get(mSongPosition);

    if (currentSong != null) {
        // Tạo một Intent để chia sẻ thông tin bài hát đang phát
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, generateSongShareText(currentSong));

        // Bắt đầu Activity chia sẻ
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài hát nhạc"));
    } else {
        Toast.makeText(getActivity(), "Không có thông tin bài hát để chia sẻ.", Toast.LENGTH_SHORT).show();
    }
}

    private String generateSongShareText(Song song) {
        return "Chia sẽ bài hát nhạc trẻ:\n" +
                song.getTitle() + " - " + song.getArtist() + "\n" +
                song.getUrl() + "\n";
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
        // Hủy đăng ký BroadcastReceiver khi Fragment bị hủy
        requireActivity().unregisterReceiver(musicShutdownReceiver);
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

    private boolean checkUserIsVIP1() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Người dùng đã đăng nhập, kiểm tra trạng thái VIP từ cơ sở dữ liệu
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference booleanRef = databaseRef.child("users/" + user.getUid() + "/isVIP");
            booleanRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isVIP = dataSnapshot.getValue(Boolean.class);
                    if (isVIP != null) {
                        // Xử lý trạng thái VIP
                        handleVIPStatus(isVIP);
                    } else {
                        // Người dùng không phải VIP
                        handleVIPStatus(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Người dùng không phải VIP
                    handleVIPStatus(false);
                }
            });

            // Kết quả trạng thái VIP sẽ được xử lý trong callback, nên hàm checkUserIsVIP1()
            // không trả về giá trị ngay lập tức
            return false;
        } else {
            // Người dùng chưa đăng nhập, không phải VIP
            return false;
        }
    }

    private void handleVIPStatus(boolean isVIP) {
        if (isVIP) {
            // Người dùng là VIP, xử lý logic tương ứng
        } else {
            // Người dùng không phải VIP, xử lý logic tương ứng
        }
    }

    private void checkUserIsVIP(final Callback<Boolean> callback) {
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
            callback.onResult(false);
        }
    }

    public interface Callback<T> {
        void onResult(T result);
    }

    private void clickOnPrevButton() {
        checkUserIsVIP(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean isVIP) {
                if (isVIP) {
                    // Xử lý khi người dùng là VIP
                    GlobalFuntion.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
                } else {
                    // Xử lý khi người dùng không phải VIP
                    MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                        }
                    });
                    mAdView = mFragmentPlaySongBinding.adView;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                    InterstitialAd.load(getActivity(), "ca-app-pub-8801498166910444/9063512975", adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    mInterstitialAd = interstitialAd;
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd.show(getActivity());
                                    } else {
                                        Log.d("TAG", "Quảng cáo toàn màn hình chưa sẵn sàng.");
                                    }
                                    GlobalFuntion.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Xử lý lỗi
                                    mInterstitialAd = null;
                                    GlobalFuntion.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
                                }
                            });
                }
            }
        });
    }

    private void clickOnNextButton() {
        checkUserIsVIP(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean isVIP) {
                if (isVIP) {
                    // Xử lý khi người dùng là VIP
                    GlobalFuntion.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
                } else {
                    // Xử lý khi người dùng không phải VIP
                    MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
                        @Override
                        public void onInitializationComplete(InitializationStatus initializationStatus) {
                        }
                    });
                    mAdView = mFragmentPlaySongBinding.adView;
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);
                    InterstitialAd.load(getActivity(), "ca-app-pub-8801498166910444/6877113584", adRequest,
                            new InterstitialAdLoadCallback() {
                                @Override
                                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                    mInterstitialAd = interstitialAd;
                                    if (mInterstitialAd != null) {
                                        mInterstitialAd.show(getActivity());
                                    } else {
                                        Log.d("TAG", "Quảng cáo toàn màn hình chưa sẵn sàng.");
                                    }
                                    GlobalFuntion.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
                                }

                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Xử lý lỗi
                                    mInterstitialAd = null;
                                    GlobalFuntion.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
                                }
                            });
                }
            }
        });
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFuntion.startMusicService(getActivity(), Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFuntion.startMusicService(getActivity(), Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void showTimerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Chọn thời gian thoát tắt nhạc");

        // Mảng chứa các mốc thời gian
        final CharSequence[] timerOptions = {"5 phút", "10 phút", "15 phút", "30 phút"};

        builder.setItems(timerOptions, (dialog, which) -> {
            String selectedTime = timerOptions[which].toString();
            Toast.makeText(getContext(), "Đặt hẹn giờ thoát nhạc " + selectedTime, Toast.LENGTH_SHORT).show();

            // Gọi phương thức để xử lý hẹn giờ tắt nhạc với thời gian được chọn
            scheduleMusicShutdown(selectedTime);
        });

        builder.show();
    }

    //  // Use Handler to delay the action
    //        Handler handler = new Handler();
    //        handler.postDelayed(new Runnable() {
    //            @Override
    //            public void run() {
    //                if (MusicService.mPlayer != null && MusicService.mPlayer.isPlaying()) {
//                        MusicService.mPlayer.pause();
    //                    isPlaying = false;
    //                     mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
    //                    sendBroadcastChangeListener();
    //                }
    //                // You can also add additional logic based on your music control requirements
    //
    //                Toast.makeText(getContext(), "Nhạc đã được tắt theo hẹn giờ", Toast.LENGTH_SHORT).show();
    //            }
    //        }, delayMillis);

    private void scheduleMusicShutdown(String selectedTime) {
        long delayMillis = 0; // Default delay is 0 milliseconds

        // Convert selectedTime to milliseconds based on the chosen option
        switch (selectedTime) {
            case "5 phút":
                delayMillis = 5 * 60 * 1000; // 5 minutes in milliseconds
                break;
            case "10 phút":
                delayMillis = 10 * 60 * 1000; // 10 minutes in milliseconds
                break;
            case "15 phút":
                delayMillis = 15 * 60 * 1000; // 15 minutes in milliseconds
                break;
            case "30 phút":
                delayMillis = 30 * 60 * 1000; // 30 minutes in milliseconds
                break;
        }
        Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (MusicService.mPlayer != null && MusicService.mPlayer.isPlaying()) {
                        MusicService.mPlayer.pause();
                            isPlaying = false;
                             mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
                            sendBroadcastChangeListener();
                        }
                        // You can also add additional logic based on your music control requirements

                        Toast.makeText(getContext(), "Nhạc đã được tắt theo hẹn giờ", Toast.LENGTH_SHORT).show();
                    }
                }, delayMillis);
    }

    private void download() {
        checkUserIsVIP(new Callback<Boolean>() {
            @Override
            public void onResult(Boolean isVipUser) {
                if (!isVipUser) {
                    // Hiển thị thông báo cho người dùng không phải là VIP
                    Toast.makeText(getActivity(), "Đăng ký gói premium để được tải nhạc", Toast.LENGTH_SHORT).show();
                    return;
                }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);

        // Kiểm tra xem bài hát hiện tại có URL hợp lệ không
        if (currentSong == null || TextUtils.isEmpty(currentSong.getUrl())) {
            Toast.makeText(getActivity(), "Không thể tải bài hát này", Toast.LENGTH_SHORT).show();
            return;
        }
        // Sử dụng DownloadManager để tải bài hát
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(currentSong.getUrl()));
        request.setTitle(currentSong.getTitle());
        request.setDescription("Đang tải bài hát");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, currentSong.getTitle() + ".mp3");

        DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(getActivity(), "Đang tải xuống...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Không thể tạo yêu cầu tải xuống", Toast.LENGTH_SHORT).show();
        }
            }
        });
    }

    private void sendBroadcastChangeListener() {
        if (getActivity() != null) {
            Intent intent = new Intent(Constant.CHANGE_LISTENER);
            intent.putExtra(Constant.MUSIC_ACTION, mAction);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    }
}