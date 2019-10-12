package com.example.feecalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feecalculator.repository.RepositoryStudentFees

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryStudent(private val repo: RepositoryStudentFees) : ViewModelProvider.NewInstanceFactory(){

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelStudent(repo) as T
    }
}