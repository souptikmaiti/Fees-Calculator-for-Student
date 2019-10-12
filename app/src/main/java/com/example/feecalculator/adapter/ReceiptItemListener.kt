package com.example.feecalculator.adapter

import com.example.feecalculator.data.entitites.FeesReceipt

interface ReceiptItemListener {
    fun onReceiptItemSelected(receipt: FeesReceipt)
}