package com.example.feecalculator.ui


import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.feecalculator.R
import com.example.feecalculator.data.entitites.Student
import com.example.feecalculator.databinding.FragmentReceiptBinding
import com.example.feecalculator.util.snackbar
import com.example.feecalculator.viewmodel.ReceiptViewModelListener
import com.example.feecalculator.viewmodel.ViewModelFactoryReceipt
import com.example.feecalculator.viewmodel.ViewModelReceipt
import kotlinx.android.synthetic.main.fragment_receipt.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ReceiptFragment : Fragment(),KodeinAware,ReceiptViewModelListener {
    override val kodein: Kodein by kodein()
    private val factoryReceipt: ViewModelFactoryReceipt by instance()
    private lateinit var viewModel: ViewModelReceipt
    private var student:Student?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        var binding:FragmentReceiptBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_receipt,container,false)
        viewModel = ViewModelProviders.of(this,factoryReceipt).get(ViewModelReceipt::class.java)
        binding.viewModelReceipt = viewModel
        binding.lifecycleOwner = this
        viewModel.viewModelListener = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            student = it.getSerializable("selectedStudent") as Student
        }

        student?.let {
            viewModel.setStudentSelected(it)

            if(it.typePayer!!.equals("donor")){
                tv_doj.visibility = View.GONE
            }else{
                tv_doj.visibility = View.VISIBLE
            }
        }

        btn_receipts.setOnClickListener {
            var bundle = bundleOf("selectedStudent" to student)
            findNavController().navigate(R.id.action_receiptFragment_to_dueFragment,bundle)
        }

    }

    override fun onSuccess(msg: String, operation: String) {
        root_linear_layout.snackbar(msg)
        if(operation =="insert"){
            var bundle = bundleOf("selectedStudent" to student)
            findNavController().navigate(R.id.action_receiptFragment_to_dueFragment,bundle)
        }
    }

    override fun onFailure(msg: String) {
        root_linear_layout.snackbar(msg)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.go_to_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home_btn -> findNavController().navigate(R.id.action_receiptFragment_to_homeFragment)
        }
        return true
    }
}
