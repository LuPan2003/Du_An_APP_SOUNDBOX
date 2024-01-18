package com.example.soundbox_du_an_md31.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.soundbox_du_an_md31.Fragment.ListSongPlayingFragment;
import com.example.soundbox_du_an_md31.Fragment.PlayMusicDownFragment;

public class MusicViewPagerDownAdapter extends FragmentStateAdapter {
    public MusicViewPagerDownAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ListSongPlayingFragment();
        }
        return new PlayMusicDownFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
