package com.example.feecalculator.viewmodel

interface StudentViewModelListener {
    fun onSuccess(message:String)
    fun onFailure(message:String)
}