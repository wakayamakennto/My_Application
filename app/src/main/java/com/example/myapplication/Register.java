package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        Button registerButton = findViewById(R.id.buttonRegister);
        Button loginButton = findViewById(R.id.buttonLogin); // 「Login」ボタンを追加

        // 「Register」ボタンのクリックイベント
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Register.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            // 登録後の処理（例: MainActivityに遷移）
                        } else {
                            Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 「Login」ボタンのクリックイベント
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this,Login.class);
            startActivity(intent);
            finish();  // 現在のアクティビティを終了
        });
    }
}
