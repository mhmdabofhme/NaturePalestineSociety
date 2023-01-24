package com.example.naturepalestinesociety.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.naturepalestinesociety.databinding.SliderImageBinding
import com.example.naturepalestinesociety.helpers.loadImage
import com.example.naturepalestinesociety.models.post.Media

class ImageSlideAdapter(private val imageList: List<Media>) :
    RecyclerView.Adapter<ImageSlideAdapter.ImageViewHolder>() {


    class ImageViewHolder(view: SliderImageBinding) :
        RecyclerView.ViewHolder(view.root) {
        val binding: SliderImageBinding = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view: SliderImageBinding =
            SliderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.binding.root.loadImage(imageList[position].file_name)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }


}