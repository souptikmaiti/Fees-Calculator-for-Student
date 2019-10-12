package com.example.feecalculator.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.feecalculator.R
import com.example.feecalculator.data.entitites.Student
import kotlinx.android.synthetic.main.item_student.view.*
import java.text.SimpleDateFormat
import java.util.*


class StudentsAdapter(var studentList: Array<Student>): RecyclerView.Adapter<StudentsAdapter.StudentHolder>() {
    var studentItemListener: StudentItemListener?= null
    var indexSelected: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentHolder {
        return StudentHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_student,parent,false))
    }

    override fun getItemCount() = studentList.size

    override fun onBindViewHolder(holder: StudentHolder, position: Int) {
        var student:Student = studentList.get(position)

        holder.bind(student);
    }


    inner class StudentHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

        fun bind(student: Student) {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

            if(!student.imagePath.isNullOrBlank()){
                Glide.with(itemView.context).load(student.imagePath).into(itemView.iv_student)
            }else{
                Glide.with(itemView.context).load(R.drawable.ic_tag_faces).into(itemView.iv_student)
            }
            itemView.tv_name.text = student.fullName
            if(student.dob == null){
                itemView.tv_dob.text = "DoB: "
            }else{
                itemView.tv_dob.text = "DoB: " + formatter.format(student.dob)
            }

            if(student.mobileNo == null){
                itemView.tv_mobile.text = "mobile: "
            }else{
                itemView.tv_mobile.text = "mobile: " + student.mobileNo
            }

            itemView.tv_doj.text = "Admission: " + formatter.format(student.doj)
            itemView.isSelected = student.isSelected

            if(student.typePayer.isNullOrBlank()){
                itemView.tv_type.text = "Type: "
            }else{
                itemView.tv_type.text = "Type: " + student.typePayer
                if(student.typePayer!!.equals("donor")){
                    itemView.tv_dob.visibility = View.GONE
                    itemView.tv_doj.visibility = View.GONE
                }else{
                    itemView.tv_dob.visibility = View.VISIBLE
                    itemView.tv_doj.visibility = View.VISIBLE
                }
            }

            itemView.card_student.setOnLongClickListener {
                if(indexSelected != -1){
                    resetBeforeLongSelection()
                }
                studentItemListener?.onItemSelected(student)
                return@setOnLongClickListener true
            }

            itemView.card_student.setOnClickListener {
                resetSelection(adapterPosition, student)
                if(indexSelected != -1){
                    studentItemListener?.onItemClicked(student)
                }else{
                    studentItemListener?.onItemClicked(null)
                }
            }
        }
    }

    private fun resetBeforeLongSelection() {
        studentList[indexSelected].isSelected = false
        notifyItemChanged(indexSelected)
        indexSelected = -1
    }

    private fun resetSelection(pos: Int, student: Student) {
        if(indexSelected == -1) {   //first selection
            student.isSelected = true
            notifyItemChanged(pos)
            indexSelected = pos
            return
        }else if(indexSelected == pos){  //already selected row
            student.isSelected = false
            notifyItemChanged(pos)
            indexSelected = -1
            return
        }else{
            studentList[indexSelected].isSelected = false
            notifyItemChanged(indexSelected)
            student.isSelected = true
            notifyItemChanged(pos)
            indexSelected = pos
        }
    }

    fun getCurrentStudent(adapterPosition: Int) = studentList.get(adapterPosition)
}