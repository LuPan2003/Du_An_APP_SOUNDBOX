package com.example.soundbox_du_an_md31.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Adapter.SongAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.FragmentPopularSongsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PopularSongsFragment extends Fragment {

    private FragmentPopularSongsBinding mFragmentPopularSongsBinding;
    private List<Song> mListSong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPopularSongsBinding = FragmentPopularSongsBinding.inflate(inflater, container, false);
        mFragmentPopularSongsBinding.layoutPlayAll.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.mListSongPlaying.addAll(mListSong);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
        getListPopularSongs();
        initListener();
        mFragmentPopularSongsBinding.iconBack.setOnClickListener(v -> backHome());
        mFragmentPopularSongsBinding.sharePlay.setOnClickListener(v -> sharePlaylist());
        return mFragmentPopularSongsBinding.getRoot();
    }
    private void sharePlaylist() {
        // Tạo một Intent để chia sẻ thông tin danh sách nhạc
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, generatePlaylistShareText());

        // Bắt đầu Activity chia sẻ
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ danh sách nhạc"));
    }
    private String generatePlaylistShareText() {
        StringBuilder shareText = new StringBuilder("Chia sẽ danh sách nhạc RAP:\n");

        for (Song song : mListSong) {
            shareText.append(song.getTitle()).append(" - ").append(song.getArtist()).append("\n");
            shareText.append(song.getUrl()).append("\n");
        }

        return shareText.toString();
    }
    private void backHome() {
        if (getActivity() == null) {
            return;
        }

        // Lấy FragmentManager
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Tạo một FragmentTransaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Thêm HomeFragment vào ngăn xếp và chuyển về nó
        fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
        fragmentTransaction.addToBackStack(null); // Để có thể quay lại từ HomeFragment

        // Thực hiện Transaction
        fragmentTransaction.commit();
    }

    private void getListPopularSongs() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) {
                        return;
                    }
                    if (song.getCount() > 0) {
                        mListSong.add(song);
                    }
                }
                Collections.sort(mListSong, (song1, song2) -> song2.getCount() - song1.getCount());
                displayListPopularSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListPopularSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentPopularSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        mFragmentPopularSongsBinding.rcvData.setAdapter(songAdapter);
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

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
    }
}
