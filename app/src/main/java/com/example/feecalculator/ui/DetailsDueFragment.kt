package com.example.feecalculator.ui


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.feecalculator.R
import com.example.feecalculator.data.entitites.Student
import com.example.feecalculator.viewmodel.ViewModelFactoryReceipt
import com.example.feecalculator.viewmodel.ViewModelFactoryStudent
import com.example.feecalculator.viewmodel.ViewModelReceipt
import kotlinx.android.synthetic.main.fragment_details_due.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class DetailsDueFragment : Fragment(), KodeinAware {
    override val kodein: Kodein by kodein()
    private val factory: ViewModelFactoryReceipt by instance()

    var student:Student?=null
    lateinit var receiptModel: ViewModelReceipt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        receiptModel = ViewModelProviders.of(this,factory).get(ViewModelReceipt::class.java)
        return inflater.inflate(R.layout.fragment_details_due, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {arg->
            student = arg.getSerializable("selectedStudent") as Student
            student?.let {
                var paid = receiptModel.calculatePayments(it.id)
                if(it.typePayer!!.equals("student")){
                    var months = receiptModel.calculateMonths(it.doj)
                    var bill = months * receiptModel.rate
                    var due = bill - paid
                    tv_due_details.append("Payment Details of ${it.fullName}\n")
                    tv_due_details.append("Total Months: $months\n")
                    tv_due_details.append("Total Bill: $bill\n")
                    tv_due_details.append("Total Amount Paid: $paid\n")
                    tv_due_details.append("Total Amount Due: $due\n")
                }
                else{
                    tv_due_details.append("Received total Rs $paid from ${it.fullName}")
                }

            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.go_to_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.home_btn ->{
                findNavController().navigate(R.id.action_detailsDueFragment_to_homeFragment)
            }
        }
        return true
    }
}
