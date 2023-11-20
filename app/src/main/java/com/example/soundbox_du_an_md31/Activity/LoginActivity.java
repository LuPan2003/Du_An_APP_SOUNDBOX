package com.example.soundbox_du_an_md31.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soundbox_du_an_md31.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    public static String KEY_EMAIL = "email";
    public static String KEY_PASSWORD = "password";
    public static String KEY_CHECKSTATUS = "checkstatus";
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private CheckBox cbLuuThongTin;
    Button btnSignIn;
    private TextInputEditText email , password ;
    private TextView forgetpass,btnDangky;
    // Khai báo biến Firebase Analytics
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressDialog = new ProgressDialog(this);

        // Khởi tạo Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

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
                // Sau khi đăng nhập thành công, gửi sự kiện đăng nhập
                logLoginEvent();
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
                                // Lấy UID của người dùng đã đăng nhập
                                String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                String userName  = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                // Lấy thời gian hiện tại
                                long currentTime = System.currentTimeMillis();
                                // Cập nhật trường loginTime trong dữ liệu người dùng
                                updateLoginTime(userUid, currentTime);
                                Log.d("zzzzzz" ,  userUid + userName );
                                luuThongTin();
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
    private void logLoginEvent() {
        // Tạo một bundle để chứa dữ liệu muốn gửi cùng sự kiện
        Bundle bundle = new Bundle();
        // Lấy thời điểm hiện tại
        long currentTime = System.currentTimeMillis();
        // Thêm thời điểm đăng nhập vào bundle
        bundle.putLong("login_time", currentTime);
        // Ghi lại sự kiện đăng nhập
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
    }
    private void updateLoginTime(String userUid, long loginTime) {
        // Tham chiếu đến nút cụ thể của người dùng trong Firebase Realtime Database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userUid);

        // Tạo một HashMap để lưu thông tin cập nhật
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("loginTime", loginTime);

        // Thực hiện cập nhật trên Firebase
        userRef.updateChildren(updateData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Cập nhật thành công
                            Log.d("Firebase", "Cập nhật loginTime thành công");
                        } else {
                            // Xử lý khi cập nhật thất bại
                            Log.e("Firebase", "Lỗi cập nhật loginTime: " + task.getException().getMessage());
                        }
                    }
                });
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
    private void layThongTin() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        boolean check = sharedPreferences.getBoolean(KEY_CHECKSTATUS, false);
        if (check) {
            String emailNguoiDUng = sharedPreferences.getString(KEY_EMAIL, "");
            String matKhau = sharedPreferences.getString(KEY_PASSWORD, "");
            email.setText(emailNguoiDUng);
            password.setText(matKhau);
        } else {
            email.setText("");
            password.setText("");
        }
        cbLuuThongTin.setChecked(check);

    }
    private void luuThongTin() {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String emailNguoiDung = email.getText().toString();
        String pass = password.getText().toString();
        boolean check = cbLuuThongTin.isChecked();
        if (!check) {
            editor.clear();
        } else {
            editor.putString(KEY_EMAIL, emailNguoiDung);
            editor.putString(KEY_PASSWORD, pass);
            editor.putBoolean(KEY_CHECKSTATUS, check);
        }
        editor.commit();
    }
    @SuppressLint("WrongViewCast")
    private void initUi(){
        email = findViewById(R.id.UserName);
        password = findViewById(R.id.Password);
        btnDangky=findViewById(R.id.btnSignUp1);
        btnSignIn = findViewById(R.id.btnSignIn);
        forgetpass = findViewById(R.id.tvForgetpass);
        cbLuuThongTin = findViewById(R.id.chk_remember);
    }

    @Override
    protected void onResume() {
        super.onResume();
        layThongTin();
    }
}