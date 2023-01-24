package com.example.naturepalestinesociety.activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Window
import androidx.activity.viewModels
import com.example.naturepalestinesociety.adapters.ProjectsAdapter
import com.example.naturepalestinesociety.databinding.ActivityCreateProjectBinding
import com.example.naturepalestinesociety.databinding.DialogAddProjectBinding
import com.example.naturepalestinesociety.helpers.*
import com.example.naturepalestinesociety.models.project.AddProject
import com.example.naturepalestinesociety.models.project.Project
import com.example.naturepalestinesociety.utils.Status
import com.example.naturepalestinesociety.viewmodel.MainViewModel

class CreateProjectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateProjectBinding
    private val viewModel by viewModels<MainViewModel>()
    private var projects = mutableListOf<Project>()
    private var projectsFiltered = mutableListOf<Project>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFullScreen(window)
        setLightStatusBar(window,true)
        viewModel.init(this)

        binding.imgBack.setOnClickListener { finish() }

        getProjects()

        binding.fabAdd.setOnClickListener {
            showDialog()
        }


        binding.editProjects.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    search(s.toString())
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    search(s.toString())
                }
                else{
                    getProjects()
                }
            }
        })

    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DialogAddProjectBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.btnSave.setOnClickListener {
            if (dialogBinding.editProject.text.toString().isNotEmpty()) {
                val name = dialogBinding.editProject.text.toString()
                addProject(name,name)
            }
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun getProjects() {
        projects.clear()
        viewModel.getProjects().observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            projects.addAll(it.data.body()?.data!!)
                            binding.recProjects.adapter = ProjectsAdapter(this, projects) {
                                val intent = Intent()
                                intent.putExtra(Constants.PROJECT, it)
                                setResult(Constants.RESULT_CODE_GET_PROJECT, intent)
                                super.onBackPressed()
                            }
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "getProjects: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun addProject(name_ar: String,name_en: String) {
        val project = AddProject(name_ar,name_en)
        viewModel.addProject(project).observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (it.data?.body()?.data != null) {
                            val project = it.data.body()?.data!!
                            val intent = Intent()
                            intent.putExtra(Constants.PROJECT, project)
                            setResult(Constants.RESULT_CODE_GET_PROJECT, intent)
                            super.onBackPressed()
                        }
                    }
                    Status.ERROR -> {
                        Log.d(TAG, "addProject: ${it.message}")
                        toastError(it.message)
                    }
                    Status.LOADING -> {

                    }
                }
            }
        }
    }

    private fun search(searchedName:String){
        projectsFiltered.clear()
        for (project in projects) {
            if (project.name.contains(searchedName, ignoreCase = true)) {
                projectsFiltered.add(project)
            }
        }
        binding.recProjects.adapter = ProjectsAdapter(this, projectsFiltered) {
            val intent = Intent()
            intent.putExtra(Constants.PROJECT, it)
            setResult(Constants.RESULT_CODE_GET_PROJECT, intent)
            super.onBackPressed()
        }
    }


}


