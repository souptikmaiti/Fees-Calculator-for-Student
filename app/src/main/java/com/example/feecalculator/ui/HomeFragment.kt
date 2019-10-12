package com.example.feecalculator.ui


import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feecalculator.R
import com.example.feecalculator.adapter.StudentItemListener
import com.example.feecalculator.adapter.StudentsAdapter
import com.example.feecalculator.data.entitites.Student
import com.example.feecalculator.util.snackbar
import com.example.feecalculator.viewmodel.StudentViewModelListener
import com.example.feecalculator.viewmodel.ViewModelFactoryStudent
import com.example.feecalculator.viewmodel.ViewModelStudent
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class HomeFragment : Fragment(), KodeinAware, StudentItemListener, StudentViewModelListener{

    override val kodein by kodein()

    private val factory: ViewModelFactoryStudent by instance()
    private lateinit var viewModelStudent: ViewModelStudent
    private var studentsAdapter: StudentsAdapter? = null
    private var currentStudent: Student? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewModelStudent = ViewModelProviders.of(this,factory).get(ViewModelStudent::class.java)
        viewModelStudent.studentViewModelListener = this
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentStudent = null
        setRecyclerview("all")
        setBottomNav()
    }

    fun setRecyclerview(typePayer: String){
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.setHasFixedSize(true)
        if(typePayer.equals("all")){
            viewModelStudent.getAllPayers()?.observe(this, Observer { studentList ->
                studentList?.let {
                    studentsAdapter = StudentsAdapter(studentList)
                    recycler_view.adapter = studentsAdapter
                    studentsAdapter?.studentItemListener = this
                }
            })
        }else{
            viewModelStudent.getPayers(typePayer)?.observe(this, Observer { studentList ->
                studentList?.let {
                    studentsAdapter = StudentsAdapter(studentList)
                    recycler_view.adapter = studentsAdapter
                    studentsAdapter?.studentItemListener = this
                }
            })
        }

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var swipedStudent = studentsAdapter?.getCurrentStudent(viewHolder.getAdapterPosition())
                swipedStudent?.let {
                    confirmDeletion(it, viewHolder.getAdapterPosition())
                }

            }
        }).attachToRecyclerView(recycler_view)


    }

    private fun setBottomNav() {
        bottom_nav.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.b_nav_payments ->{
                    if(currentStudent!=null){
                        var bundle = bundleOf("selectedStudent" to currentStudent)
                        findNavController().navigate(R.id.action_homeFragment_to_dueFragment,bundle)
                    }else{
                        Toast.makeText(context,"Please select an item", Toast.LENGTH_LONG).show()
                    }
                }
                R.id.b_nav_receipt ->{
                    if(currentStudent!=null){
                        var bundle = bundleOf("selectedStudent" to currentStudent)
                        findNavController().navigate(R.id.action_homeFragment_to_receiptFragment,bundle)
                    }else{
                        Toast.makeText(context,"Please select an item", Toast.LENGTH_LONG).show()
                    }
                }
                R.id.b_nav_account ->{
                    if(currentStudent!=null){
                        var bundle = bundleOf("selectedStudent" to currentStudent)
                        findNavController().navigate(R.id.action_homeFragment_to_newStudentFragment,bundle)
                    }else{
                        Toast.makeText(context,"Please select an item", Toast.LENGTH_LONG).show()
                    }
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    private fun confirmDeletion(student: Student, pos:Int){
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure ?")
            setMessage("Delete operation can't be undone")
            setPositiveButton("Yes"){_,_->
                viewModelStudent.deleteStudent(student)
            }
            setNegativeButton("No"){_,_->
                studentsAdapter?.notifyItemChanged(pos)
            }
        }.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_student_btn ->{
                findNavController().navigate(R.id.action_homeFragment_to_newStudentFragment)
            }
            R.id.type_student ->{
                setRecyclerview("student")
            }
            R.id.type_donor ->{
                setRecyclerview("donor")
            }
            R.id.type_all ->{
                setRecyclerview("all")
            }
        }
        return true
    }

    override fun onItemSelected(student: Student) {
        currentStudent = student
        AlertDialog.Builder(context).apply {
            setTitle("What do you want with this student ?")
            setMessage("Select your action")
            setPositiveButton("Modify"){_,_->
                var bundle = bundleOf("selectedStudent" to currentStudent)
                findNavController().navigate(R.id.action_homeFragment_to_newStudentFragment, bundle)
            }
            setNeutralButton("Receipt Manager"){_,_->
                var bundle = bundleOf("selectedStudent" to currentStudent)
                findNavController().navigate(R.id.action_homeFragment_to_receiptFragment, bundle)
            }
            setNegativeButton("Cancel"){_,_->

            }
        }.create().show()
    }

    override fun onItemClicked(student: Student?) {
        currentStudent = student
    }

    override fun onSuccess(message: String) {
        root_frame_layout.snackbar(message)
    }

    override fun onFailure(message: String) {
        root_frame_layout.snackbar(message)
    }
}
