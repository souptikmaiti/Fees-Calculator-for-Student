package com.example.feecalculator.data.entitites

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "student_table")
data class Student (
    @NonNull
    @ColumnInfo(name = "full_name")
    var fullName: String,

    @ColumnInfo(name = "father_name")
    var fatherName: String? = null,

    @ColumnInfo(name = "adhar_no")
    var adharNo: String? = null,

    @ColumnInfo(name = "mobile_no")
    var mobileNo: String? = null,

    @ColumnInfo(name = "date_of_birth")
    var dob: Date? = null,

    @NonNull
    @ColumnInfo(name = "date_of_admission")
    var doj: Date,

    @ColumnInfo(name = "profile_image")
    var imagePath: String?=null,

    @ColumnInfo(name = "type_payer")
    var typePayer:String? = null
): Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Ignore
    var isSelected: Boolean = false
}