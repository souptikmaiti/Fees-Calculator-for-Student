package com.example.feecalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feecalculator.repository.RepositoryStudentFees

@Suppress("UNCHECKED_CAST")
class ViewModelFactoryReceipt(private val repo:RepositoryStudentFees): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelReceipt(repo) as T
    }
}