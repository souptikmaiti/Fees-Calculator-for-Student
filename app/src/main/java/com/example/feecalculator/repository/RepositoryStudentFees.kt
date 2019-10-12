package com.example.feecalculator.repository

import android.app.Application
import android.os.AsyncTask
import com.example.feecalculator.data.daos.FeesReceiptDao
import com.example.feecalculator.data.daos.StudentDao
import com.example.feecalculator.data.database.DatabaseFeesCal
import com.example.feecalculator.data.entitites.FeesReceipt
import com.example.feecalculator.data.entitites.Student

class RepositoryStudentFees(private val db:DatabaseFeesCal) {
    private var studentDao: StudentDao = db.studentDao()
    private var feesReceiptDao: FeesReceiptDao = db.feesReceiptDao()

    suspend fun insertStudent(student: Student){
        studentDao.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student){
        studentDao.deleteStudent(student)
    }

    suspend fun updateStudent(student: Student){
        studentDao.updateStudent(student)
    }

    fun getPayers(typePayer:String) = studentDao.getPayers(typePayer)

    fun getAllPayers() = studentDao.getAllPayers()

    suspend fun insertReceipt(receipt: FeesReceipt){
        feesReceiptDao.insertReceipt(receipt)
    }

    suspend fun deleteReceipt(receipt: FeesReceipt){
        feesReceiptDao.deleteReceipt(receipt)
    }

    suspend fun updateReceipt(receipt: FeesReceipt){
        feesReceiptDao.updateReceipt(receipt)
    }

    fun getAllReceiptForStudent(stuentId:Int) = feesReceiptDao.getReceiptsForTheStudent(stuentId)

    suspend fun getSumForPayer(id:Int) = feesReceiptDao.getSumForPayer(id)
}