package com.example.feecalculator.data.entitites

import androidx.room.*
import java.io.Serializable
import java.util.*

@Entity(tableName = "fees_receipt_table",
    foreignKeys = [ForeignKey(entity = Student::class,
        parentColumns = ["id"],
        childColumns = ["student_id"],
        onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["student_id"])])
data class FeesReceipt(
    @ColumnInfo(name = "date_of_payment")
    var dop: Date,

    var amount: Double,

    @ColumnInfo(name = "student_id")
    var studentId: Int
):Serializable{
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}