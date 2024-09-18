package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.bumptech.glide.Glide

class TimelineActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var postList: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        // RecyclerView のセットアップ
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postList = mutableListOf()
        timelineAdapter = TimelineAdapter(postList)
        recyclerView.adapter = timelineAdapter

        // Firebase Realtime Database からデータを取得
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
                // データの取得に失敗した場合のエラーメッセージ表示
                Toast.makeText(this@TimelineActivity, "Failed to load posts: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
