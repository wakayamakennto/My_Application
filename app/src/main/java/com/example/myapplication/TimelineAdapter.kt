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

        // 投稿画像をGlideで表示
        Glide.with(holder.imageView.context)
            .load(post.imageUrl)  // Firebaseに保存された画像のURL
            .into(holder.imageView)

        // ユーザーアイコンをGlideで表示
        Glide.with(holder.userIconImageView.context)
            .load(post.userIconUrl)  // ユーザーアイコンのURL
            .placeholder(R.drawable.ic_user_placeholder)  // プレースホルダーを設定
            .into(holder.userIconImageView)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val userIconImageView: ImageView = itemView.findViewById(R.id.userIconImageView)  // ユーザーアイコンのImageViewを参照
    }
}

