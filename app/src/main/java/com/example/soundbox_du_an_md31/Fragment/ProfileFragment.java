package com.example.soundbox_du_an_md31.Fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.util.Base64;
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
import com.example.soundbox_du_an_md31.utils.GlideUtils;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    private static final int PICK_IMAGE_REQUEST = 1;



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
                                long diffInMillis = endTime.getTime() - currentTime.getTime();
                                long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) +1;

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
        img_changeIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child("avatar");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
//                    String base64Image = snapshot.toString();
//                    img_avatarProfile.setImageURI(Uri.parse(base64Image));
                    Log.d("quy1", snapshot.toString());
                    String base64Image = (String) snapshot.getValue();
                    byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    img_avatarProfile.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            String base64Image = convertImageToBase64(selectedImageUri);
            if (base64Image != null) {
                // Sử dụng chuỗi Base64 theo ý của bạn
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference avaRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
                avaRef.addValueEventListener(new ValueEventListener() {
                    private  boolean isHandle = false;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!isHandle){
                            Map<String, Object> data = new HashMap<>();
                            data.put("avatar", base64Image);
                            avaRef.updateChildren(data);
                            Log.d("quy",base64Image);
                            isHandle = true;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
    private String convertImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setUri(Uri mUri){
        this.mUri = mUri;
    }


    @Override
    public void onResume() {
        super.onResume();

    }
}