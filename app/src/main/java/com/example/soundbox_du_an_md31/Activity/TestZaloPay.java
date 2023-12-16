package com.example.soundbox_du_an_md31.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
                requestZalo(10000,1);
            }
        });
        btn6month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestZalo(50000,6);
            }
        });
        btn12month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestZalo(100000,12);
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
                        if(user == null){
                            Toast.makeText(getApplicationContext(), "Bạn đang nghe nhạc với tư cách khách", Toast.LENGTH_SHORT).show();
                        }else {
                            DatabaseReference reference = database.getReference("users").child(user.getUid());
                            reference.addValueEventListener(new ValueEventListener() {
                                private boolean isDataHandled = false;
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!isDataHandled) {
                                        // Thực hiện xử lý dữ liệu ở đây
                                        Log.d("zzzz",snapshot.toString());
                                        Boolean isVIP = snapshot.child("isVIP").getValue(Boolean.class);
                                        // Kiểm tra kiểu giá trị "isVIP"
                                        if(isVIP == true) {
                                            Log.d("zzzz","true");
                                            // Giá trị "isVIP" là true
                                            Object value = snapshot.getValue();
                                            if (value instanceof HashMap) {
                                                DatabaseReference ref1 = database.getReference("users").child(user.getUid());
                                                HashMap<String, Object> hashMapValue = (HashMap<String, Object>) value;
                                                String endTimeStr = (String) hashMapValue.get("endTime");
                                                Log.d("time", String.valueOf(endTimeStr));
                                                long amountht ;
                                                if(snapshot.child("amount").getValue(Long.class) != null){
                                                    amountht = snapshot.child("amount").getValue(Long.class);
                                                }else{
                                                    amountht = 0;
                                                }
                                                Log.d("amount", String.valueOf(amountht));
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                Date endTime = null;
                                                try {
                                                    endTime = dateFormat.parse(endTimeStr);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
//                                            Thêm số tháng vào giá trị endTime
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTime(endTime);
                                                calendar.add(Calendar.MONTH, month); // Thêm số tháng vào giá trị endTime
//                                            Lấy giá trị endTime mới
                                                String newEndTime = dateFormat.format(calendar.getTime());

//                                            amount mới
                                                int newamount = (int) (amountht + amount);
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("isVIP", true);
                                                data.put("endTime", newEndTime);
                                                data.put("amount",newamount);
                                                ref1.updateChildren(data);
                                                Toast.makeText(TestZaloPay.this, "Oke", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            DatabaseReference ref2 = database.getReference("users").child(user.getUid());
                                            Log.d("zzzz","fasle");
                                            // Giá trị "isVIP" là false hoặc null
                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            String currentDate = dateFormat.format(calendar.getTime());
//                                      sau ? tháng
                                            calendar.add(Calendar.MONTH, month);
                                            String newDate = dateFormat.format(calendar.getTime());
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("isVIP", true);
                                            data.put("startTime", currentDate);
                                            data.put("endTime", newDate);
                                            data.put("amount",amount);
                                            ref2.updateChildren(data);
                                            Toast.makeText(TestZaloPay.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                                        }
                                        isDataHandled = true;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.d("error",error.toString());
                                }
                            });
                        }
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