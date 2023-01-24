package com.example.naturepalestinesociety.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.naturepalestinesociety.databinding.ListItemBinding

class SpinnerAdapter(
    private val list: Array<String>
) : BaseAdapter() {

    fun getPosition(item:String?):Int{
        return list.indexOf(item)
    }
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent!!.context),
            parent,
            false
        )

        binding.root.text = getItem(position) as String
        return binding.root
    }
}