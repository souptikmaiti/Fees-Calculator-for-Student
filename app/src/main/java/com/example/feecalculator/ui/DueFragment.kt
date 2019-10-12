package com.example.feecalculator.ui


import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.feecalculator.R
import com.example.feecalculator.adapter.ReceiptAdapter
import com.example.feecalculator.adapter.ReceiptItemListener
import com.example.feecalculator.data.entitites.FeesReceipt
import com.example.feecalculator.data.entitites.Student
import com.example.feecalculator.util.snackbar
import com.example.feecalculator.viewmodel.ReceiptViewModelListener
import com.example.feecalculator.viewmodel.ViewModelFactoryReceipt
import com.example.feecalculator.viewmodel.ViewModelReceipt
import kotlinx.android.synthetic.main.fragment_due.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class DueFragment : Fragment(), KodeinAware, ReceiptItemListener, ReceiptViewModelListener {
    override val kodein: Kodein by kodein()
    private val factoryReceipt: ViewModelFactoryReceipt by instance()
    private lateinit var viewModelReceipt: ViewModelReceipt
    private var receiptAdapter:ReceiptAdapter?=null
    private var student:Student?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewModelReceipt = ViewModelProviders.of(this,factoryReceipt).get(ViewModelReceipt::class.java)
        viewModelReceipt.viewModelListener = this
        return inflater.inflate(R.layout.fragment_due, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            student = it.getSerializable("selectedStudent") as Student
        }

        student?.let {
            tv_student_name.text = "${it.fullName}"
            rv_receipts.layoutManager = LinearLayoutManager(context)
            rv_receipts.setHasFixedSize(true)

            viewModelReceipt.getAllReceipts(it.id).observe(this, Observer {receiptList->
                receiptAdapter = ReceiptAdapter(receiptList)
                rv_receipts.adapter = receiptAdapter
                if(it.typePayer.equals("donor")){
                    tv_due.text = "Total Donation: ${viewModelReceipt.calculatePayments(student!!.id)}"
                }
                else{
                    tv_due.text = "Due Amount: ${calculateDues(it.doj, it.id)}"
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
                var swipedReceipt = receiptAdapter?.getCurrentReceipt(viewHolder.getAdapterPosition())
                swipedReceipt?.let {
                    confirmDeletion(it, viewHolder.getAdapterPosition())
                }

            }
        }).attachToRecyclerView(rv_receipts)
    }

    private fun calculateDues(doj: Date,id: Int): Double {
        return (viewModelReceipt.calculateMonths(doj)*viewModelReceipt.rate - viewModelReceipt.calculatePayments(id))
    }


    private fun confirmDeletion(receipt: FeesReceipt, pos:Int){
        AlertDialog.Builder(context).apply {
            setTitle("Are you sure ?")
            setMessage("Delete operation can't be undone")
            setPositiveButton("Yes"){_,_->
                viewModelReceipt.deleteReceipt(receipt)
            }
            setNegativeButton("No"){_,_->
                receiptAdapter?.notifyItemChanged(pos)
            }
        }.create().show()
    }

    override fun onReceiptItemSelected(receipt: FeesReceipt) {

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.due_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.due_details ->{
                var bundle = bundleOf("selectedStudent" to student)
                findNavController().navigate(R.id.action_dueFragment_to_detailsDueFragment, bundle)
            }

            R.id.home_go ->{
                findNavController().navigate(R.id.action_dueFragment_to_homeFragment)
            }
        }
        return true
    }

    override fun onSuccess(msg: String, operation: String) {
        relative_root_layout.snackbar(msg)
    }

    override fun onFailure(msg: String) {
        relative_root_layout.snackbar(msg)
    }
}
