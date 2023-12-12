package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChangepointActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RankingAdapter rankingAdapter;
    private AppCompatButton btn1month,btn6month,btn12month;
    private ImageView btnBack;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepoint);
        btn1month = findViewById(R.id.premium_1month_point);
        btn6month = findViewById(R.id.premium_6month_point);
        btn12month = findViewById(R.id.premium_12month_point);
        btnBack = findViewById(R.id.icon_back_point);
        recyclerView = findViewById(R.id.rcv_data);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Lấy UserId của người dùng đăng nhập
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        if (userId != null) {
            // Gọi hàm để lấy thông tin người dùng từ bảng rankings
            getUserRankingInfo(userId);
        }


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
                Changepointsfor1month();
            }
        });
        btn6month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xữ lý 6 tháng
                Changepointsfor6month();
            }
        });
        btn12month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //xữ lý 1 năm
                Changepointsfor1year();
            }
        });

    }
    private void Changepointsfor1month() {
        // Lấy UserId của người dùng đăng nhập
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        if (userId != null) {
            // Lấy điểm hiện tại từ bảng rankings
            DatabaseReference rankingRef = FirebaseDatabase.getInstance().getReference("rankings").child(userId);

            rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Người dùng có trong bảng rankings
                        int userPoints = dataSnapshot.child("point").getValue(Integer.class);

                        // Lấy trạng thái VIP từ bảng users
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                                if (userDataSnapshot.exists()) {
                                    boolean isVIP = userDataSnapshot.child("isVIP").getValue(Boolean.class);

                                    // Kiểm tra điểm có đủ để đổi VIP không và kiểm tra trạng thái VIP
                                    if (!isVIP) {
                                        // User is not VIP, perform VIP upgrade
                                        performVIPUpgrade(userId, userPoints, 500, 1); // 1 tháng
                                    } else {
                                        // User is already a VIP, display a message or handle as needed
                                        showAlreadyVIPMessage();
                                    }
                                } else {
                                    // Người dùng không có trong bảng users
                                    // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                                    showNoUserInUsersMessage();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError userError) {
                                // Xử lý lỗi nếu cần
                            }
                        });
                    } else {
                        // Người dùng không có trong bảng rankings
                        // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                        showNoUserInRankingsMessage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }

    private void Changepointsfor1year() {
        // Lấy UserId của người dùng đăng nhập
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        if (userId != null) {
            // Lấy điểm hiện tại từ bảng rankings
            DatabaseReference rankingRef = FirebaseDatabase.getInstance().getReference("rankings").child(userId);

            rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Người dùng có trong bảng rankings
                        int userPoints = dataSnapshot.child("point").getValue(Integer.class);

                        // Lấy trạng thái VIP từ bảng users
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                                if (userDataSnapshot.exists()) {
                                    boolean isVIP = userDataSnapshot.child("isVIP").getValue(Boolean.class);

                                    // Kiểm tra điểm có đủ để đổi VIP không và kiểm tra trạng thái VIP
                                    if (!isVIP) {
                                        // User is not VIP, perform VIP upgrade
                                        performVIPUpgrade(userId, userPoints, 30000, 12); // 12 tháng
                                    } else {
                                        // User is already a VIP, display a message or handle as needed
                                        showAlreadyVIPMessage();
                                    }
                                } else {
                                    // Người dùng không có trong bảng users
                                    // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                                    showNoUserInUsersMessage();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError userError) {
                                // Xử lý lỗi nếu cần
                            }
                        });
                    } else {
                        // Người dùng không có trong bảng rankings
                        // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                        showNoUserInRankingsMessage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }

    private void Changepointsfor6month() {
        // Lấy UserId của người dùng đăng nhập
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = (user != null) ? user.getUid() : null;

        if (userId != null) {
            // Lấy điểm hiện tại từ bảng rankings
            DatabaseReference rankingRef = FirebaseDatabase.getInstance().getReference("rankings").child(userId);

            rankingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Người dùng có trong bảng rankings
                        int userPoints = dataSnapshot.child("point").getValue(Integer.class);

                        // Lấy trạng thái VIP từ bảng users
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userDataSnapshot) {
                                if (userDataSnapshot.exists()) {
                                    boolean isVIP = userDataSnapshot.child("isVIP").getValue(Boolean.class);

                                    // Kiểm tra điểm có đủ để đổi VIP không và kiểm tra trạng thái VIP
                                    if (!isVIP) {
                                        // User is not VIP, perform VIP upgrade
                                        performVIPUpgrade6(userId, userPoints, 1500, 6); // 6 tháng
                                    } else {
                                        // User is already a VIP, display a message or handle as needed
                                        showAlreadyVIPMessage();
                                    }
                                } else {
                                    // Người dùng không có trong bảng users
                                    // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                                    showNoUserInUsersMessage();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError userError) {
                                // Xử lý lỗi nếu cần
                            }
                        });
                    } else {
                        // Người dùng không có trong bảng rankings
                        // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                        showNoUserInRankingsMessage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý lỗi nếu cần
                }
            });
        }
    }
    private void performVIPUpgrade6(String userId, int userPoints, int pointsNeeded, int numberOfMonths) {
        // Kiểm tra xem số điểm của người dùng có đủ để đổi VIP không
        if (userPoints >= pointsNeeded) {
            // Trừ điểm
            int newPoints = userPoints - pointsNeeded;
            DatabaseReference rankingRef = FirebaseDatabase.getInstance().getReference("rankings").child(userId);
            rankingRef.child("point").setValue(newPoints);

            // Lấy ngày hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            // Tính toán ngày kết thúc sau số tháng đặt trước
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, numberOfMonths);
            String endDate = sdf.format(calendar.getTime());

            // Thực hiện logic đổi điểm thành VIP
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("isVIP").setValue(true);
            userRef.child("startTime").setValue(currentDate);
            userRef.child("endTime").setValue(endDate);

            // Hiển thị thông báo hoặc cập nhật UI nếu cần
            showSuccessMessage();
        } else {
            // Hiển thị thông báo nếu điểm không đủ
            showInsufficientPointsMessage();
        }
    }

    private void performVIPUpgrade(String userId, int userPoints, int pointsNeeded, int numberOfMonths) {
        // Kiểm tra xem số điểm của người dùng có đủ để đổi VIP không
        if (userPoints >= pointsNeeded) {
            // Trừ điểm
            int newPoints = userPoints - pointsNeeded;
            DatabaseReference rankingRef = FirebaseDatabase.getInstance().getReference("rankings").child(userId);
            rankingRef.child("point").setValue(newPoints);

            // Lấy ngày hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());

            // Tính toán ngày kết thúc sau số tháng đặt trước
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, numberOfMonths);
            String endDate = sdf.format(calendar.getTime());

            // Thực hiện logic đổi điểm thành VIP
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
            userRef.child("isVIP").setValue(true);
            userRef.child("startTime").setValue(currentDate);
            userRef.child("endTime").setValue(endDate);

            // Hiển thị thông báo hoặc cập nhật UI nếu cần
            showSuccessMessage();
        } else {
            // Hiển thị thông báo nếu điểm không đủ
            showInsufficientPointsMessage();
        }
    }

    private void showSuccessMessage() {
        Toast.makeText(ChangepointActivity.this, "Đổi điểm thành công. Bạn đã trở thành VIP.", Toast.LENGTH_SHORT).show();
    }

    private void showInsufficientPointsMessage() {
        Toast.makeText(ChangepointActivity.this, "Bạn không đủ điểm để đổi VIP.", Toast.LENGTH_SHORT).show();
    }

    private void showNoUserInRankingsMessage() {
        Toast.makeText(ChangepointActivity.this, "Vui lòng nghe nhạc hoặc xem quảng cáo để đủ điểm", Toast.LENGTH_SHORT).show();
    }
    private void showAlreadyVIPMessage() {
        Toast.makeText(ChangepointActivity.this, "Tài khoản của bạn đã là tài khoản VIP rồi!", Toast.LENGTH_SHORT).show();
    }
    private void showNoUserInUsersMessage() {
        Toast.makeText(ChangepointActivity.this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
    }
    private void getUserRankingInfo(String userId) {
        DatabaseReference rankingRef = FirebaseDatabase.getInstance().getReference("rankings");

        // Lấy thông tin người dùng từ bảng rankings
        rankingRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Người dùng có trong bảng rankings
                    String userName = dataSnapshot.child("userName").getValue(String.class);
                    String userEmail = dataSnapshot.child("userEmail").getValue(String.class);
                    int userPoints = dataSnapshot.child("point").getValue(Integer.class);

                    // Hiển thị thông tin trong RecyclerView
                    RankingItem userRankingItem = new RankingItem(userName, userEmail, userPoints);
                    displayUserRankingInfo(userRankingItem);
                } else {
                    // Người dùng không có trong bảng rankings
                    // Xử lý theo ý của bạn, có thể hiển thị một thông báo hoặc ẩn RecyclerView
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    private void displayUserRankingInfo(RankingItem userRankingItem) {
        // Khởi tạo Adapter và liên kết với RecyclerView
        List<RankingItem> userRankingList = new ArrayList<>();
        userRankingList.add(userRankingItem);
        rankingAdapter = new RankingAdapter(userRankingItem);
        recyclerView.setAdapter(rankingAdapter);
    }
}
