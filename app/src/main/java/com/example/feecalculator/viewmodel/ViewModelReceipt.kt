package com.example.feecalculator.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.feecalculator.data.entitites.FeesReceipt
import com.example.feecalculator.data.entitites.Student
import com.example.feecalculator.repository.RepositoryStudentFees
import com.example.feecalculator.util.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class ViewModelReceipt(private val repo:RepositoryStudentFees): ViewModel() {
    var viewModelListener:ReceiptViewModelListener?=null
    var student:Student? =null
    var receipt:FeesReceipt?=null

    var dop: String?=LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    var amount: String? = "150"
    var name:String?=null
    var doj:String? = null

    var rate:Double = 150.0

    fun onReceiptButton(view:View){
        createReceipt()
    }

    fun setStudentSelected(student: Student?){
        this.student = student
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        name = student?.fullName
        student?.doj?.let {doj = "Date of Admission: " + formatter.format(it) }
    }

    private fun createReceipt() {
        var amountPaid: Double?=null
        var datePayment:Date? =null
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        var studentId = student?.id
        if(studentId==null) return
        if (amount.isNullOrBlank()) {
            viewModelListener?.onFailure("Please Enter an amount")
            return
        } else if (dop.isNullOrBlank()) {
            viewModelListener?.onFailure("Please Enter the date of payment")
            return
        } else {
            try {
                amountPaid = amount!!.toDouble()
            } catch (e: Exception) {
                viewModelListener?.onFailure( "Give a valid number")
                return
            }
            try {
                datePayment = formatter.parse(dop)
            } catch (e: Exception) {
                viewModelListener?.onFailure( "Give a valid Date")
                return
            }

            receipt = FeesReceipt(dop=datePayment, amount = amountPaid, studentId = studentId)
            insertReceipt(receipt!!)
        }
    }

    private fun insertReceipt(receipt: FeesReceipt){
        Coroutines.main {
            repo.insertReceipt(receipt)
            viewModelListener?.onSuccess("Receipt Added", "insert")
        }
    }

    private fun updateReceipt(receipt: FeesReceipt){
        Coroutines.main {
            repo.updateReceipt(receipt)
            viewModelListener?.onSuccess("Receipt Updated","update")
        }
    }

    fun deleteReceipt(receipt: FeesReceipt){
        Coroutines.main {
            repo.deleteReceipt(receipt)
            viewModelListener?.onSuccess("Receipt Deleted","delete")
        }
    }

    fun getAllReceipts(studentId: Int):LiveData<Array<FeesReceipt>> = repo.getAllReceiptForStudent(studentId)

    fun calculateMonths(dateJoined:Date):Int{
        var current = LocalDateTime.now()
        var dateJoin: LocalDate = dateJoined.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        var diffYear = current.year - dateJoin.year
        var diffMonth = (diffYear * 12) + current.month.value - dateJoin.month.value
        return diffMonth
    }

     fun calculatePayments(id:Int): Double{
        var sum:Double =0.0
        runBlocking {
            withContext(Dispatchers.IO){ repo.getSumForPayer(id) }?.let {
                sum = it
            }
        }
        return sum
     }

    /*suspend fun calculatePayments(id:Int): Double{
        return withContext(Dispatchers.IO){ repo.getSumForPayer(id) }
    }*/
}