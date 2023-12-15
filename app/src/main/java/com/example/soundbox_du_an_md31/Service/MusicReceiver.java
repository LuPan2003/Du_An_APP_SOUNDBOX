package com.example.soundbox_du_an_md31.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.soundbox_du_an_md31.Constant.Constant;
import com.example.soundbox_du_an_md31.Constant.GlobalFuntion;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getExtras().getInt(Constant.MUSIC_ACTION);
        GlobalFuntion.startMusicService(context, action, MusicService.mSongPosition);
        // Xóa các đối tượng từ bảng Realtime Database

    }
    }
