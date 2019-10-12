package com.example.feecalculator.ui


import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.feecalculator.R
import com.example.feecalculator.data.entitites.Student
import com.example.feecalculator.databinding.FragmentNewStudentBinding
import com.example.feecalculator.util.snackbar
import com.example.feecalculator.viewmodel.StudentViewModelListener
import com.example.feecalculator.viewmodel.ViewModelFactoryStudent
import com.example.feecalculator.viewmodel.ViewModelStudent
import kotlinx.android.synthetic.main.fragment_new_student.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NewStudentFragment : Fragment(),KodeinAware,StudentViewModelListener{

    override val kodein: Kodein by kodein()
    private val factory:ViewModelFactoryStudent by instance()
    private lateinit var viewModelStudent: ViewModelStudent
    private var student:Student?=null
    val REQUEST_IMAGE_CAPTURE = 55

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val binding:FragmentNewStudentBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_new_student,container,false)
        viewModelStudent = ViewModelProviders.of(this,factory).get(ViewModelStudent::class.java)
        binding.studentViewModel = viewModelStudent
        viewModelStudent.studentViewModelListener = this
        activity?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpSpinner()
        arguments?.let {
            student = arguments?.getSerializable("selectedStudent") as Student
        }

        student?.let {
            viewModelStudent.setStudentInfo(it)
            if(it.imagePath!=null){
                Glide.with(this).load(it.imagePath).into(iv_profile)
            }
        }

        ib_photo.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun setUpSpinner(){
        ArrayAdapter.createFromResource(
            context!!,
            R.array.type_payer_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            type_spinner.adapter = adapter
        }
        type_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(student !=null){
                    if(student!!.typePayer.equals("student")) type_spinner.setSelection(0)
                    if(student!!.typePayer.equals("donor")) type_spinner.setSelection(1)
                }
                viewModelStudent.typePayer = p0?.getItemAtPosition(p2) as String
                if(viewModelStudent.typePayer =="donor"){
                    et_father.visibility = View.GONE
                    et_dob.visibility = View.GONE
                    et_adhar.visibility = View.GONE
                    et_doj.visibility = View.GONE
                    if(student == null){
                        var djoin: String?= LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        viewModelStudent.doj = djoin
                    }


                }else{
                    et_father.visibility = View.VISIBLE
                    et_dob.visibility = View.VISIBLE
                    et_adhar.visibility = View.VISIBLE
                    et_doj.visibility = View.VISIBLE
                    if(student==null){
                        viewModelStudent.doj = null
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.new_student_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.save_btn ->{
                viewModelStudent.upsertStudent()
            }
            R.id.delete_btn ->{
                if(student!=null){
                    confirmDeletion()
                }else{
                    root_linear_layout.snackbar("Can't delete")
                }
            }
            R.id.home_btn ->{
                findNavController().navigate(R.id.action_newStudentFragment_to_homeFragment)
            }
        }
        return true
    }

    private fun confirmDeletion(){
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure ?")
            setMessage("Delete operation can't be undone")
            setPositiveButton("Yes"){_,_->
                viewModelStudent.deleteStudent(student!!)
            }
            setNegativeButton("No"){_,_->

            }
        }.create().show()
    }

    override fun onSuccess(message: String) {
        root_linear_layout.snackbar(message)
        findNavController().navigate(R.id.action_newStudentFragment_to_homeFragment)
    }

    override fun onFailure(message: String) {
        root_linear_layout.snackbar(message)
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(activity!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            prepareSavingtoInternalStorage(imageBitmap)
            iv_profile.setImageBitmap(imageBitmap)
        }
    }

    private fun prepareSavingtoInternalStorage(imageBitmap: Bitmap) {
        var imFolder = context?.getDir("Images", MODE_PRIVATE)
        var uniqueFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
        var file = File(imFolder, uniqueFileName + ".jpeg")
        viewModelStudent.imageBitmap = imageBitmap
        viewModelStudent.uniquePhotoFile = file
    }
}
