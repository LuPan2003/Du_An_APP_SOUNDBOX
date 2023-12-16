package com.example.soundbox_du_an_md31.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Activity.SharedPreferencesManager;
import com.example.soundbox_du_an_md31.Adapter.HistoryAdapter;
import com.example.soundbox_du_an_md31.Adapter.SongAdapter;
import com.example.soundbox_du_an_md31.Adapter.SongGridAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.utils.StringUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LibraryFragment extends Fragment {
    private MainActivity mainActivity;
    private ImageView profile,imgavatar,icon_settings;
    private TextView soLuongAlbum,tv_favorite;
    private LinearLayout album, favorite,history,layout_all_2;
    private List<Song> mListSong;
    private RecyclerView rcvHistory;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mainActivity = (MainActivity) getActivity();
        profile = view.findViewById(R.id.icon_profile);
        album = view.findViewById(R.id.danhsachphat);
        favorite = view.findViewById(R.id.btn_listsongfavorite);
        soLuongAlbum = view.findViewById(R.id.soLuongAlbum);
        tv_favorite = view.findViewById(R.id.tv_favorite);
        rcvHistory = view.findViewById(R.id.rcv_listHistory);
        layout_all_2 = view.findViewById(R.id.layout_content_all1);

//        rcvHistory.setNestedScrollingEnabled(false);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        getListSongFromFirebase("");
        getListAllSongs();
        initListener();
        if(user == null){
            soLuongAlbum.setText("0 danh sách");
            tv_favorite.setText("0 bài hát");
        }else{
            Uri photoUrl = user.getPhotoUrl();

            Glide.with(getContext()).load(photoUrl).override(32,32).error(R.drawable.avata).into(profile);
            Log.d("image", "onCreateView: "+ photoUrl);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference musicRef = database.getReference("album");
            DatabaseReference albumRef = musicRef.child(user.getUid());
            DatabaseReference favRef = database.getReference("favoritesongs").child(user.getUid());

            albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    // Số lượng phần tử trong bảng "album"
                    soLuongAlbum.setText(count+" danh sách");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xảy ra lỗi trong quá trình truy vấn
                    System.out.println("Lỗi: " + databaseError.getMessage());
                }
            });
            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = (int) snapshot.getChildrenCount();
                    // Số lượng phần tử trong bảng "album"
                    tv_favorite.setText(count+" bài hát");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Lỗi: " + error.getMessage());
                }
            });
        }




        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.gotoProfile();
            }
        });
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.gotoAlBum();
            }
        });
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoFavoriteSongs();
            }
        });



        return view;
    }

    @Override
    public void onResume() {

        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            soLuongAlbum.setText("0 danh sách");
            tv_favorite.setText("0 bài hát");
        }else{
            getListSongFromFirebase("");
            getListAllSongs();
            initListener();
            Uri photoUrl = user.getPhotoUrl();

            Glide.with(getContext()).load(photoUrl).override(32,32).error(R.drawable.avata).into(profile);
            Log.d("image", "onCreateView: "+ photoUrl);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference musicRef = database.getReference("album");
            DatabaseReference albumRef = musicRef.child(user.getUid());
            DatabaseReference favRef = database.getReference("favoritesongs").child(user.getUid());

            albumRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    // Số lượng phần tử trong bảng "album"
                    soLuongAlbum.setText(count+" danh sách");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xảy ra lỗi trong quá trình truy vấn
                    System.out.println("Lỗi: " + databaseError.getMessage());
                }
            });
            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = (int) snapshot.getChildrenCount();
                    // Số lượng phần tử trong bảng "album"
                    tv_favorite.setText(count+" bài hát");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    System.out.println("Lỗi: " + error.getMessage());
                }
            });
        }
    }


    private void getListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

// Truy cập đến nút (node) cần kiểm tra
        DatabaseReference booleanRef = databaseRef.child("users/"+user.getUid()+"/isVIP");

        booleanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem giá trị có tồn tại hay không
                if (dataSnapshot.exists()) {
                    // Lấy giá trị boolean từ DataSnapshot
                    Boolean booleanValue = dataSnapshot.getValue(Boolean.class);

                    // Kiểm tra giá trị boolean
                    if (booleanValue != null && booleanValue) {
                        // Giá trị là true
                        // TODO: Xử lý khi giá trị là true
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("history");

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user.getUid())) {
                                    // Nút con tồn tại
                                    // Thực hiện các hành động tương ứng
                                    MyApplication.get(getActivity()).getSongsHistoryDatabaseReference().addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            mListSong = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Song song = dataSnapshot.getValue(Song.class);
                                                if (song == null) {
                                                    return;
                                                }
                                                mListSong.add(0, song);
                                            }

                                            displayListAllSongs();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
                                        }
                                    });

                                } else {
                                    // Nút con không tồn tại
                                    // Thực hiện các hành động tương ứng
                                    Toast.makeText(getActivity(), "Chưa có bài hát nào", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Xảy ra lỗi trong quá trình đọc dữ liệu
                                Toast.makeText(getActivity(), "App đang bảo trì !!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        // Giá trị là false hoặc null

                        // TODO: Xử lý khi giá trị là false hoặc null
                        // Thành công
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("history");

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(user.getUid())) {
                                    // Nút con tồn tại
                                    // Thực hiện các hành động tương ứng
                                    MyApplication.get(getActivity()).getSongsHistoryDatabaseReference().addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            mListSong = new ArrayList<>();
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                Song song = dataSnapshot.getValue(Song.class);
                                                if (song == null) {
                                                    return;
                                                }
                                                mListSong.add(0, song);
                                            }

                                            displayListAllSongs();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
                                        }
                                    });

                                } else {
                                    // Nút con không tồn tại
                                    // Thực hiện các hành động tương ứng
                                    Toast.makeText(getActivity(), "Chưa có bài hát nào", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Xảy ra lỗi trong quá trình đọc dữ liệu
                                Toast.makeText(getActivity(), "App đang bảo trì !!", Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                } else {
                    // Nút không tồn tại trong database
                    // TODO: Xử lý khi nút không tồn tại
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("history");

                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user.getUid())) {
                                // Nút con tồn tại
                                // Thực hiện các hành động tương ứng
                                MyApplication.get(getActivity()).getSongsHistoryDatabaseReference().addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        mListSong = new ArrayList<>();
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            Song song = dataSnapshot.getValue(Song.class);
                                            if (song == null) {
                                                return;
                                            }
                                            mListSong.add(0, song);
                                        }

                                        displayListAllSongs();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
                                    }
                                });

                            } else {
                                // Nút con không tồn tại
                                // Thực hiện các hành động tương ứng
                                Toast.makeText(getActivity(), "Chưa có bài hát nào", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Xảy ra lỗi trong quá trình đọc dữ liệu
                            Toast.makeText(getActivity(), "App đang bảo trì !!", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc giá trị
            }
        });


    }
    //được sử dụng để hiển thị danh sách tất cả các bài hát lên giao diện người dùng.

    //được sử dụng để hiển thị danh sách tất cả các bài hát lên giao diện người dùng.
    private void displayListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvHistory.setLayoutManager(linearLayoutManager);

        HistoryAdapter songAdapter = new HistoryAdapter(mListSong, this::goToSongDetail);
        rcvHistory.setAdapter(songAdapter);
    }
    //được sử dụng để chuyển đến giao diện chi tiết của một bài hát.
    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    //được sử dụng để thiết lập các lắng nghe cho các sự kiện.
    //thiết lập lắng nghe cho sự kiện nhấp chuột vào nút phát tất cả các bài hát.
    //Khi nút này được nhấp chuột, chúng ta sẽ thiết lập lại danh sách các bài hát đang phát và thêm tất cả các bài hát trong danh sách bài hát hiện tại vào danh sách các bài hát đang phát. Sau đó, chúng ta sẽ khởi động dịch vụ âm nhạc và chuyển đến giao diện chi tiết của bài hát.
    private void initListener() {
        MainActivity activity = (MainActivity) getActivity();
        if (activity == null || activity.getActivityMainBinding() == null) {
            return;
        }

        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.mListSongPlaying.addAll(mListSong);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });

        //Sự kiện tìm kiếm bài hát

    }



    private void getListSongFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                layout_all_2.setVisibility(View.VISIBLE);
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }

                    if (StringUtil.isEmpty(key)) {
                        mListSong.add(0, song);
                    } else {
                        if (GlobalFuntion.getTextSearch(song.getTitle()).toLowerCase().trim()
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())
                                || GlobalFuntion.getTextSearch(song.getArtist()).toLowerCase().trim()
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())
                                || GlobalFuntion.getTextSearch(song.getGenre()).toLowerCase().trim()
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())
                        ) {
                            mListSong.add(0, song);
                        }
                    }
                }

                displayListPopularSongs();
                displayListNewSongs();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListPopularSongs() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        SongGridAdapter songGridAdapter = new SongGridAdapter(getListPopularSongs(), this::goToSongDetail);
    }
    private List<Song> getListPopularSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        List<Song> allSongs = new ArrayList<>(mListSong);
        Collections.sort(allSongs, (song1, song2) -> song2.getCount() - song1.getCount());
        for (Song song : allSongs) {
            if (list.size() < Constant.MAX_COUNT_POPULAR) {
                list.add(song);
            }
        }
        return list;
    }

    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rcvHistory.setLayoutManager(linearLayoutManager);

        HistoryAdapter songAdapter = new HistoryAdapter(getListNewSongs(), this::goToSongDetail);
        rcvHistory.setAdapter(songAdapter);
    }
    private List<Song> getListNewSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        for (Song song : mListSong) {
            if (song.isLatest() && list.size() < Constant.MAX_COUNT_LATEST) {
                list.add(song);
            }
        }
        return list;
    }




}