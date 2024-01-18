package com.example.soundbox_du_an_md31.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Adapter.BannerSongAdapter;
import com.example.soundbox_du_an_md31.Adapter.SongAdapter;
import com.example.soundbox_du_an_md31.Adapter.SongGridAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.FragmentHomeBinding;
import com.example.soundbox_du_an_md31.utils.GlideUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private ProgressDialog progressDialog;

    private FragmentHomeBinding mFragmentHomeBinding;

    private List<Song> mListSong;
    private List<Song> mListRecommended;
    private List<Song> mListSongBanner;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListSongBanner == null || mListSongBanner.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListSongBanner.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        progressDialog = new ProgressDialog(getActivity());
        getListSongFromFirebase("");
        getRecommendedSongs();
        initListener();
//
        return mFragmentHomeBinding.getRoot();
    }

    private void initListener() {
//        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                // Do nothing
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                // Do nothing
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String strKey = s.toString().trim();
//                if (strKey.equals("") || strKey.length() == 0) {
//                    if (mListSong != null) mListSong.clear();
//                    getListSongFromFirebase("");
//                }
//            }
//        });

//        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchSong());
//
//        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
//            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                searchSong();
//                return true;
//            }
//            return false;
//        });
        mFragmentHomeBinding.btnNhactre.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openTremusic();
            }
        });
        mFragmentHomeBinding.btnNhacrap.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openRapmusic();
            }
        });
        mFragmentHomeBinding.btnNhachouse.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openHousemusic();
            }
        });
        mFragmentHomeBinding.btnNhacedm.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openEdmmusic();
            }
        });

        mFragmentHomeBinding.btnViet.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openVietnammusic();
            }
        });
        mFragmentHomeBinding.btnTrung.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openChinesemusic();
            }
        });
        mFragmentHomeBinding.btnHanquoc.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openKoreanmusic();
            }
        });
        mFragmentHomeBinding.btnUsuk.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openUsukmusic();
            }
        });

        mFragmentHomeBinding.layoutViewAllPopular.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openPopularSongsScreen();
            }
        });

        mFragmentHomeBinding.layoutViewAllNewSongs.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.openNewSongsScreen();
            }
        });
    }

    private void getListSongFromFirebase(String key) {
        progressDialog.show();
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                mFragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
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
                                .contains(GlobalFuntion.getTextSearch(key).toLowerCase().trim())) {
                            mListSong.add(0, song);
                        }
                    }
                }
                displayListBannerSongs();
                displayListPopularSongs();
                displayListNewSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListBannerSongs() {
        BannerSongAdapter bannerSongAdapter = new BannerSongAdapter(getListBannerSongs(), this::goToSongDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(bannerSongAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }


    private List<Song> getListBannerSongs() {
        if (mListSongBanner != null) {
            mListSongBanner.clear();
        } else {
            mListSongBanner = new ArrayList<>();
        }
        if (mListSong == null || mListSong.isEmpty()) {
            return mListSongBanner;
        }
        for (Song song : mListSong) {
            if (song.isFeatured() && mListSongBanner.size() < Constant.MAX_COUNT_BANNER) {
                mListSongBanner.add(song);
            }
        }
        return mListSongBanner;
    }

    private void displayListPopularSongs() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvPopularSongs.setLayoutManager(gridLayoutManager);

        SongGridAdapter songGridAdapter = new SongGridAdapter(getListPopularSongs(), this::goToSongDetail);
        mFragmentHomeBinding.rcvPopularSongs.setAdapter(songGridAdapter);
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
        mFragmentHomeBinding.rcvNewSongs.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(getListNewSongs(), this::goToSongDetail);
        mFragmentHomeBinding.rcvNewSongs.setAdapter(songAdapter);
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

//    private void searchSong() {
//        String strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
//        if (mListSong != null) mListSong.clear();
//        getListSongFromFirebase(strKey);
//        GlobalFuntion.hideSoftKeyboard(getActivity());
//    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

//    Gợi ý bài hát
    private void getRecommendedSongs(){
        DatabaseReference songsRef = FirebaseDatabase.getInstance().getReference().child("songs");
        int numberOfSongs = 5;
        songsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotMusic) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
//                    Có user
                    if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
//                        Không có bài hát
                        DatabaseReference history = FirebaseDatabase.getInstance().getReference().child("history").child(user.getUid());
                        history.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
//                                    có lịch sử
                                    Log.d("data", snapshot.toString());
                                    List<Song> allSongs = new ArrayList<>();
                                    // Lặp qua tất cả các bài hát trong dataSnapshot
                                    for (DataSnapshot songSnapshot : snapshotMusic.getChildren()) {
                                        Song song = songSnapshot.getValue(Song.class);
                                        allSongs.add(song);
                                    }
                                    List<Song> songsInGenre = new ArrayList<>();
                                    for (DataSnapshot songTheLoai : snapshot.getChildren()) {
                                        Song song = songTheLoai.getValue(Song.class);
                                        Log.d("song", song.getGenre());
                                        String theloai = song.getGenre();
                                        for (Song songRamdom : allSongs) {
                                            if (song != null && theloai.equals(song.getGenre())) {
                                                songsInGenre.add(songRamdom);
                                            }
                                        }
                                        List<Song> recommendedSongs = new ArrayList<>();
                                        Random random = new Random();
                                        while (recommendedSongs.size() < numberOfSongs && !allSongs.isEmpty()) {
                                            int randomIndex = random.nextInt(allSongs.size());
                                            recommendedSongs.add(allSongs.remove(randomIndex));
                                        }
                                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                        mFragmentHomeBinding.rcvRecommendedSongs.setLayoutManager(linearLayoutManager);
                                        SongAdapter songAdapter = new SongAdapter(recommendedSongs,song1 -> goToSong(song1) );
                                        mFragmentHomeBinding.rcvRecommendedSongs.setAdapter(songAdapter);
                                        break;
                                    }



                                }else{
//                                    Không có lịch sử
                                    List<Song> allSongs = new ArrayList<>();

                                    // Lặp qua tất cả các bài hát trong dataSnapshot
                                    for (DataSnapshot songSnapshot : snapshotMusic.getChildren()) {
                                        Song song = songSnapshot.getValue(Song.class);
                                        allSongs.add(song);
                                    }

                                    List<Song> recommendedSongs = new ArrayList<>();
                                    Random random = new Random();
                                    while (recommendedSongs.size() < numberOfSongs && !allSongs.isEmpty()) {
                                        int randomIndex = random.nextInt(allSongs.size());
                                        recommendedSongs.add(allSongs.remove(randomIndex));
                                    }
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                    mFragmentHomeBinding.rcvRecommendedSongs.setLayoutManager(linearLayoutManager);
                                    SongAdapter songAdapter = new SongAdapter(recommendedSongs,song -> goToSong(song) );
                                    mFragmentHomeBinding.rcvRecommendedSongs.setAdapter(songAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }else{
//                        Đang nghe
                        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
                        List<Song> allSongs = new ArrayList<>();

                        // Lặp qua tất cả các bài hát trong dataSnapshot
                        for (DataSnapshot songSnapshot : snapshotMusic.getChildren()) {
                            Song song = songSnapshot.getValue(Song.class);
                            allSongs.add(song);
                        }
                        List<Song> songsInGenre = new ArrayList<>();
                        String theloai = currentSong.getGenre();

                        for (Song song : allSongs) {
                            if (song != null && theloai.equals(song.getGenre())) {
                                songsInGenre.add(song);
                            }
                        }
                        List<Song> recommendedSongs = new ArrayList<>();
                        Random random = new Random();
                        while (recommendedSongs.size() < numberOfSongs && !songsInGenre.isEmpty()) {
                            int randomIndex = random.nextInt(songsInGenre.size());
                            recommendedSongs.add(songsInGenre.remove(randomIndex));
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        mFragmentHomeBinding.rcvRecommendedSongs.setLayoutManager(linearLayoutManager);
                        SongAdapter songAdapter = new SongAdapter(recommendedSongs,song -> goToSong(song) );
                        mFragmentHomeBinding.rcvRecommendedSongs.setAdapter(songAdapter);
                    }
                }else{
//                    Không nghe
                    if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
                        Log.d("song", "ko có");
                        List<Song> allSongs = new ArrayList<>();

                        // Lặp qua tất cả các bài hát trong dataSnapshot
                        for (DataSnapshot songSnapshot : snapshotMusic.getChildren()) {
                            Song song = songSnapshot.getValue(Song.class);
                            allSongs.add(song);
                        }

                        List<Song> recommendedSongs = new ArrayList<>();
                        Random random = new Random();
                        while (recommendedSongs.size() < numberOfSongs && !allSongs.isEmpty()) {
                            int randomIndex = random.nextInt(allSongs.size());
                            recommendedSongs.add(allSongs.remove(randomIndex));
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        mFragmentHomeBinding.rcvRecommendedSongs.setLayoutManager(linearLayoutManager);
                        SongAdapter songAdapter = new SongAdapter(recommendedSongs,song -> goToSong(song) );
                        mFragmentHomeBinding.rcvRecommendedSongs.setAdapter(songAdapter);
                    }else {
                        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
                        List<Song> allSongs = new ArrayList<>();

                        // Lặp qua tất cả các bài hát trong dataSnapshot
                        for (DataSnapshot songSnapshot : snapshotMusic.getChildren()) {
                            Song song = songSnapshot.getValue(Song.class);
                            allSongs.add(song);
                        }
                        List<Song> songsInGenre = new ArrayList<>();
                        String theloai = currentSong.getGenre();

                        for (Song song : allSongs) {
                            if (song != null && theloai.equals(song.getGenre())) {
                                songsInGenre.add(song);
                            }
                        }
                        List<Song> recommendedSongs = new ArrayList<>();
                        Random random = new Random();
                        while (recommendedSongs.size() < numberOfSongs && !songsInGenre.isEmpty()) {
                            int randomIndex = random.nextInt(songsInGenre.size());
                            recommendedSongs.add(songsInGenre.remove(randomIndex));
                        }
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        mFragmentHomeBinding.rcvRecommendedSongs.setLayoutManager(linearLayoutManager);
                        SongAdapter songAdapter = new SongAdapter(recommendedSongs,song -> goToSong(song) );
                        mFragmentHomeBinding.rcvRecommendedSongs.setAdapter(songAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void goToSong(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }


}
