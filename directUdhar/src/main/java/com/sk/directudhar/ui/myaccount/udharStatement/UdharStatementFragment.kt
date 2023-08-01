package com.sk.directudhar.ui.myaccount.udharStatement

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.DialogDateFilterApplyBinding
import com.sk.directudhar.databinding.DialogHistoryBinding
import com.sk.directudhar.databinding.FragmentUdharStatementBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.ui.myaccount.UdharStatementModel
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

class UdharStatementFragment : Fragment() {

    @Inject
    lateinit var udharStatementFactory: UdharStatementFactory

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding: FragmentUdharStatementBinding

    private lateinit var udharStatementViewModel: UdharStatementViewModel

    private val args: UdharStatementFragmentArgs by navArgs()

    private lateinit var adapter: UdharStatementAdapter
    private lateinit var filterBottomDialog: BottomSheetDialog

    private lateinit var mFilterBottomDialogBinding: DialogDateFilterApplyBinding
    private var monthType: Int = 4
    private var fromDate = ""
    private var toDate = ""
    private lateinit var transDetail: UdharStatementModel
    private var dialog: AlertDialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUdharStatementBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    override fun onResume() {
        super.onResume()
        dialog?.dismiss()
    }
    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectUdharStatementFragment(this)
        udharStatementViewModel =
            ViewModelProvider(this, udharStatementFactory)[UdharStatementViewModel::class.java]

        bindWidgetsWithAnEvent()
        setupTabLayout()

        mBinding.btnDownload.setOnClickListener {
            filterDialog()
        }
        udharStatementViewModel.getUdharStatementResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    activitySDk.toast(it.errorMessage)
                    mBinding.rvUdharStatement.visibility = View.GONE
                    mBinding.tvDataNotFound.visibility = View.VISIBLE
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    setRecyclerViewData(it.data)
                }
            }
        }

        udharStatementViewModel.downloadReportResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    activitySDk.toast(it.errorMessage)
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    if (it.data.file != null) {
                        lifecycleScope.launch {
                            val file =
                                downloadPDFFileWithProgress(
                                    activitySDk,
                                    it.data.file!!,
                                )
                            if (file != null) {
                                Toast.makeText(
                                    activitySDk,
                                    "PDF downloaded successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    activitySDk,
                                    "PDF download failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        activitySDk.toast(it.data.error!!)
                    }
                }
            }
        }

        udharStatementViewModel.transactionDetailResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    activitySDk.toast(it.errorMessage)
                }

                is NetworkResult.Success -> {
                    Log.e("TAG", "initView: dsds12113231", )
                    ProgressDialog.instance!!.dismiss()
                    openTransactionDetailPopup(it.data)

                }
            }
        }

        udharStatementViewModel.historyResponse.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    activitySDk.toast(it.errorMessage)
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    if (it.data.isEmpty()) {
                        activitySDk.toast("Data Not Found")
                    } else {
                        openHistoryBottomsheet(activitySDk, it.data)
                    }
                }
            }
        }
    }

    private fun openHistoryBottomsheet(
        mContx: MainActivitySDk,
        data: ArrayList<HistoryResponseModel>
    ) {
        val historyBottomDialog = BottomSheetDialog(mContx, R.style.Theme_Design_BottomSheetDialog)
        val mHistoryBottomDialogBinding: DialogHistoryBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.dialog_history, null, false)
        historyBottomDialog.setContentView(mHistoryBottomDialogBinding.root)
        historyBottomDialog.show()

        mHistoryBottomDialogBinding.imClose.setOnClickListener {
            historyBottomDialog.dismiss()
        }
        val adapter = HistoryAdapter(data)
        mHistoryBottomDialogBinding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        mHistoryBottomDialogBinding.rvHistory.adapter = adapter
    }

    private fun openTransactionDetailPopup(
        data: ArrayList<TransactionDetailResponseModel>,
    ) {

        val alertDialogBuilder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.transaction_detail_dialog_layout, null)
        alertDialogBuilder.setView(view)
        alertDialogBuilder.setCancelable(true)
        dialog = alertDialogBuilder.create()
        dialog?.show()

        val date = view.findViewById<TextView>(R.id.tvDate)
        val txnId = view.findViewById<TextView>(R.id.tvTxnId)
        val status = view.findViewById<TextView>(R.id.tvStatus)
        val dueAmount = view.findViewById<TextView>(R.id.tvDueAmount)
        val dueDate = view.findViewById<TextView>(R.id.tvDueDate)
        val btnHistory = view.findViewById<AppCompatButton>(R.id.btnHistory)

        val dialogButton = view.findViewById<ImageView>(R.id.dialogButton)
        dialogButton.setOnClickListener {
            dialog?.dismiss()
        }

        date.text = Utils.simpleDateFormate(
            transDetail.transactionDate!!,
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "dd MMMM yyyy"
        )
        txnId.text = transDetail.transactionId
        status.text = transDetail.status
        dueAmount.text = transDetail.dueAmount.toString()
        dueDate.text = transDetail.dueDate

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvTransactionDetails)
        val adapter = TransactionDetailsAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnHistory.setOnClickListener {
            callHistoryApi(transDetail.transactionId)
        }

    }

    private fun callHistoryApi(
        transactionId: String?
    ) {
        udharStatementViewModel.getPaidTransactionHistory(transactionId!!)
    }

    private fun setToolBar(visibleFilterIcon: Boolean) {
        if (visibleFilterIcon) {
            activitySDk.ivDateFilterToolbar.visibility = View.VISIBLE
        } else {
            activitySDk.ivDateFilterToolbar.visibility = View.GONE
        }
        activitySDk.ivDateFilterToolbar.setOnClickListener {
            filterDialog()
        }
    }

    private fun setupTabLayout() {
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText("Outstanding Txn"), true)
        mBinding.tabs.addTab(mBinding.tabs.newTab().setText("Paid Txn"))
    }

    private fun bindWidgetsWithAnEvent() {
        mBinding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                setCurrentTabFragment(p0!!.position)
            }


        })
    }

    private fun setCurrentTabFragment(tabPosition: Int) {
        when (tabPosition) {
            0 -> replaceView(tabPosition)
            1 -> replaceView(tabPosition)
        }
    }

    private fun replaceView(flag: Int) {
        val accountId: Long = args.accountId
        if (flag == 0) {
            udharStatementViewModel.getUdharStatement(accountId, 1)
        } else {
            udharStatementViewModel.getUdharStatement(accountId, 2)
        }
    }

    private fun setRecyclerViewData(data: ArrayList<UdharStatementModel>) {
        mBinding.rvUdharStatement.layoutManager = LinearLayoutManager(activitySDk)
        adapter = UdharStatementAdapter(data) { itemId ->
            transactionDetailsApiCall(itemId)
        }
        mBinding.rvUdharStatement.adapter = adapter
        if (data.size > 0) {
            setToolBar(true)
            mBinding.rvUdharStatement.visibility = View.VISIBLE
            mBinding.tvDataNotFound.visibility = View.GONE
            mBinding.btnDownload.visibility = View.VISIBLE
        } else {
            setToolBar(false)
            mBinding.rvUdharStatement.visibility = View.GONE
            mBinding.tvDataNotFound.visibility = View.VISIBLE
            mBinding.btnDownload.visibility = View.GONE

        }
    }

    private fun transactionDetailsApiCall(itemId: UdharStatementModel) {
        transDetail = itemId
        udharStatementViewModel.getTransactionDetail(itemId.transactionId!!)
    }

    private fun filterDialog() {
        monthType = 4
        fromDate = ""
        toDate = ""
        filterBottomDialog = BottomSheetDialog(activitySDk, R.style.Theme_Design_BottomSheetDialog)
        mFilterBottomDialogBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.dialog_date_filter_apply, null, false)
        filterBottomDialog.setContentView(mFilterBottomDialogBinding.root)
        filterBottomDialog.show()

        mFilterBottomDialogBinding.imClose.setOnClickListener {
            filterBottomDialog.dismiss()
        }

        mFilterBottomDialogBinding.rbThisMonth.setOnClickListener {
            setRadioBtnUi(1, mFilterBottomDialogBinding)
        }

        mFilterBottomDialogBinding.rbLastMonth.setOnClickListener {
            setRadioBtnUi(2, mFilterBottomDialogBinding)
        }

        mFilterBottomDialogBinding.rbLastPreviewsMonth.setOnClickListener {
            setRadioBtnUi(3, mFilterBottomDialogBinding)
        }

        mFilterBottomDialogBinding.rbCustomeDate.setOnClickListener {
            setRadioBtnUi(4, mFilterBottomDialogBinding)
        }

        mFilterBottomDialogBinding.tvFormDate.setOnClickListener {
            showFormDatePicker()
        }

        mFilterBottomDialogBinding.tvToDate.setOnClickListener {
            showToDatePicker()
        }

        mFilterBottomDialogBinding.btnDownload.setOnClickListener {
            filterBottomDialog.dismiss()

            udharStatementViewModel.downloadReport(DownloadLedgerReportResquestModel().apply {
                AccountId = "60377"
                MonthType = monthType
                FromDate = fromDate
                ToDate = toDate
            })
        }
    }

    private fun setRadioBtnUi(
        monthTypeFlag: Int,
        mFilterBottomDialogBinding: DialogDateFilterApplyBinding
    ) {
        monthType = monthTypeFlag
        when (monthTypeFlag) {
            1 -> {
                mFilterBottomDialogBinding.rbThisMonth.isChecked = true
                mFilterBottomDialogBinding.rbLastMonth.isChecked = false
                mFilterBottomDialogBinding.rbLastPreviewsMonth.isChecked = false
                mFilterBottomDialogBinding.rbCustomeDate.isChecked = false
                mFilterBottomDialogBinding.llCustomDatePicker.visibility = View.GONE
            }

            2 -> {
                mFilterBottomDialogBinding.rbThisMonth.isChecked = false
                mFilterBottomDialogBinding.rbLastMonth.isChecked = true
                mFilterBottomDialogBinding.rbLastPreviewsMonth.isChecked = false
                mFilterBottomDialogBinding.rbCustomeDate.isChecked = false
                mFilterBottomDialogBinding.llCustomDatePicker.visibility = View.GONE
            }

            3 -> {
                mFilterBottomDialogBinding.rbThisMonth.isChecked = false
                mFilterBottomDialogBinding.rbLastMonth.isChecked = false
                mFilterBottomDialogBinding.rbLastPreviewsMonth.isChecked = true
                mFilterBottomDialogBinding.rbCustomeDate.isChecked = false
                mFilterBottomDialogBinding.llCustomDatePicker.visibility = View.GONE
            }

            else -> {
                mFilterBottomDialogBinding.rbThisMonth.isChecked = false
                mFilterBottomDialogBinding.rbLastMonth.isChecked = false
                mFilterBottomDialogBinding.rbLastPreviewsMonth.isChecked = false
                mFilterBottomDialogBinding.rbCustomeDate.isChecked = true
                mFilterBottomDialogBinding.llCustomDatePicker.visibility = View.VISIBLE
            }
        }

    }

    private fun showFormDatePicker() {
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activitySDk,
            { _, year1, monthOfYear, dayOfMonth ->
                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year1)
                fromDate =
                    Utils.simpleDateFormate(dat, "dd-M-yyyy", "dd-MM-yyyy")!! + "T00:00:00.000Z"
                mFilterBottomDialogBinding.tvFormDate.text =
                    Utils.simpleDateFormate(dat, "dd-M-yyyy", "dd-MM-yyyy")
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun showToDatePicker() {
        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activitySDk,
            { _, year1, monthOfYear, dayOfMonth ->
                val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year1)
                toDate =
                    Utils.simpleDateFormate(dat, "dd-M-yyyy", "dd-MM-yyyy")!! + "T00:00:00.000Z"
                mFilterBottomDialogBinding.tvToDate.text =
                    Utils.simpleDateFormate(dat, "dd-M-yyyy", "dd-MM-yyyy")
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }


    private suspend fun downloadPDFFileWithProgress(
        context: Context,
        url: String,
    ) {
        withContext(Dispatchers.IO) {
            try {

                val download = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val pdfUri = Uri.parse(url)
                val getPdf = DownloadManager.Request(pdfUri)
                getPdf.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                download.enqueue(getPdf)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}