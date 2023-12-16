package com.example.soundbox_du_an_md31.Fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.soundbox_du_an_md31.databinding.FragmentKoreanmusicBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class KoreanmusicFragment extends Fragment {
    private FragmentKoreanmusicBinding fragmentKoreanmusicBinding;
    private List<Song> mListSong;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentKoreanmusicBinding = FragmentKoreanmusicBinding.inflate(inflater, container, false);
        fragmentKoreanmusicBinding.layoutPlayAll.setOnClickListener(v -> {
            MusicService.clearListSongPlaying();
            MusicService.mListSongPlaying.addAll(mListSong);
            MusicService.isPlaying = false;
            GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
        });
        getListNewSongs();
        initListener();
        fragmentKoreanmusicBinding.iconBackHan.setOnClickListener(v -> backHome());
        fragmentKoreanmusicBinding.sharePlay.setOnClickListener(v -> sharePlaylist());
        fragmentKoreanmusicBinding.btnDownload.setOnClickListener(v -> download());
        return fragmentKoreanmusicBinding.getRoot();
    }
    private void checkUserIsVIP(final PlaySongFragment.Callback<Boolean> callback) {
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

    private interface Callback<T> {
        void onResult(T result);
    }

    private void download() {
        checkUserIsVIP(new PlaySongFragment.Callback<Boolean>() {
            @Override
            public void onResult(Boolean isVipUser) {
                if (!isVipUser) {
                    // Hiển thị thông báo cho người dùng không phải là VIP
                    Toast.makeText(getActivity(), "Đăng ký gói premium để được tải nhạc", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem danh sách bài hát có hợp lệ không
                if (mListSong == null || mListSong.isEmpty()) {
                    Toast.makeText(getActivity(), "Danh sách bài hát trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lặp qua danh sách bài hát và tải xuống mỗi bài hát
                for (Song song : mListSong) {
                    // Kiểm tra xem bài hát có URL hợp lệ không
                    if (song == null || TextUtils.isEmpty(song.getUrl())) {
                        Toast.makeText(getActivity(), "Không thể tải bài hát này", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sử dụng DownloadManager để tải bài hát
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(song.getUrl()));
                    request.setTitle(song.getTitle());
                    request.setDescription("Đang tải bài hát");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, song.getTitle() + ".mp3");

                    DownloadManager downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
                    } else {
                        Toast.makeText(getActivity(), "Không thể tạo yêu cầu tải xuống", Toast.LENGTH_SHORT).show();
                    }
                }

                Toast.makeText(getActivity(), "Đang tải xuống...", Toast.LENGTH_SHORT).show();
            }
        });
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
        StringBuilder shareText = new StringBuilder("Chia sẽ danh sách nhạc Korea:\n");

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
                    if (song != null && "Korea".equals(song.getNation())) {
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
        fragmentKoreanmusicBinding.rcvData.setLayoutManager(linearLayoutManager);

        SongAdapter songAdapter = new SongAdapter(mListSong, this::goToSongDetail);
        fragmentKoreanmusicBinding.rcvData.setAdapter(songAdapter);
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