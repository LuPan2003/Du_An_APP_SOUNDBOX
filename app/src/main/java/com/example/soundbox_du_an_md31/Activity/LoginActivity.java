package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soundbox_du_an_md31.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    Button btnSignIn;
    private TextInputEditText email , password ;
    private TextView forgetpass,btnDangky;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        initUi();

        btnDangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_right);
                finish();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(login);
            }
        });
    }

    private void signIn() {
        // Kiểm tra xem email và mật khẩu có hợp lệ không.
        boolean isValid = checkValidCredentials(email.getText().toString(), password.getText().toString());

        if (email.length()==0){
            Toast.makeText(this, "Vui lòng điền Email", Toast.LENGTH_SHORT).show();
            return;
        }else if (password.length()==0){
            Toast.makeText(this, "Vui lòng điền password", Toast.LENGTH_SHORT).show();
            return;
        }else {
            if (!isValid) {
                Toast.makeText(this, "Email hoặc mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        // Thử đăng nhập người dùng.
        try {
            progressDialog.show();
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                            // Thành công
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent login = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(login);
                            } else {
                            // Thất bại
                                String error = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } catch (Exception ex) {
                    // Xử lý tất cả các ngoại lệ.
                    Toast.makeText(LoginActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
                    // Mã luôn được thực thi, ngay cả khi có ngoại lệ xảy ra.
        }
    }


    public boolean checkValidCredentials(String email, String password) {
        // Kiểm tra xem email có hợp lệ hay không.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        // Kiểm tra xem mật khẩu có hợp lệ hay không.
        if (password.length() < 6) {
            return false;
        }

        return true;
    }
    @SuppressLint("WrongViewCast")
    private void initUi(){
        email = findViewById(R.id.UserName);
        password = findViewById(R.id.Password);
        btnDangky=findViewById(R.id.btnSignUp1);
        btnSignIn = findViewById(R.id.btnSignIn);
        forgetpass = findViewById(R.id.tvForgetpass);
    }
}