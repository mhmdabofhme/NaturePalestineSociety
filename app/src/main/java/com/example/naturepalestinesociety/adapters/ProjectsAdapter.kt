package com.example.naturepalestinesociety.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.activities.PostDetailsActivity
import com.example.naturepalestinesociety.databinding.CustomPostBinding
import com.example.naturepalestinesociety.databinding.ListItemBinding
import com.example.naturepalestinesociety.helpers.loadImage
import com.example.naturepalestinesociety.models.project.Project


class ProjectsAdapter(
    private val activity: Activity,
    private val list: MutableList<Project>,
    private val onClick: (data: Project) -> Unit
) :
    RecyclerView.Adapter<ProjectsAdapter.MyViewHolder>() {

    inner class MyViewHolder(b: ListItemBinding) :
        RecyclerView.ViewHolder(b.root) {
        val binding: ListItemBinding = b
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val b: ListItemBinding =
            ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(b)
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val data = list[position]

        holder.binding.root.text = data.name
        holder.binding.root.setOnClickListener {
            onClick(data)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}