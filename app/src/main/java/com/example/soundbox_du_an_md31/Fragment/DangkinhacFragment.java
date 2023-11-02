package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.soundbox_du_an_md31.Adapter.DangkinhacVipAdapter;
import com.example.soundbox_du_an_md31.databinding.FragmentDangkinhacBinding;
import com.example.soundbox_du_an_md31.R;
import com.google.android.material.tabs.TabLayoutMediator;


public class DangkinhacFragment extends Fragment {
    private FragmentDangkinhacBinding binding;
    public static final String TAG = DangkinhacFragment.class.getName();

    public DangkinhacFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nạp Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dangkinhac, container, false);
        View rootView = binding.getRoot();


        DangkinhacVipAdapter adapter = new DangkinhacVipAdapter(this);
        binding.viewPager.setAdapter(adapter);


        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Plus");
            } else if (position == 1) {
                tab.setText("Premium");
            }
        }).attach();


        // Trả về rootView từ Binding
        return rootView;
    }
}
