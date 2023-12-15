package com.example.soundbox_du_an_md31.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.soundbox_du_an_md31.Activity.TestZaloPay;
import com.example.soundbox_du_an_md31.R;


public class PremiumFragment extends Fragment {

    private ImageView icon_back;

    public static final String TAG = PremiumFragment.class.getName();

    private AppCompatButton btn_premium1month;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premium, container, false);
        icon_back = view.findViewById(R.id.icon_back);
        btn_premium1month = view.findViewById(R.id.btnZalopay);

        btn_premium1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TestZaloPay.class);
                startActivity(intent);
            }
        });

        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
        return view;
    }

}