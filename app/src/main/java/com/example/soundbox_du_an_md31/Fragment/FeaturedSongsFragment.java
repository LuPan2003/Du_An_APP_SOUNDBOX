package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.example.soundbox_du_an_md31.databinding.FragmentFeaturedSongsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeaturedSongsFragment extends Fragment {

    private FragmentFeaturedSongsBinding mFragmentFeaturedSongsBinding;
    private List<Song> mListSong;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentFeaturedSongsBinding = FragmentFeaturedSongsBinding.inflate(inflater, container, false);

        getListFeaturedSongs();
        initListener();

        return mFragmentFeaturedSongsBinding.getRoot();
    }

    private void getListFeaturedSongs() {
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
                    if (song.isFeatured()) {
                        mListSong.add(0, song);
                    }
                }
                displayListFeaturedSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListFeaturedSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentFeaturedSongsBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        mFragmentFeaturedSongsBinding.rcvData.setAdapter(songAdapter);
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
//        activity.getActivityMainBinding().header.layoutPlayAll.setOnClickListener(v -> {
//            MusicService.clearListSongPlaying();
//            MusicService.mListSongPlaying.addAll(mListSong);
//            MusicService.isPlaying = false;
//            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
//            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
//        });
    }
}
