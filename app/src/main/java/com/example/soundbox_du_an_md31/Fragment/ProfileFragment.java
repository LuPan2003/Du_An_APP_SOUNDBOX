package com.example.soundbox_du_an_md31.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.soundbox_du_an_md31.Activity.MainActivity;
import com.example.soundbox_du_an_md31.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class ProfileFragment extends Fragment {
    private ProgressDialog progressDialog;
    private Uri mUri;
    public static final String TAG = LibraryFragment.class.getName();
    private ImageView icon_back, img_avatarProfile, img_changeIMG;
    private TextView tv_name, tv_email;
    private AppCompatButton btn_change_information, btn_change_password, btn_exit;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();
        icon_back = view.findViewById(R.id.icon_back);
        btn_change_information = view.findViewById(R.id.btn_change_infor);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        img_avatarProfile = view.findViewById(R.id.img_avatarProfile);
        tv_email = view.findViewById(R.id.tv_email);
        tv_name = view.findViewById(R.id.tv_name);
        btn_exit = view.findViewById(R.id.btn_exits);
        img_changeIMG = view.findViewById(R.id.img_changeImage);
        progressDialog = new ProgressDialog(mainActivity);


        setProfile();

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                mainActivity.finish();

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
        btn_change_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.gotoChangeInformation();
            }
        });
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.gotoChangePassword();
            }
        });
        initListener();
        return view;
    }

    private void setProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        Uri photoUrl = user.getPhotoUrl();
        Log.d("image1", "setProfile: "+ photoUrl);

        if (name == null) {
            tv_name.setVisibility(View.GONE);
            tv_email.setVisibility(View.GONE);

        } else {
            tv_name.setVisibility(View.VISIBLE);
            tv_email.setVisibility(View.VISIBLE);
            tv_name.setText(name);
            tv_email.setText(email);
        }

        Glide.with(getContext()).load(photoUrl).error(R.drawable.avata).into(img_avatarProfile);


    }

    private void initListener() {
        img_changeIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRequestPermission();
            }
        });
    }

    private void onClickRequestPermission() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mainActivity.openGallery();
            return;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            mainActivity.openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MainActivity.MY_REQUEST_CODE);
        }
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        img_avatarProfile.setImageBitmap(imageBitmap);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }
        progressDialog.show();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(mUri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(mainActivity, "Cập nhật avatar thành công", Toast.LENGTH_SHORT).show();
                            setProfile();
                        }
                    }
                });
    }
    public void setUri(Uri mUri){
        this.mUri = mUri;
    }




}