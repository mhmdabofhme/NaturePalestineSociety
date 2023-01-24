package com.example.naturepalestinesociety.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.activities.PostDetailsActivity
import com.example.naturepalestinesociety.activities.UpdatePostActivity
import com.example.naturepalestinesociety.databinding.CustomMyPostBinding
import com.example.naturepalestinesociety.helpers.Constants
import com.example.naturepalestinesociety.helpers.loadImage
import com.example.naturepalestinesociety.models.post.Post


class MyPostsAdapter(
    private val activity: Activity, private val list: MutableList<Post>,
    val onClick: (post: Post) -> Unit
) :
    RecyclerView.Adapter<MyPostsAdapter.MyViewHolder>() {

    inner class MyViewHolder(b: CustomMyPostBinding) :
        RecyclerView.ViewHolder(b.root) {
        val binding: CustomMyPostBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b: CustomMyPostBinding =
            CustomMyPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val data = list[position]



        holder.binding.apply {
            imgUser.loadImage(data.user.photo)


            imgPost.loadImage(data.media[data.media.size-1].file_name)
            txtPostTitle.text = data.title


            txtUsername.text = data.user.name
        }

        holder.binding.imgMore.setOnClickListener {
            val popupMenu = PopupMenu(activity, holder.binding.imgMore)
            popupMenu.menuInflater.inflate(R.menu.popup_post_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener{ item ->
                when (item.itemId) {
                    R.id.pop_edit -> {
                        val intent = Intent(activity, UpdatePostActivity::class.java)
                        intent.putExtra(Constants.POST_ID, data.id)
                        activity.startActivity(intent)
                    }
                    R.id.pop_delete -> {
                        onClick(data)
                    }
                }
                true
            }
            popupMenu.show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, PostDetailsActivity::class.java)
            intent.putExtra(Constants.POST, data)
            activity.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

}