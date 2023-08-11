package com.sk.directudhar.ui.applyloan

import android.R
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sk.directudhar.databinding.FragmentAadhaarCardBinding
import com.sk.directudhar.databinding.FragmentApplyLoanBinding
import com.sk.directudhar.databinding.FragmentBusinessDetailsBinding
import com.sk.directudhar.ui.applyloan.ApplyLoanFactory
import com.sk.directudhar.ui.applyloan.ApplyLoanViewModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.Utils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class BusinessDetailsFragment : Fragment() {
    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentBusinessDetailsBinding

    lateinit var applyLoanViewModel: ApplyLoanViewModel

    lateinit var proceedBottomDialog: BottomSheetDialog

    @Inject
    lateinit var applyLoanFactory: ApplyLoanFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentBusinessDetailsBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

        mBinding.etusinessIncorporationDate.setOnClickListener {
            showDatePicker(mBinding.etusinessIncorporationDate)


        }

    }
    private fun CheckDate(etDate: EditText) {
        try {
            val c1 = Calendar.getInstance()
            val mYear = c1[Calendar.YEAR]
            val mMonth = c1[Calendar.MONTH]
            val mDay = c1[Calendar.DAY_OF_MONTH]
            println("the selected $mDay")
            val dialog = DatePickerDialog(
                requireActivity(),
                { view: DatePicker?, mYear1: Int, mMonth1: Int, mDay1: Int ->
                    try {
                        etDate.setText(
                            StringBuilder() // Month is 0 based so add 1
                                .append(mDay1).append("/").append(mMonth1 + 1).append("/")
                                .append(mYear1).append(" ")
                        )
                        //date = Utils.getSimpleDateFormat(etDate.text.toString().trim { it <= ' ' })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                mYear,
                mMonth,
                mDay
            )
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun showDatePicker(etDate: EditText) {
        val c = Calendar.getInstance()
        val currentYear = c[Calendar.YEAR]
        val currentMonth = c[Calendar.MONTH]
        val currentDay = c[Calendar.DAY_OF_MONTH]

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = formatDate(year, monthOfYear, dayOfMonth)
            //    etDate.setText(selectedDate)
                etDate.setText(
                    StringBuilder() // Month is 0 based so add 1
                        .append(dayOfMonth).append("/").append(monthOfYear + 1).append("/")
                        .append(year).append(" ")
                )

            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}