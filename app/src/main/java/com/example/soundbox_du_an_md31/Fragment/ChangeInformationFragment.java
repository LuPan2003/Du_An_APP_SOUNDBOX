package com.example.soundbox_du_an_md31.Fragment;



import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.soundbox_du_an_md31.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class ChangeInformationFragment extends Fragment {
    private ProgressDialog progressDialog;
    private ImageView icon_back;
    private Button btn_updateProfile;
    private EditText edt_phone, edt_name;
    public static final String TAG = ChangeInformationFragment.class.getName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_information, container, false);
        icon_back = view.findViewById(R.id.icon_back);
        edt_name = view.findViewById(R.id.edt_nameUD);
        edt_phone = view.findViewById(R.id.edt_nunmberphoneUD);
        btn_updateProfile = view.findViewById(R.id.btn_updateProfile);

        progressDialog = new ProgressDialog(getActivity());
        setProfile();


        icon_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                }
            }
        });
        btn_updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
        return view;
    }
    private void updateProfile(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        if (edt_name.getText().length() == 0 || edt_phone.getText().length() ==0){
            Toast.makeText(getActivity(), "Không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("name", edt_name.getText().toString());
        data.put("numberphone", edt_phone.getText().toString());
        ref.updateChildren(data);

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(edt_name.getText().toString().trim())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Cập nhật profile thành công", Toast.LENGTH_SHORT).show();
                            getFragmentManager().popBackStack();
                        }
                    }
                });




    }

    private void setProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String numberphone = snapshot.child("numberphone").getValue(String.class);
                edt_phone.setText(numberphone);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        edt_name.setText(user.getDisplayName());
    }
}