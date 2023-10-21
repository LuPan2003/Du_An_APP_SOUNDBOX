package com.example.soundbox_du_an_md31.Activity;

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


public class RegistrationActivity extends AppCompatActivity {
    Button btnHuy,btnSignUp;
    private FirebaseAuth mAuth;
    private TextInputEditText email,pass,rePass;
    Context context = RegistrationActivity.this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
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
                Log.d("zzz", "onClick: " + strEmail +","+ strPass);
                createAccount(strEmail,strPass);
            }

        });
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegistrationActivity.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }
    private void initUi(){
        email = findViewById(R.id.email1);
        pass = findViewById(R.id.pass1);
        rePass = findViewById(R.id.repass1);
        btnHuy = findViewById(R.id.btnHuy);
        btnSignUp = findViewById(R.id.btnSignUp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}