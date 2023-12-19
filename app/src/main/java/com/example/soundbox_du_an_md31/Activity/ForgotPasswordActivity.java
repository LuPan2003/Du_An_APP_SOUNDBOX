package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.example.soundbox_du_an_md31.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button btnGuiEmail,btnHuy;
    private AppCompatEditText emailEditText;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initUi();
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnGuiEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy email từ người dùng.
                String email = emailEditText.getText().toString();
                    sendEmail(email);
            }
            private void sendEmail(String email) {
                // Kiểm tra xem email có hợp lệ không.
                if (!isValidEmail(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
//                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                // Check if the email already exists
//                boolean isEmailVerified = user.isEmailVerified();
//                if (isEmailVerified) {
//                    // Email đã được xác minh
//                    Log.d("TAG", "Email đã được xác minh");
//                } else {
//                    // Email chưa được xác minh
//                    Log.d("TAG", "Email chưa được xác minh");
//                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Email đã được gửi thành công.
                                Toast.makeText(ForgotPasswordActivity.this, "Đã gửi email nhắc nhở đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                // Gửi email thất bại.
                                String error = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            }
        });
    }
    // Hàm kiểm tra email có hợp lệ không.
    private boolean isValidEmail(String email) {
        return email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    }
    @SuppressLint("WrongViewCast")
    private void initUi(){
        emailEditText = findViewById(R.id.edt_email_forgot);
        btnGuiEmail=findViewById(R.id.reset_password_button);
        btnHuy = findViewById(R.id.btn_huy);
        back = findViewById(R.id.back);
    }
}