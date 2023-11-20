package com.example.soundbox_du_an_md31.Fragment;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.soundbox_du_an_md31.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vn.momo.momo_partner.AppMoMoLib;

public class PremiumFragment extends Fragment {

    private ImageView icon_back;

    public static final String TAG = PremiumFragment.class.getName();

    private AppCompatButton btn_premium1month;

//    private String amount = "10000";
//    private String fee = "0";
//    int environment = 0;//developer default
//    private String merchantName = "Go to premium";
//    private String merchantCode = "SCB01";
//    private String merchantNameLabel = "SoundBox";
//    private String description = "Gói nghe nhạc vip";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_premium, container, false);
        icon_back = view.findViewById(R.id.icon_back);
        btn_premium1month = view.findViewById(R.id.premium_1month);

        AppMoMoLib.getInstance().setEnvironment(AppMoMoLib.ENVIRONMENT.DEVELOPMENT); // AppMoMoLib.ENVIRONMENT.PRODUCTION
        btn_premium1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    //Get token through MoMo app

}