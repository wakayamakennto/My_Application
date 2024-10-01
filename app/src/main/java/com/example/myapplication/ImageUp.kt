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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.InputStream
import java.util.*

class ImageUp : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var buttonSelectImage: Button
    private lateinit var buttonUploadImage: Button
    private lateinit var backButton: Button
    private var imageUri: Uri? = null

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("posts")

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                buttonSelectImage.isEnabled = true
            } else {
                Toast.makeText(this, "ストレージへのアクセス権限が必要です", Toast.LENGTH_SHORT).show()
            }
        }

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
            uploadImageToFirebase()
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            buttonSelectImage.isEnabled = true
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun uploadImageToFirebase() {
        if (imageUri != null) {
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference
            val fileReference: StorageReference = storageReference.child("images/${UUID.randomUUID()}.jpg")

            fileReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        saveImageUrlToDatabase(imageUrl)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@ImageUp, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this@ImageUp, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        val postId = databaseReference.push().key ?: return
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown User"

        val post = Post(
            postId = postId,
            userId = userId,
            content = "Sample text",  // 投稿のテキストなど
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis()
        )

        databaseReference.child(postId).setValue(post)
            .addOnSuccessListener {
                Toast.makeText(this@ImageUp, "Post saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this@ImageUp, "Failed to save post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
