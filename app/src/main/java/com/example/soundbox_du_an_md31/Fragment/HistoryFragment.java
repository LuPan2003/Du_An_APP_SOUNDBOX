package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Adapter.SongAdapter;
import com.example.soundbox_du_an_md31.Adapter.SongGridAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.FragmentAlbumBinding;
import com.example.soundbox_du_an_md31.databinding.FragmentHistoryBinding;
import com.example.soundbox_du_an_md31.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding mFragmentAllSongsBinding;
    private List<Song> mListSong;
    private ImageView icon_back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentAllSongsBinding = FragmentHistoryBinding.inflate(inflater, container, false);
        mFragmentAllSongsBinding.layoutPlayAll.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user == null){
                Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
                return;
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("history");

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(user.getUid())) {
                        // Nút con tồn tại
                        // Thực hiện các hành động tương ứng
                        MusicService.clearListSongPlaying();
                        MusicService.mListSongPlaying.addAll(mListSong);
                        MusicService.isPlaying = false;
                        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
                        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
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


        });

        getListSongFromFirebase("");
        getListAllSongs();
        initListener();
        return mFragmentAllSongsBinding.getRoot();
    }


    //được sử dụng để lấy danh sách tất cả các bài hát từ Firebase Realtime Database.
    // Trong hàm này, chúng ta sẽ sử dụng phương thức addValueEventListener()
    // để lắng nghe các thay đổi đối với dữ liệu trong Firebase Realtime Database.
    private void getListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
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
    //được sử dụng để hiển thị danh sách tất cả các bài hát lên giao diện người dùng.
    private void displayListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAllSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        mFragmentAllSongsBinding.rcvData.setAdapter(songAdapter);
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
        mFragmentAllSongsBinding.imgSearchAll.setOnClickListener(view -> searchSong());
        mFragmentAllSongsBinding.edtSearchNameAll.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
                return true;
            }
            return false;
        });

        mFragmentAllSongsBinding.edtSearchNameAll.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (mListSong != null) mListSong.clear();
                    getListSongFromFirebase("");
                }
            }
        });

        // Back
        mFragmentAllSongsBinding.iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
    }



    private void getListSongFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Toast.makeText(getActivity(), "Chưa đăng nhập tài khoản", Toast.LENGTH_SHORT).show();
            return;
        }
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
                            mFragmentAllSongsBinding.layoutContentAll.setVisibility(View.VISIBLE);
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
        mFragmentAllSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(getListNewSongs(), this::goToSongDetail);
        mFragmentAllSongsBinding.rcvData.setAdapter(songAdapter);
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
    private void searchSong() {
        String strKey = mFragmentAllSongsBinding.edtSearchNameAll.getText().toString().trim();
        if (mListSong != null) mListSong.clear();
        getListSongFromFirebase(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }
}