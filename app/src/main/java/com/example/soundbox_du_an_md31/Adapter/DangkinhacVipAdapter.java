package com.example.soundbox_du_an_md31.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.soundbox_du_an_md31.Fragment.FeedbackFragment;
import com.example.soundbox_du_an_md31.Fragment.NhacVipFragment;

public class DangkinhacVipAdapter extends FragmentStateAdapter {

    public DangkinhacVipAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new NhacVipFragment();
        } else {
            return new FeedbackFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
