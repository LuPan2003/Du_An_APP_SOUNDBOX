package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.Activity.PlayMusicActivity;
import com.example.soundbox_du_an_md31.Activity.PlayMusicDownActivity;
import com.example.soundbox_du_an_md31.Adapter.DownloadAdapter;
import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.example.soundbox_du_an_md31.Dao.Dao;
import com.example.soundbox_du_an_md31.Model.SongDown;
import com.example.soundbox_du_an_md31.Service.MyServiceDown;
import com.example.soundbox_du_an_md31.databinding.FragmentDownLoadBinding;

import java.util.List;

public class DownLoadFragment extends Fragment {

    private FragmentDownLoadBinding fragmentDownLoadBinding;

    private Dao mDao;

    private DownloadAdapter adapter;


    private List<SongDown> mSongDown;

    public DownLoadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentDownLoadBinding = FragmentDownLoadBinding.inflate(inflater, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentDownLoadBinding.recyclerviewDownload.setLayoutManager(linearLayoutManager);

        this.mDao = new Dao(getActivity());
        this.mSongDown = this.mDao.getListSongDown();
        this.adapter = new DownloadAdapter(mSongDown,this::goToSongDetail);
        showData();
        initListener();
        return fragmentDownLoadBinding.getRoot();
    }

    public void showData() {
        this.adapter.setData(mSongDown);
        fragmentDownLoadBinding.recyclerviewDownload.setAdapter(adapter);
    }

    private void displayListAllSongs() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        fragmentDownLoadBinding.recyclerviewDownload.setLayoutManager(linearLayoutManager);


    }
    //được sử dụng để chuyển đến giao diện chi tiết của một bài hát.
    private void goToSongDetail(@NonNull SongDown song) {
        MyServiceDown.clearListSongPlaying();
        MyServiceDown.mListSongPlaying1.add(song);
        MyServiceDown.isPlaying = false;
        GlobalFuntion.startMusicServiceDown(getActivity(), Constant.PLAY, 0);
        GlobalFuntion.startActivity(getActivity(), PlayMusicDownActivity.class);
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
            MyServiceDown.clearListSongPlaying();
            MyServiceDown.mListSongPlaying1.addAll(mSongDown);
            MyServiceDown.isPlaying = false;
            GlobalFuntion.startMusicServiceDown(getActivity(), Constant.PLAY, 0);
            GlobalFuntion.startActivity(getActivity(), PlayMusicDownActivity.class);
        });


    }
}