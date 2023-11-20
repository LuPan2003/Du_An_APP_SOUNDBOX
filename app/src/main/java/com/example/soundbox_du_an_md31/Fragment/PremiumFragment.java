package com.example.soundbox_du_an_md31.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


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
        btn_premium1month = view.findViewById(R.id.premium_1month);

        btn_premium1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Thông báo");
                builder.setMessage("Bạn có muốn mua gói premium 1 tháng không?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Random random = new Random();
                        int randomNumber = random.nextInt(10) + 1;

                        if(randomNumber % 2 == 0){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("isVIP", true);
                            updates.put("time", 1);
                            ref.updateChildren(updates);
                            Toast.makeText(getActivity(), "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentManager.getBackStackEntryCount() > 0) {
                                fragmentManager.popBackStack();
                            }
                        }else{
                            Toast.makeText(getActivity(), "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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