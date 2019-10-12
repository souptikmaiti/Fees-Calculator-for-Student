package com.example.feecalculator.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.feecalculator.data.daos.FeesReceiptDao
import com.example.feecalculator.data.daos.StudentDao
import com.example.feecalculator.data.entitites.FeesReceipt
import com.example.feecalculator.data.entitites.Student

@Database(entities = [Student::class, FeesReceipt::class], version = 1)
@TypeConverters(DateConverter::class)
abstract class DatabaseFeesCal : RoomDatabase(){
    abstract fun studentDao(): StudentDao
    abstract fun feesReceiptDao(): FeesReceiptDao

    companion object{

        @Volatile
        private var instance: DatabaseFeesCal? = null

        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext, DatabaseFeesCal::class.java,
            "student_fees.db").build()
    }
}