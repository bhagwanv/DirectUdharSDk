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
        spinnerView()
        return mBinding.root
    }

    private fun initView() {

        mBinding.etusinessIncorporationDate.setOnClickListener {
            showDatePicker(mBinding.etusinessIncorporationDate)


        }

    }

    fun spinnerView() {
        val businessArray = listOf(
            "One",
            "Two"
        )
        val incomeSlabArray = listOf(
            "1 lac-3 lac",
            "3 lac-5 lac"
        )
        val ownerShipArray = listOf(
            "Rented",
            "Owned",
            "Owned by parents",
            "Owned by Spouse"
        )

        val manualBillUploadArray = listOf(
            "Electricity bill upload",
            "Upload Bill Manual",
            "Customer Number"
        )
        val businessAdapter = ArrayAdapter(activitySDk, R.layout.simple_list_item_1, businessArray)
        mBinding.spBusinessType.adapter = businessAdapter
        mBinding.spBusinessType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Toast.makeText(
                        activitySDk,
                        "Type" + " " + businessArray[position],
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

        val incomeAdapter = ArrayAdapter(activitySDk, R.layout.simple_list_item_1, incomeSlabArray)
        mBinding.spIncomeSlab.adapter = incomeAdapter
        mBinding.spIncomeSlab.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    activitySDk,
                    "Type" + " " + incomeSlabArray[position],
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

        val ownerShipAdapter =
            ArrayAdapter(activitySDk, R.layout.simple_list_item_1, ownerShipArray)
        mBinding.spOwnerShipType.adapter = ownerShipAdapter
        mBinding.spOwnerShipType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Toast.makeText(
                        activitySDk,
                        "Type" + " " + ownerShipArray[position],
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
            }

        val manualBillUploadArrayAdapter =
            ArrayAdapter(activitySDk, R.layout.simple_list_item_1, manualBillUploadArray)
        mBinding.spManualBillUploadType.adapter = manualBillUploadArrayAdapter
        mBinding.spManualBillUploadType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Toast.makeText(
                        activitySDk,
                        "Type" + " " + ownerShipArray[position],
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // Code to perform some action when nothing is selected
                }
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