package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;  // FirestoreではなくRealtime Databaseに変更
    private Uri imageUri;  // アイコンの画像URI

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Realtime Databaseの参照を取得

        EditText emailEditText = findViewById(R.id.editTextEmail);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        ImageView iconImageView = findViewById(R.id.imageViewIcon);
        Button selectImageButton = findViewById(R.id.buttonSelectImage);
        Button registerButton = findViewById(R.id.buttonRegister);
        Button loginButton = findViewById(R.id.buttonLogin);

        // アイコン選択ボタン
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Icon"), PICK_IMAGE_REQUEST);
        });

        // ユーザー登録ボタン
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || imageUri == null) {
                Toast.makeText(Register.this, "Please fill all fields and select an icon", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            uploadImageAndSaveUserData(userId);
                        } else {
                            Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // ログインボタン
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    // アイコン画像を選択後の処理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ImageView iconImageView = findViewById(R.id.imageViewIcon);
            iconImageView.setImageURI(imageUri);  // 選択された画像を表示
        }
    }

    // アイコンをFirebase Storageにアップロードし、Realtime Databaseにユーザーデータを保存
    private void uploadImageAndSaveUserData(String userId) {
        StorageReference ref = storageReference.child("users/" + userId + "/profile.jpg");

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();

                    // Realtime Databaseにユーザーデータを保存
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", mAuth.getCurrentUser().getEmail());
                    user.put("iconUrl", imageUrl); // アイコンのURL
                    user.put("username", "your username"); // 任意で追加情報も

                    databaseReference.child(userId).setValue(user)  // Realtime Databaseに保存
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                // MainActivityなど次の画面に遷移
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(Register.this, "Error saving user data", Toast.LENGTH_SHORT).show();
                            });
                }))
                .addOnFailureListener(e -> {
                    Toast.makeText(Register.this, "Failed to upload icon: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
