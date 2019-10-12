package com.example.feecalculator.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.example.feecalculator.data.entitites.Student
import com.example.feecalculator.repository.RepositoryStudentFees
import com.example.feecalculator.util.Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


class ViewModelStudent(private val repo: RepositoryStudentFees): ViewModel() {

    var studentViewModelListener: StudentViewModelListener?=null
    var fullName: String?= null
    var typePayer: String?=null
    var fatherName:String?= null
    var dob :String?= null
    var doj:String?= null
    var adharNo:String?= null
    var mobileNo:String?=null
    var photoUri: String?=null
    var imageBitmap:Bitmap?=null
    var uniquePhotoFile: File?=null

    var student:Student ?=null

    private fun createStudent(){
        var id: Int?= student?.id
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        var dateJoin: Date ?= null
        var dateBirth: Date?=null
        if(fullName.isNullOrBlank()){
            studentViewModelListener?.onFailure("Name can't be blank")
            return
        }else if(doj.isNullOrBlank()){
            studentViewModelListener?.onFailure("Admission Date can't be blank")
            return
        }else{
            try{
                dateJoin= formatter.parse(doj)
            }catch (e: Exception){
                studentViewModelListener?.onFailure( /*e.message.toString() + "\n" + */"Expected date format: dd/mm/yyyy")
                return
            }
            try{
                dateBirth =  formatter.parse(dob)
            }catch (e: Exception){
                studentViewModelListener?.onFailure(e.message.toString())
            }
            if(id == null){
                if(uniquePhotoFile!=null && imageBitmap!=null){
                    photoUri = saveToInternalStorage()
                }
                student = Student(fullName = fullName!!,fatherName = fatherName,
                    dob = dateBirth, doj = dateJoin!!, adharNo = adharNo, mobileNo = mobileNo, typePayer = typePayer, imagePath = photoUri)

                insertStudent(student!!)
            }else{
                if(uniquePhotoFile!=null && imageBitmap!=null){
                    if(student?.imagePath!=null){
                        deleteImage(student?.imagePath!!)
                        photoUri = saveToInternalStorage()
                    }else{
                        photoUri = saveToInternalStorage()
                    }
                }
                student = Student(fullName = fullName!!,fatherName = fatherName,
                    dob = dateBirth, doj = dateJoin!!, adharNo = adharNo, mobileNo = mobileNo, typePayer = typePayer, imagePath = photoUri)
                student?.id = id
                updateStudent(student!!)
            }

        }
    }

    private fun deleteImage(imagePath: String) {
        runBlocking {
            withContext(Dispatchers.IO){
                var oldImageFile: File = File(imagePath)
                if(oldImageFile.exists()){
                    oldImageFile.delete()
                }
            }
        }
    }

    fun getPayers(typePayer:String)= repo.getPayers(typePayer)

    fun getAllPayers()= repo.getAllPayers()

    fun upsertStudent(){
        createStudent()
    }

    private fun insertStudent(student: Student){
        Coroutines.main {
            repo.insertStudent(student)
            studentViewModelListener?.onSuccess("New Student Added")
        }
    }

    fun deleteStudent(student: Student){
        if(student.imagePath!=null){
            deleteImage(student.imagePath!!)
        }
        Coroutines.main {
            repo.deleteStudent(student)
            studentViewModelListener?.onSuccess("Student Deleted")
        }
    }

    private fun updateStudent(student: Student){
        Coroutines.main {
            repo.updateStudent(student)
            studentViewModelListener?.onSuccess("Student Updated")
        }
    }

    fun setStudentInfo(student: Student?){
        this.student = student
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

        fullName = student?.fullName
        fatherName = student?.fatherName
        student?.dob?.let { dob = formatter.format(it) }
        student?.doj?.let {doj = formatter.format(it) }
        adharNo = student?.adharNo
        mobileNo = student?.mobileNo
        typePayer = student?.typePayer
        photoUri = student?.imagePath
    }

    fun saveToInternalStorage():String?{
        var absoluteFilePath:String? = null
        runBlocking {
            withContext(Dispatchers.IO){
                var stream: OutputStream = FileOutputStream(uniquePhotoFile);
                imageBitmap!!.compress(Bitmap.CompressFormat.JPEG,100,stream);
                absoluteFilePath =  uniquePhotoFile!!.absolutePath
            }
        }
        return absoluteFilePath
    }
}