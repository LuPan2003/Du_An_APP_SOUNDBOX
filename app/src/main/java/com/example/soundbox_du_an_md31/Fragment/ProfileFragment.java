package com.example.soundbox_du_an_md31.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class ProfileFragment extends Fragment {
    private ProgressDialog progressDialog;
    private Uri mUri;
    public static final String TAG = LibraryFragment.class.getName();
    private ImageView icon_back, img_avatarProfile, img_changeIMG,img_changeIMGVip;
    private TextView tv_name, tv_email,tv_daypremium;
    private AppCompatButton btn_change_information, btn_change_password, btn_exit, btn_premium,btn_change_background;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();

        icon_back = view.findViewById(R.id.icon_back);
        btn_change_information = view.findViewById(R.id.btn_change_infor);
        btn_change_password = view.findViewById(R.id.btn_change_password);
        btn_premium = view.findViewById(R.id.btn_premium);
        btn_change_background = view.findViewById(R.id.btn_ChangeColor);
        img_avatarProfile = view.findViewById(R.id.img_avatarProfile);
        tv_email = view.findViewById(R.id.tv_email);
        tv_name = view.findViewById(R.id.tv_name);
        tv_daypremium = view.findViewById(R.id.tv_daypremium);
        btn_exit = view.findViewById(R.id.btn_exits);
        img_changeIMG = view.findViewById(R.id.img_changeImage);
        img_changeIMGVip = view.findViewById(R.id.changeImageVip);

        progressDialog = new ProgressDialog(mainActivity);




        setProfile();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(mainActivity, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
        }
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

// Truy cập đến nút (node) cần kiểm tra
        DatabaseReference booleanRef = databaseRef.child("users/"+user.getUid()+"/isVIP");
        // Đọc giá trị boolean từ nút đó
        booleanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem giá trị có tồn tại hay không
                if (dataSnapshot.exists()) {
                    // Lấy giá trị boolean từ DataSnapshot
                    Boolean booleanValue = dataSnapshot.getValue(Boolean.class);

                    // Kiểm tra giá trị boolean
                    if (booleanValue == true) {
                        // Giá trị là true
                        // TODO: Xử lý khi giá trị là true
                       img_changeIMGVip.setVisibility(View.VISIBLE);
                        DatabaseReference endTimeRef = databaseRef.child("users/"+user.getUid()+"/endTime");
                        endTimeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d("zzz123", snapshot.toString());
                                String endTimeStr = snapshot.getValue(String.class);
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                Date endTime = null;
                                try {
                                    endTime = dateFormat.parse(endTimeStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                // Lấy thời điểm hiện tại
                                Calendar currentCalendar = Calendar.getInstance();
                                Date currentTime = currentCalendar.getTime();

                                // Tính số ngày giữa endTime và thời điểm hiện tại
                                long diffInMillis = Math.abs(currentTime.getTime() - endTime.getTime());
                                long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                                tv_daypremium.setText(String.valueOf(days) + " days");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.d("error", error.toString());
                            }
                        });
                    } else {
                        // Giá trị là false hoặc null

                        // TODO: Xử lý khi giá trị là false hoặc null
                        // Thành công
                        img_changeIMGVip.setVisibility(View.GONE);
                        tv_daypremium.setVisibility(View.GONE);

                    }
                } else {
                    // Nút không tồn tại trong database
                    // TODO: Xử lý khi nút không tồn tại

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc giá trị
            }
        });

        btn_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(mainActivity, MainActivity.class);
                startActivity(intent);
                mainActivity.finish();



            }
        });

//        btn_change_background.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean(DARK_MODE_KEY, !isDarkMode);
//                editor.apply();
//
//                // Áp dụng chế độ tối hoặc chế độ sáng
//                if (!isDarkMode) {
//                    appl
//                } else {
//                    applyLightMode();
//                }
//            }
//        });
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
        btn_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.gotoPremium();
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
            tv_email.setVisibility(View.VISIBLE);
            tv_email.setText(email);

        }else if(email == null){
            tv_name.setVisibility(View.GONE);
            tv_email.setVisibility(View.GONE);
        }else {
            tv_name.setVisibility(View.VISIBLE);
            tv_email.setVisibility(View.VISIBLE);
            tv_name.setText(name);
            tv_email.setText(email);
        }

        Glide.with(getContext()).load(photoUrl).override(120, 120).error(R.drawable.avata).into(img_avatarProfile);


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


    @Override
    public void onResume() {
        super.onResume();

    }
}