package com.example.soundbox_du_an_md31.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soundbox_du_an_md31.R;
import com.example.soundbox_du_an_md31.Service.MusicReceiver;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 123;
    private static final long TIME_DELAY = 1 * 60 * 60 * 1000; // 24 giờ
    private static int SPLASH_TIME_OUT = 4000;
    ImageView logoApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logoApp = findViewById(R.id.logoApp) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, MusicReceiver.class);
//        AutoDeleteTask autoDeleteTask = new AutoDeleteTask();
//        Log.d("delete1", "onDataChange: ");
//
//        autoDeleteTask.start();

        ///------------------------------------------------------///

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if(user == null){
//            return;
//        }
//        String childTablePath = "/history/"+user.getUid();
//
//        // Thời gian ngưỡng (số mili giây)
//        long deleteThreshold = 24 * 60 * 60 * 1000; // 24 giờ (1 ngày)
//
//        // Khởi tạo FirebaseApp (chỉ cần thực hiện một lần)
//        FirebaseApp.initializeApp(getApplicationContext());
//
//        // Tạo một TimerTask để xóa đối tượng
//        TimerTask task = new TimerTask() {
//            public void run() {
//                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference(childTablePath);
//                long currentTime = System.currentTimeMillis();
//
//                databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            DataSnapshot timestampSnapshot = snapshot.child("timestamp");
//                            long objectTime = timestampSnapshot.getValue(Long.class);
//                            if (objectTime < currentTime - deleteThreshold) {
//                                snapshot.getRef().removeValue();
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        // Xử lý lỗi nếu cần
//                    }
//                });
//            }
//        };
//
//        // Lập lịch cho công việc xóa tự động chạy sau mỗi khoảng thời gian
//        Timer timer = new Timer();
//        timer.schedule(task, 0, deleteThreshold);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerTime = System.currentTimeMillis() + TIME_DELAY;
        Log.d("abc1", "đã vào");
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        fadeIn();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, SPLASH_TIME_OUT);
    }
    private void fadeIn() {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(3000);
        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeIn);
        logoApp.setAnimation(animation);
    }
    private void nextActivity(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else{
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }
}