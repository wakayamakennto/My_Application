package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TimelineAdapter(private val postList: List<Post>) : RecyclerView.Adapter<TimelineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = postList[position]
        holder.textViewContent.text = post.content
        // 他のUI要素も必要に応じて設定

        // Glideを使用してFirebase Storageの画像をImageViewに読み込む
        Glide.with(holder.imageView.context)
            .load(post.imageUrl)  // Firebaseに保存された画像のURL
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)  // ImageViewの参照を取得
    }
}
