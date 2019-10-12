package com.example.feecalculator.adapter

import com.example.feecalculator.data.entitites.Student

interface StudentItemListener {
    fun onItemSelected(student: Student)
    fun onItemClicked(student: Student?)
}