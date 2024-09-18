package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.io.InputStream
import java.util.*

class ImageUp : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonUploadImage: Button
    private lateinit var backButton: Button
    private var imageUri: Uri? = null

    // 権限のリクエストランチャー
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 権限が許可された場合、画像選択ボタンを有効にする
                buttonSelectImage.isEnabled = true
            } else {
                // 権限が拒否された場合、ユーザーにメッセージを表示する
                Toast.makeText(this, "ストレージへのアクセス権限が必要です", Toast.LENGTH_SHORT).show()
            }
        }

    // 画像選択の結果を処理するランチャー
    private val selectImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                val inputStream: InputStream? = imageUri?.let { contentResolver.openInputStream(it) }
                val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
                imageView.setImageBitmap(bitmap)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_up)

        backButton = findViewById(R.id.backButton)
        buttonSelectImage = findViewById(R.id.buttonSelectImage)
        imageView = findViewById(R.id.imageView)
        buttonUploadImage = findViewById(R.id.buttonUploadImage)

        backButton.setOnClickListener {
            finish()
        }

        buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            selectImageLauncher.launch(intent)
        }

        buttonUploadImage.setOnClickListener {
            uploadImageToFirebase()  // 画像をFirebaseにアップロードする処理
        }

        // 権限が許可されているか確認する
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 権限が許可されていない場合、リクエストする
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            // 権限が既に許可されている場合、画像選択ボタンを有効にする
            buttonSelectImage.isEnabled = true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // 画像をFirebase Storageにアップロードして、そのURLをRealtime Databaseに保存するメソッド
    private fun uploadImageToFirebase() {
        if (imageUri != null) {
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference
            val fileReference: StorageReference = storageReference.child("images/${UUID.randomUUID()}.jpg")

            // 画像をFirebase Storageにアップロード
            fileReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    // 画像のダウンロードURLを取得
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveImageUrlToDatabase(imageUrl)  // ダウンロードURLをRealtime Databaseに保存
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@ImageUp, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this@ImageUp, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    // 画像URLをFirebase Realtime Databaseに保存するメソッド
    private fun saveImageUrlToDatabase(imageUrl: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("posts")
        val postId = databaseReference.push().key ?: return

        val post = mapOf(
            "imageUrl" to imageUrl,
            "text" to "Sample text",  // 投稿のテキストなど
            "timestamp" to ServerValue.TIMESTAMP  // サーバー側のタイムスタンプ
        )

        // データをRealtime Databaseに保存
        databaseReference.child(postId).setValue(post)
            .addOnSuccessListener {
                Toast.makeText(this@ImageUp, "Post saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@ImageUp, "Failed to save post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
