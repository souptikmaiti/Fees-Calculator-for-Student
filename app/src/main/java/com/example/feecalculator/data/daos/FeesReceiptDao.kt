package com.example.feecalculator.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.feecalculator.data.entitites.FeesReceipt

@Dao
interface FeesReceiptDao {

    @Insert
    suspend fun insertReceipt(receipt: FeesReceipt)

    @Delete
    suspend fun deleteReceipt(receipt: FeesReceipt)

    @Update
    suspend fun updateReceipt(receipt: FeesReceipt)

    @Query("select * from fees_receipt_table where student_id = :id order by date_of_payment desc")
    fun getReceiptsForTheStudent(id:Int) : LiveData<Array<FeesReceipt>>

    @Query("select sum(amount) as total FROM fees_receipt_table WHERE student_id = :id")
    suspend fun getSumForPayer(id:Int): Double?
}