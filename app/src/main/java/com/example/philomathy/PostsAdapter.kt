package com.example.philomathy

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.philomathy.models.Posts
import java.math.BigInteger
import java.security.MessageDigest

class PostsAdapter(val context: Context, val posts: List<Posts>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>()
{
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        fun bind(post: Posts)
        {
            val username = post.user?.username as String
            itemView.findViewById<TextView>(R.id.username).text = post.user?.username
            itemView.findViewById<TextView>(R.id.post_description).text  = post.description
            Glide.with(context).load(post.imageurl).into(itemView.findViewById(R.id.img_url))
            itemView.findViewById<TextView>(R.id.posttime).text = DateUtils.getRelativeTimeSpanString(post.creationtime)
            Glide.with(context).load(getProfileImageUrl(username)).into(itemView.findViewById(R.id.ivProfileImage))

        }

        private fun getProfileImageUrl(username: String): String {
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(username.toByteArray())
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
       return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size


}