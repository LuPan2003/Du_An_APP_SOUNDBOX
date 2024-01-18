package com.example.soundbox_du_an_md31.Fragment;

import static com.example.soundbox_du_an_md31.Fragment.ChangeInformationFragment.TAG;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.session.MediaController;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Adapter.DownloadAdapter;
import com.example.soundbox_du_an_md31.Adapter.SongAdapter;
import com.example.soundbox_du_an_md31.Adapter.SongPlayingAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Model.Song;
import com.example.soundbox_du_an_md31.Model.SongDown;
import com.example.soundbox_du_an_md31.MyApplication;
import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicService;
import com.example.soundbox_du_an_md31.Service.MyServiceDown;
import com.example.soundbox_du_an_md31.databinding.FragmentListSongPlayingBinding;
import com.example.soundbox_du_an_md31.databinding.FragmentListSongPlayingFragmentBinding;
import com.example.soundbox_du_an_md31.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ListSongPlayingFragmentFragment extends Fragment {

    private MediaController mediaController;
    private FragmentListSongPlayingFragmentBinding mFragmentListSongPlayingBinding;
    private SongPlayingAdapter mSongPlayingAdapter;
    private List<SongDown> mListSong;
    public ListSongPlayingFragmentFragment() {
        // Required empty public constructor
    }


    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatusListSongPlaying();
        }
    };
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentListSongPlayingBinding = FragmentListSongPlayingFragmentBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        displayListSongPlaying();
        getListSongFromFirebase("");
        getListAllSongs();
        initListener();
        return mFragmentListSongPlayingBinding.getRoot();
    }

    private void getListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SongDown song = dataSnapshot.getValue(SongDown.class);
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
    }
    private void displayListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentListSongPlayingBinding.rcvData.setLayoutManager(linearLayoutManager);
        DownloadAdapter songAdapter = new DownloadAdapter(mListSong, this::goToSongDetail);
        mFragmentListSongPlayingBinding.rcvData.setAdapter(songAdapter);
    }
    private void goToSongDetail(@NonNull SongDown song) {
        MyServiceDown.clearListSongPlaying();
        MyServiceDown.mListSongPlaying1.add(song);
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicServiceDown(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicActivity.class);
    }

    // hiển thị danh sách các bài hát đang phát lên
    private void displayListSongPlaying() {
        if (getActivity() == null || MusicService.mListSongPlaying == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentListSongPlayingBinding.rcvData.setLayoutManager(linearLayoutManager);

        mSongPlayingAdapter = new SongPlayingAdapter(MusicService.mListSongPlaying, this::clickItemSongPlaying);
        mFragmentListSongPlayingBinding.rcvData.setAdapter(mSongPlayingAdapter);

        updateStatusListSongPlaying();
    }


    //Sự kiện tìm kiếm bài hát
    private void initListener() {
        //Sự kiện tìm kiếm bài hát
        mFragmentListSongPlayingBinding.imgSearchAllList.setOnClickListener(view -> searchSong());
        mFragmentListSongPlayingBinding.edtSearchNameList.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
                return true;
            }
            return false;
        });

        mFragmentListSongPlayingBinding.edtSearchNameList.addTextChangedListener(new TextWatcher() {
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
    }

    private void getListSongFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        MyApplication.get(getActivity()).getSongsDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFragmentListSongPlayingBinding.layoutContentAll.setVisibility(View.VISIBLE);
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SongDown song = dataSnapshot.getValue(SongDown.class);
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
                displayListNewSongs();
            }
            //được sử dụng để chuyển đến giao diện chi tiết của một bài hát.
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
        mFragmentListSongPlayingBinding.rcvData.setLayoutManager(linearLayoutManager);

        DownloadAdapter songAdapter = new DownloadAdapter(getListNewSongs(), this::goToSongDetail);
        mFragmentListSongPlayingBinding.rcvData.setAdapter(songAdapter);
    }
    private List<SongDown> getListNewSongs() {
        List<SongDown> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        for (SongDown song : mListSong) {
            if (song.isLatest() && list.size() < Constant.MAX_COUNT_LATEST) {
                list.add(song);
            }
        }
        return list;
    }
    //cập nhật trạng thái của danh sách các bài hát đang phát lên.
    // Hàm này sẽ kiểm tra vị trí bài hát hiện tại và cập nhật trạng thái của các bài hát trong danh sách.
    @SuppressLint("NotifyDataSetChanged")
    private void updateStatusListSongPlaying() {
        if (getActivity() == null || MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        for (int i = 0; i < MusicService.mListSongPlaying.size(); i++) {
            MusicService.mListSongPlaying.get(i).setPlaying(i == MusicService.mSongPosition);
        }
        mSongPlayingAdapter.notifyDataSetChanged();
    }
    //được gọi khi người dùng nhấp vào một bài hát trong danh sách các bài hát đang phát lên.
    // Hàm này sẽ dừng dịch vụ âm nhạc và khởi động lại dịch vụ âm nhạc với bài hát được chọn.
    private void clickItemSongPlaying(int position) {
        MusicService.isPlaying = false;
        GlobalFuntion.startMusicService(getActivity(), Constant.PLAY, position);

    }
    private void searchSong() {
        String strKey = mFragmentListSongPlayingBinding.edtSearchNameList.getText().toString().trim();
        if (mListSong != null) mListSong.clear();
        Log.d(TAG, "searchSong: zzzzzzzzzzzzzz");
        getListSongFromFirebase(strKey);
        GlobalFuntion.hideSoftKeyboard(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }
}

