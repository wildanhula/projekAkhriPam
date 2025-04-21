package com.example.projectakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class login_activity extends AppCompatActivity {
    private Button btnLogin;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(login_activity.this,
                            "Email dan Password tidak boleh kosong",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (isValidLogin(email, password)) {
                        Toast.makeText(login_activity.this,
                                "Berhasil Log In",
                                Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(login_activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(login_activity.this,
                                "Email atau password salah",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private boolean isValidLogin(String email, String password) {
        return email.equals("farrel@gmail.com") && password.equals("inipasswordnya");
    }
}