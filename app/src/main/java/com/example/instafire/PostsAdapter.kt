package com.example.instafire

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instafire.models.Post
import kotlinx.android.synthetic.main.item_post.view.*

class PostsAdapter(val context: Context, val posts: List<Post>) :
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //return view holder which takes view as parameter
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)

    }
    // this uses binding function for viewing and posting

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }


    override fun getItemCount() = posts.size
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(post: Post) {
            itemView.tvusername.text = post.user?.username
            itemView.tvdescription.text = post.description
            Glide.with(context).load(post.imageurl).into(itemView.tvpost)
            itemView.tvrelativetime.text = DateUtils.getRelativeTimeSpanString(post.creationtime)
        }


    }

}
