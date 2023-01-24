package com.example.naturepalestinesociety.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.naturepalestinesociety.R
import com.example.naturepalestinesociety.adapters.ProjectsAdapter
import com.example.naturepalestinesociety.databinding.ActivityChooseSpeciesBinding
import com.example.naturepalestinesociety.helpers.Constants
import com.example.naturepalestinesociety.helpers.setFullScreen
import com.example.naturepalestinesociety.helpers.setLightStatusBar

class ChooseSpeciesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseSpeciesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseSpeciesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window,true)
        binding.imgBack.setOnClickListener { finish() }


        val list = listOf(
            "specie 1",
            "specie 2",
            "specie 3",
            "specie 4",
            "specie 5",
            "specie 6",
            "specie 7",
            "specie 8",
            "specie 9",
        )

//        binding.recSpecies.adapter = ProjectsAdapter(this, list) {
//            val intent = Intent()
//            intent.putExtra(Constants.SPECIES_NAME, it)
//            setResult(Constants.RESULT_CODE_GET_SPECIES, intent)
//            super.onBackPressed()
//        }

    }
}