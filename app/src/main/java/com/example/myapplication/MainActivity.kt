package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.content.Intent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var title:TextView//メンバ変数定義
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        title = findViewById(R.id.title)//ボタンに同じくimport

        val startButton: Button = findViewById(R.id.start)//ボタンに関するライブラリ的なやつをimportしておくこと
        val scoreButton: Button = findViewById(R.id.score)
        val buttonGoToRegister: Button = findViewById(R.id.buttonGoToRegister) // 追加されたボタンの参照を取得
        val logoutButton: Button = findViewById(R.id.buttonLogout)
        val button_go_to_recyclerview: Button = findViewById(R.id.button_go_to_recyclerview)

        startButton.setOnClickListener{
            // ImageUpアクティビティに遷移
            val intent = Intent(this, ImageUp::class.java)
            startActivity(intent)
        }
        scoreButton.setOnClickListener {
            /* MoneyDestinationSetアクティビティに遷移*/
            val intent=Intent(this,MoneyDestinationSet::class.java)
            startActivity(intent)
        }
        buttonGoToRegister.setOnClickListener {
            // RegisterActivityに遷移
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        logoutButton.setOnClickListener {
            mAuth.signOut() // ユーザーをサインアウト
            Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()

            // サインアウト後、LoginActivityに戻るなどの処理を行う
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish() // 現在のアクティビティを終了
        }
        button_go_to_recyclerview.setOnClickListener {
            // TimelineActivityに遷移
            val intent = Intent(this, TimelineActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}