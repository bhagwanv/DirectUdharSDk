package com.sk.directudhar.ui

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.gson.Gson
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.data.SequenceEnumClass
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.ui.mainhome.MainViewFactory
import com.sk.directudhar.ui.mainhome.MainViewModel
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import javax.inject.Inject


class MainActivitySDk :AppCompatActivity() {

    private lateinit var navController: NavController

    @Inject
    lateinit var mainViewModelFactory: MainViewFactory

    lateinit var mainViewModel: MainViewModel

    lateinit var mobilNumber: String

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
    }

    private fun setupToolbar() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        setSupportActionBar(findViewById(R.id.toolbarNew))

    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.inject(this)
        mainViewModel =
            ViewModelProvider(this, mainViewModelFactory)[MainViewModel::class.java]

        mainViewModel.callToken(Utils.CLIENT_CREDENTIALS, Utils.SECRETKEY, Utils.APIKYYE)
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
                        checkSequenceNo(initiateAccountModel.Data.SequenceNo)
                    }else{
                        this.toast(initiateAccountModel.Msg)
                    }
                }
            }
        }
    }

    private fun checkSequenceNo(sequenceNo: Int) {
        val sequence = SequenceEnumClass.from(sequenceNo)
        when(sequence){
            SequenceEnumClass.APPLY_LOAN -> applyLoanCall()
            SequenceEnumClass.ADHAR_CARD -> aadhaarCardCall()
            SequenceEnumClass.CIBIL_SCORE -> checkCibilScore()
            SequenceEnumClass.APPRAVAL_PENDING -> approvalPending()
            SequenceEnumClass.E_AGREEMENT -> agreement()
            SequenceEnumClass.E_MANDATE -> mandate()
            SequenceEnumClass.SUCCESS -> successCall()
            SequenceEnumClass.PAN_CARD -> panCardCall()
            SequenceEnumClass.MY_ACCOUNT -> myAccountCall()
            else -> {
                this.toast("Sequence Not Found")

            }
        }
    }

    private fun panCardCall() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.PanCardFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun successCall() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.SuccessFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun mandate() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.EMandateFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun agreement() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.EAgreementFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun approvalPending() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.ApprovalPendingFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun checkCibilScore() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.CibilScoreFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun aadhaarCardCall() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.AadhaarFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun myAccountCall() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.myAccountFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }

    private fun applyLoanCall() {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination =R.id.ApplyLoanFragment
        navController.graph = navGraph
        setupActionBarWithNavController(navController)
    }


}