package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.soundbox_du_an_md31.databinding.FragmentNhacVipBinding;

public class NhacVipFragment extends Fragment {
    private FragmentNhacVipBinding myBinding;
    public static final String TAG = FragmentNhacVipBinding.class.getName();

    public NhacVipFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nạp Binding
       myBinding = FragmentNhacVipBinding.inflate(inflater, container, false);
        View rootView = myBinding.getRoot();
   

        // Trả về rootView từ Binding
        return rootView;
    }
}
