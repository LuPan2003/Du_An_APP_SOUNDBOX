package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.R;

public class LibraryFragment extends Fragment {
    private MainActivity mainActivity;
    private ImageView profile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_library, container, false);
        mainActivity = (MainActivity) getActivity();
        profile = view.findViewById(R.id.icon_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.gotoProfile();
            }
        });
        return view;
    }

}