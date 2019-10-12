package com.example.feecalculator.viewmodel

interface ReceiptViewModelListener {
    fun onSuccess(msg: String, operation:String)
    fun onFailure(msg: String)
}