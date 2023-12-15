package com.example.soundbox_du_an_md31.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.soundbox_du_an_md31.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegistrationActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    Button btnHuy,btnSignUp;
    private FirebaseAuth mAuth;
    private TextInputEditText email,pass,rePass , username;
    Context context = RegistrationActivity.this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        initUi();
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = email.getText().toString().trim();
                String strPass = pass.getText().toString().trim();
                String strRePass = rePass.getText().toString().trim();
                String strUsername = username.getText().toString().trim();
                Log.d("zzz", "onClick: " + strEmail +","+ strPass);
                createAccount(strEmail,strPass , strUsername);
            }

        });
    }

    private void createAccount(String email, String password, String username) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String userID = user.getUid();
                                long currentTime = System.currentTimeMillis();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                DatabaseReference userNode = databaseReference.child("users").child(userID);
                                userNode.child("email").setValue(user.getEmail());
                                userNode.child("isLocked").setValue(false);
                                userNode.child("isVIP").setValue(false);
                                userNode.child("creationTime").setValue(currentTime);

                                // Thêm dòng này để lưu trường username
                                userNode.child("username").setValue(username);
                            } else {
                                // Người dùng chưa đăng nhập
                            }
                            Toast.makeText(RegistrationActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initUi(){
        email = findViewById(R.id.email1);
        pass = findViewById(R.id.pass1);
        rePass = findViewById(R.id.repass1);
        btnHuy = findViewById(R.id.btnHuy);
        btnSignUp = findViewById(R.id.btnSignUp);
        username = findViewById(R.id.txtname);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}