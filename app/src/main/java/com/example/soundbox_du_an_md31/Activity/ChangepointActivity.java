package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soundbox_du_an_md31.Model.RankingItem;
import com.example.soundbox_du_an_md31.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChangepointActivity extends AppCompatActivity {
    private RankingAdapter rankingAdapter;
    private AppCompatButton btn1month,btn6month,btn12month;
    private ImageView btnBack;
    private TextView tv_point;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepoint);
        btn1month = findViewById(R.id.premium_1month_point);
        btn6month = findViewById(R.id.premium_6month_point);
        btn12month = findViewById(R.id.premium_12month_point);
        tv_point = findViewById(R.id.tv_point);
        btnBack = findViewById(R.id.icon_back_point);

        getPoint();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangepointActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn1month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xữ lý 1 tháng
                requestPoint(500,1);
            }
        });
        btn6month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xữ lý 6 tháng
                requestPoint(1500,6);
            }
        });
        btn12month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xữ lý 1 năm
                requestPoint(3000,12);
            }
        });

    }

    private void getPoint(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("rankings").child(user.getUid());
        if(user != null){
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // hành động point
                        Log.d("data", snapshot.toString());
                        long pointht = snapshot.child("point").getValue(Long.class);
                        Log.d("pointht", String.valueOf(pointht));
                        tv_point.setText(String.valueOf(pointht) + " ĐIỂM");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("error", error.toString());
                }
            });
        }
    }
    private void requestPoint(int point, int month){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("rankings").child(user.getUid());
        if(user != null){
            reference.addValueEventListener(new ValueEventListener() {
                private boolean isDataHandled = false;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!isDataHandled){
                        // hành động point
                        Log.d("data", snapshot.toString());
                        long pointht = snapshot.child("point").getValue(Long.class);
                        Log.d("pointht", String.valueOf(pointht));

                        if(pointht >= point){
                            // đăng ký
                            DatabaseReference userVIP = database.getReference("users").child(user.getUid());
                            userVIP.addValueEventListener(new ValueEventListener() {
                                private boolean isDataHandledAbc = false;
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!isDataHandledAbc){
                                        // Check vip
                                        Boolean isVIP = snapshot.child("isVIP").getValue(Boolean.class);
                                        if(isVIP == true) {
//                                        Nếu là vip
                                            Object value = snapshot.getValue();
                                            if (value instanceof HashMap) {
                                                DatabaseReference ref1 = database.getReference("users").child(user.getUid());
                                                HashMap<String, Object> hashMapValue = (HashMap<String, Object>) value;
                                                String endTimeStr = (String) hashMapValue.get("endTime");
                                                Log.d("time", String.valueOf(endTimeStr));
                                                long pointbandau;
                                                if(snapshot.child("point").getValue(Long.class) != null){
                                                    pointbandau = snapshot.child("point").getValue(Long.class);
                                                }else{
                                                    pointbandau = 0;
                                                }
                                                Log.d("amount", String.valueOf(pointbandau));
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

//                                            point mới
                                                long newPoint = pointbandau + point;
                                                //Point
                                                Map<String, Object> dataPoint = new HashMap<>();
                                                long pointRank = pointht - point;
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("isVIP", true);
                                                data.put("endTime", newEndTime);
                                                data.put("point",newPoint);
                                                dataPoint.put("point",pointRank);
                                                ref1.updateChildren(data);
                                                reference.updateChildren(dataPoint);
                                                Toast.makeText(ChangepointActivity.this, "Thành công", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(ChangepointActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }else{
//                                        Không phải vip
                                            Calendar calendar = Calendar.getInstance();
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                            String currentDate = dateFormat.format(calendar.getTime());
//                                      sau ? tháng
                                            calendar.add(Calendar.MONTH, month);
                                            String newDate = dateFormat.format(calendar.getTime());
//                                        Point
                                            long pointbandau = snapshot.child("point").getValue(Long.class);
                                            long pointUser = point +pointbandau;
                                            Map<String, Object> dataPoint = new HashMap<>();
                                            long newPoint = pointht - point;
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("isVIP", true);
                                            data.put("startTime", currentDate);
                                            data.put("endTime", newDate);
                                            data.put("point",pointUser);
                                            dataPoint.put("point",newPoint);
                                            userVIP.updateChildren(data);
                                            reference.updateChildren(dataPoint);
                                            Toast.makeText(ChangepointActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChangepointActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                        isDataHandledAbc = true;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else{
                            Toast.makeText(ChangepointActivity.this, "Số point không đủ, vui lòng xem thêm quảng cáo", Toast.LENGTH_SHORT).show();
                        }
                        isDataHandled = true;
                    }else{
                        Log.d("fail", "Lỗi");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("error", error.toString());
                }
            });
        }
    }

}
