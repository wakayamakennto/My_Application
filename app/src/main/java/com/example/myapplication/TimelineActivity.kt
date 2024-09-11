package com.example.snsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.firebase.database.DataSnapshot // 必要なインポートを追加
import com.google.firebase.database.DatabaseError // 必要なインポートを追加
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TimelineActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var postList: MutableList<Post>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postList = mutableListOf()
        timelineAdapter = TimelineAdapter(postList)
        recyclerView.adapter = timelineAdapter

        // Firebase Realtime Databaseからデータを取得
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (snapshot in dataSnapshot.children) {
                    val post = snapshot.getValue(Post::class.java)
                    post?.let { postList.add(it) }
                }
                timelineAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // エラー処理
            }
        })
    }
}
