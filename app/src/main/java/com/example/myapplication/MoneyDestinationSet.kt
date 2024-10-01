package com.example.myapplication // パッケージ名が異なる場合は正しいパッケージ名に変更

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MoneyDestinationSetActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_money_destination_set)

        // Firebaseデータベースの参照を取得
        database = FirebaseDatabase.getInstance().getReference("savingsGoal")

        val savingsInput: EditText = findViewById(R.id.savings_input)
        val setSavingsButton: Button = findViewById(R.id.set_savings_button)
        val savingsDisplay: TextView = findViewById(R.id.savings_display)
        val backButton: Button = findViewById(R.id.backButton)

        setSavingsButton.setOnClickListener {
            val amount = savingsInput.text.toString().toIntOrNull()
            if (amount != null && amount > 0) {
                // 目標金額をFirebaseに保存
                database.setValue(amount)
                savingsDisplay.text = "設定した目標金額: $amount 円"
                Toast.makeText(this, "目標金額が保存されました: $amount 円", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "有効な金額を入力してください", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()  // 戻るボタンで前の画面に戻る
        }
    }
}
