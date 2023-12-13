package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Adapter.SongAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.databinding.FragmentHousemusicBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HousemusicFragment extends Fragment {
    private FragmentHousemusicBinding fragmentHousemusicBinding;
    private List<Song> mListSong;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHousemusicBinding = FragmentHousemusicBinding.inflate(inflater, container, false);
        getListNewSongs();
        fragmentHousemusicBinding.iconBackNhachouse.setOnClickListener(v -> backHome());
        return fragmentHousemusicBinding.getRoot();
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
    private void getListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListSong == null) {
                    mListSong = new ArrayList<>();
                } else {
                    mListSong.clear();
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song != null && "Blues".equals(song.getGenre()) || "Reggae".equals(song.getGenre())) {
                        mListSong.add(song);
                    }
                }

                displayListNewSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFuntion.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }
    private void displayListNewSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentHousemusicBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        fragmentHousemusicBinding.rcvData.setAdapter(songAdapter);
    }
    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }
}