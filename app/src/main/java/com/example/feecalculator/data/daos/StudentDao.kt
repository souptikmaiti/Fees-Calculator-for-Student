package com.example.feecalculator.data.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.feecalculator.data.entitites.Student

@Dao
interface StudentDao {

    @Insert
    suspend fun insertStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Update
    suspend fun updateStudent(student: Student)

    @Query("select * from student_table where type_payer =:typePayer order by full_name")
    fun getPayers(typePayer:String): LiveData<Array<Student>>

    @Query("select * from student_table order by full_name")
    fun getAllPayers(): LiveData<Array<Student>>
}