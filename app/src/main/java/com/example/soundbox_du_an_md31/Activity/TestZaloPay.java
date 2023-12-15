package com.example.soundbox_du_an_md31.Activity;

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
import com.example.soundbox_du_an_md31.Model.CreateOrderZaloPay;
import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import kotlinx.coroutines.GlobalScope;
import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class TestZaloPay extends AppCompatActivity {
    private AppCompatButton btn1month, btn6month, btn12month;
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
        btn1month = findViewById(R.id.premium_1month);
        btn6month = findViewById(R.id.premium_6month);
        btn12month = findViewById(R.id.premium_12month);
        btnBack = findViewById(R.id.icon_back);
        btn1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestZalo(10000);
            }
        });
        btn6month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestZalo(50000);
            }
        });
        btn12month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestZalo(100000);
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

    private void requestZalo(int amount) {
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
                        calendar.add(Calendar.MONTH, 1);
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