package com.sk.directudhar.ui.mainhome

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.data.SequenceEnumClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject


class MainActivitySDk : AppCompatActivity() {

    private lateinit var navController: NavController

    @Inject
    lateinit var mainViewModelFactory: MainViewFactory

    lateinit var mainViewModel: MainViewModel
    lateinit var mobilNumber: String
    lateinit var navHostFragment: NavHostFragment
    lateinit var toolbar: Toolbar
    lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apply_loan)
        setupToolbar()
        getIntentValueFromCompanyApp()
        initView()
    }

    private fun getIntentValueFromCompanyApp() {
        val intent = intent
        mobilNumber = intent.getStringExtra("mobileNumber")!!
        SharePrefs.getInstance(this)?.putString(
            SharePrefs.MOBILE_NUMBER,
            mobilNumber
        )
    }

    private fun setupToolbar() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        toolbar = findViewById(R.id.toolbar)
        toolbarTitle = findViewById(R.id.toolbarTitle)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.inject(this)
        mainViewModel = ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]
        this.onBackPressedDispatcher.addCallback(this, callback)
        mainViewModel.callToken(Utils.CLIENT_CREDENTIALS, Utils.APIKYYE, Utils.SECRETKEY)
        setObserber()
    }

    private fun setObserber() {
        mainViewModel.tokenResponse.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(this)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()

                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    SharePrefs.getInstance(this)
                        ?.putString(SharePrefs.TOKEN, it.data.access_token)
                    mainViewModel.getAccountInitiateResponse(mobilNumber)
                }
            }
        }
        mainViewModel.accountInitiateResponse.observe(this) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(this)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    Toast.makeText(this, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    val initiateAccountModel =
                        Gson().fromJson(it.data, InitiateAccountModel::class.java)
                    if (initiateAccountModel.Result) {
                        SharePrefs.getInstance(this)?.putInt(
                            SharePrefs.SEQUENCENO,
                            initiateAccountModel.Data.SequenceNo
                        )
                        SharePrefs.getInstance(this)?.putInt(
                            SharePrefs.LEAD_MASTERID,
                            initiateAccountModel.Data.LeadMasterId
                        )
                      //  checkSequenceNo(initiateAccountModel.Data.SequenceNo)
                        checkSequenceNo(2)
                    } else {
                        this.toast(initiateAccountModel.Msg)
                    }
                }
            }
        }
    }

    fun checkSequenceNo(sequenceNo: Int) {
        when (SequenceEnumClass.from(sequenceNo)) {
            SequenceEnumClass.PHONE_VERIFICATION -> navigateViewCall(R.id.phoneVerificationFragment)
            SequenceEnumClass.BUSINESS_DETAILS -> navigateViewCall(R.id.businessDetailsFragment)
            SequenceEnumClass.APPLY_LOAN -> navigateViewCall(R.id.ApplyLoanFragment)
            SequenceEnumClass.ADHAR_CARD -> navigateViewCall(R.id.AadhaarFragment)
            SequenceEnumClass.CIBIL_SCORE -> navigateViewCall(R.id.CibilScoreFragment)
            SequenceEnumClass.APPRAVAL_PENDING -> navigateViewCall(R.id.ApprovalPendingFragment)
            SequenceEnumClass.E_AGREEMENT -> navigateViewCall(R.id.EAgreementFragment)
            SequenceEnumClass.E_MANDATE -> navigateViewCall(R.id.EMandateFragment)
            SequenceEnumClass.SUCCESS -> navigateViewCall(R.id.SuccessFragment)
            SequenceEnumClass.PAN_CARD -> navigateViewCall(R.id.PanCardFragment)
            SequenceEnumClass.MY_ACCOUNT -> navigateViewCall(R.id.myAccountFragment)
            SequenceEnumClass.CIBIL_GENERATE -> navigateViewCall(R.id.cibilGenerateFragment)
            else -> {
                this.toast("Sequence Not Found")
            }
        }
    }

    private fun navigateViewCall(fragment: Int) {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination = fragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    val callback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.graph.startDestination == navController.currentDestination?.id) {
                    finish()
                } else {
                    navController.popBackStack()
                }
            }
        }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}