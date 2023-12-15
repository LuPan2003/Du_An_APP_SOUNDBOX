package com.example.soundbox_du_an_md31.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.soundbox_du_an_md31.Model.CreateOrder;
import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class TestZaloPay extends AppCompatActivity {

    private AppCompatButton btn1month,btn6month,btn12month;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_zalo_pay);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        initUI();

        btn1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra tài khoản isVIP trước khi thanh toán
                checkUserIsVIP(10000, 1);
            }
        });
        btn6month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra tài khoản isVIP trước khi thanh toán
                checkUserIsVIP(50000, 6);
            }
        });

        btn12month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra tài khoản isVIP trước khi thanh toán
                checkUserIsVIP(100000, 12);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestZaloPay.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showVIPToast() {
        Toast.makeText(TestZaloPay.this, "Tài khoản của bạn đã là tài khoản VIP.", Toast.LENGTH_SHORT).show();
    }
    private void checkUserIsVIP(int amount, int month) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference booleanRef = databaseRef.child("users/" + user.getUid() + "/isVIP");
            booleanRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Boolean isVIP = dataSnapshot.getValue(Boolean.class);
                    if (isVIP != null && isVIP) {
                        // Người dùng đã là VIP, hiển thị thông báo
                        showVIPToast(); // hoặc showVIPAlertDialog() nếu bạn muốn sử dụng AlertDialog
                    } else {
                        // Người dùng không phải là VIP, tiếp tục với quy trình thanh toán
                        requestZalo(amount, month);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý lỗi đọc dữ liệu từ cơ sở dữ liệu
                }
            });
        } else {
            // Người dùng không đăng nhập, xử lý tương ứng nếu cần
        }
    }
    private void initUI(){
        btn1month = findViewById(R.id.premium_1month);
        btn6month = findViewById(R.id.premium_6month);
        btn12month = findViewById(R.id.premium_12month);
        btnBack = findViewById(R.id.icon_back);
    }

    private void requestZalo(int amount, int month) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();



        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(String.valueOf(amount));
            String code = data.getString("return_code");

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                Log.d("zz",token);
                ZaloPaySDK.getInstance().payOrder(TestZaloPay.this, token, "demozalopay://app", new PayOrderListener() {

                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        DatabaseReference reference = database.getReference("users").child(user.getUid());
//                        Hien tai
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String currentDate = dateFormat.format(calendar.getTime());
//                        sau 1 tháng
                        calendar.add(Calendar.MONTH, month);
                        String newDate = dateFormat.format(calendar.getTime());
                        Map<String, Object> data = new HashMap<>();
                        data.put("isVIP", true);
                        data.put("startTime", currentDate);
                        data.put("endTime", newDate);
                        data.put("amount",amount);
                        reference.updateChildren(data);
                        Toast.makeText(TestZaloPay.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Toast.makeText(TestZaloPay.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Toast.makeText(TestZaloPay.this, "Lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            Log.d("zzz", "onPaymentSucceeded: ");
            e.printStackTrace();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}