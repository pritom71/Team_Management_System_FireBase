package com.stamasoft.ptithom.firebase_all_libraries;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity_1 extends AppCompatActivity {

    TextInputEditText etRegEmail;
    TextInputEditText etRegPass;
    TextView tvLoginHere;
    Button btnRegister;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);

        // Hide the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize the views
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPass = findViewById(R.id.etRegPass);
        tvLoginHere = findViewById(R.id.tvLoginHere);
        btnRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(v -> createUser());

        tvLoginHere.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity_1.this, LoginActivity.class));
        });
    }

    private void createUser() {

        String email = etRegEmail.getText().toString().trim();
        String pass = etRegPass.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etRegEmail.setError("Email is required");
            etRegEmail.requestFocus();
        } else if (TextUtils.isEmpty(pass)) {
            etRegPass.setError("Password is required");
            etRegPass.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegistrationActivity_1.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity_1.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegistrationActivity_1.this, "Failed to create user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
