package com.sk.directudhar.ui.myaccount

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sk.directudhar.R
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentMyAccountBinding
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.AppDialogClass
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.SharePrefs
import com.sk.directudhar.utils.Utils
import com.sk.directudhar.utils.Utils.Companion.toast
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class MyAccountFragment : Fragment() {
    @Inject
    lateinit var myAccountFactory: MyAccountFactory
    @Inject
    lateinit var dialog: AppDialogClass

    lateinit var activitySDk: MainActivitySDk
    private var mBinding: FragmentMyAccountBinding? = null
    private lateinit var myAccountViewModel: MyAccountViewModel
    private var leadMasterId: Long = 0
    private var accountId: Long = 0
    private var flag: Int = 0  //outstanding=1 and Paid =2 ,  Next Due =3

  //  private lateinit var viewModel : SelectedImageViewModel

   // private var _binding: FragmentSelectImageBinding? = null
    //private val binding get() = _binding!!


    private lateinit var uri : Uri
    private lateinit var galIntent: Intent
    private lateinit var cropIntent: Intent

    val REQUEST_CODE = 200

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk = context as MainActivitySDk
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mBinding == null) {
        mBinding = FragmentMyAccountBinding.inflate(inflater, container, false)
        initView()
        }
        return mBinding!!.root
    }

    private fun initView() {
        val component = DaggerApplicationComponent.builder().build()
        component.injectMyAccount(this)
        myAccountViewModel =
            ViewModelProvider(this, myAccountFactory)[MyAccountViewModel::class.java]

        leadMasterId =
            SharePrefs.getInstance(activitySDk)?.getInt(SharePrefs.LEAD_MASTERID)!!.toLong()

        setToolBar()
      //  setObserver()

        /*mBinding!!.liOutStanding.setOnClickListener {
            flag = 1
            mBinding!!.tvTxnDetailTitle.text = getString(R.string.total_outstanding_txn_details)
            myAccountViewModel.getUdharStatement(accountId, flag)
        }

        mBinding!!.liNextDue.setOnClickListener {
            flag = 3
            mBinding!!.tvTxnDetailTitle.text = getString(R.string.next_due_txn_details)
            myAccountViewModel.getUdharStatement(accountId, flag)
        }

        mBinding!!.btnUpgrade.setOnClickListener {
            myAccountViewModel.creditLimitRequest(leadMasterId)
        }

        mBinding!!.btnViewStatement.setOnClickListener {
            val action =
                MyAccountFragmentDirections.actionMyAccountFragmentToUdharStatementFragment(
                    accountId
                )
            findNavController().navigate(action)
        }

        myAccountViewModel.getLoanAccountDetail(leadMasterId)*/

        mBinding!!.btnTakePicAgain.setOnClickListener {
            selectImageDialog()
        }
        mBinding!!.btnAddPic.setOnClickListener {
            selectImageDialog()
        }
        mBinding!!.btnSendPic.setOnClickListener {
        }

    }

    private fun openGallery() {
        galIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(Intent.createChooser(galIntent,
            "Select Image From Gallery "),2)
    }

    private fun openCamera() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE)

    }

    private fun cropImages(uri:Uri){
        /**set crop image*/
        try {
            cropIntent = Intent("com.android.camera.action.CROP")
            cropIntent.setDataAndType(uri,"image/*")
            cropIntent.putExtra("crop",true)
            cropIntent.putExtra("outputX",180)
            cropIntent.putExtra("outputY",180)
            cropIntent.putExtra("aspectX",3)
            cropIntent.putExtra("aspectY",4)
            cropIntent.putExtra("scaleUpIfNeeded",true)
            cropIntent.putExtra("return-data",true)
            startActivityForResult(cropIntent,1)

        }catch (e: ActivityNotFoundException){
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2){
            if (data != null){
                uri = data.data!!
                cropImages(uri)
            }
        }
        else if (requestCode == 1){
            if (data != null){
                val bundle = data.extras
                if (bundle != null) {
                    val bitmap = bundle!!.getParcelable<Bitmap>("data")
                    mBinding!!.selectedImage.setImageBitmap(bitmap)
                } else {
                    val uri = data.data!!
                    mBinding!!.selectedImage.setImageURI(uri)
                }

            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && data != null){
            mBinding!!.selectedImage.setImageBitmap(data.extras!!.get("data") as Bitmap)
            getImageUri(requireContext(),data.extras!!.get("data") as Bitmap)?.let {
                cropImages(it)
            }

        }
    }

   /* private fun setObserver() {
        myAccountViewModel.myAccountDetailsModelResponse.observe(viewLifecycleOwner) {
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
                    setUI(it.data)
                }
            }
        }
        myAccountViewModel.getUdharStatementResponse.observe(viewLifecycleOwner) {
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
                    if (it.data.size > 0) {
                        initRecyclerViewAdapter(it.data)
                    } else {
                        activitySDk.toast("Data Not Found")
                    }
                }
            }
        }
        myAccountViewModel.creditLimitRequestResponse.observe(viewLifecycleOwner) {
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
                    val `object` = JSONObject(it.data.toString())

                    val msg = `object`.getString("Msg")
                    dialog.accountCreatedDialog(activitySDk, msg, "OK")
                    dialog.setOnContinueCancelClick(object : AppDialogClass.OnContinueClicked {
                        override fun onContinueClicked() {

                        }
                    })
                }
            }
        }
    }*/

    private fun setToolBar() {
        activitySDk.ivDateFilterToolbar.visibility = View.GONE
    }

  /*  private fun initRecyclerViewAdapter(data: ArrayList<UdharStatementModel>) {
        mBinding!!.liTxnDueDetails.visibility = View.VISIBLE
        mBinding!!.rvTxnDetails.layoutManager = LinearLayoutManager(activitySDk)
        val adapter = TxnDetailsAdapter(data)
        mBinding!!.rvTxnDetails.adapter = adapter
    }

    private fun setUI(data: MyAccountDetailsModel) {
        if (data.accountId != null) {
            accountId = data.accountId!!.toLong()
        }
        if (data.accountNo != null) {
            mBinding!!.tvLoanAccountNumber.text = data.accountNo.toString()
        }
        if (data.totalUdharLimit != null) {
            mBinding!!.tvTotalUdhaLimit.text = data.totalUdharLimit.toString()
        }
        if (data.availableUdharLimit != null) {
            mBinding!!.tvAvailableUdharLimit.text = data.availableUdharLimit.toString()
        }
        if (data.outstanding != null) {
            mBinding!!.tvTotalOutStanding.text = data.outstanding.toString()
        }
        if (data.nextDueAmount != null) {
            if (data.nextDueDate != null) {
                val dueDate = Utils.simpleDateFormate(data.nextDueDate!!,"yyyy-MM-dd'T'HH:mm:ss.SSS","dd MMMM yyyy" )
                val textToShow = "$dueDate \n${data.nextDueAmount}"
                mBinding!!.tvNextDueOn.text = textToShow
            } else {
                mBinding!!.tvNextDueOn.text = data.nextDueAmount.toString()
            }
        }
    }
*/
    override fun onDestroy() {
        super.onDestroy()
        mBinding!!.unbind()
    }

    companion object{
        const val RequestPermissionCode = 111
    }

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext.getContentResolver(),
            inImage,
            "Title",
            null
        )
        return Uri.parse(path)
    }

    private fun selectImageDialog(){
        val customDialog = Dialog(requireActivity())
        customDialog.setContentView(R.layout.custom_dialog)
        customDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val btnGallery = customDialog.findViewById(R.id.btnGallery) as TextView
        val btnCamera = customDialog.findViewById(R.id.btnCamera) as TextView
        btnGallery.setOnClickListener {
            openGallery()
            customDialog.dismiss()
        }
        btnCamera.setOnClickListener {
            openCamera()
            customDialog.dismiss()
        }
        customDialog.show()
    }
}