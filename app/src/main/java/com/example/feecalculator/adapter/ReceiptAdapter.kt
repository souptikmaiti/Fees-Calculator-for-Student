package com.example.feecalculator.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.feecalculator.R
import com.example.feecalculator.data.entitites.FeesReceipt
import kotlinx.android.synthetic.main.item_receipt.view.*
import java.text.SimpleDateFormat
import java.util.*

class ReceiptAdapter(var receiptList: Array<FeesReceipt>): RecyclerView.Adapter<ReceiptAdapter.ReceiptHolder>() {
    var itemListener: ReceiptItemListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptHolder {
        return ReceiptHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_receipt,parent,false))
    }

    override fun getItemCount(): Int = receiptList.size

    override fun onBindViewHolder(holder: ReceiptHolder, position: Int) {
        var receipt = receiptList.get(position)
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        holder.itemView.tv_dop.text = formatter.format(receipt.dop)
        holder.itemView.tv_amount.text = receipt.amount.toString()
        holder.itemView.card_receipt.setOnLongClickListener {
            itemListener?.let {
                it.onReceiptItemSelected(receipt)
            }
            return@setOnLongClickListener true
        }
    }

    inner class ReceiptHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    fun getCurrentReceipt(pos: Int): FeesReceipt = receiptList.get(pos)
}