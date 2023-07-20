package com.sk.directudhar.ui.pancard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.databinding.FragmentPanCardBinding
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.ui.mainhome.MainActivitySDk
import com.sk.directudhar.utils.DaggerApplicationComponent
import com.sk.directudhar.utils.ProgressDialog
import com.sk.directudhar.utils.Utils.Companion.toast
import java.io.File
import java.io.FileOutputStream
import java.util.Random
import javax.inject.Inject

class PanCardFragment:Fragment() {

    lateinit var activitySDk: MainActivitySDk

    private lateinit var mBinding:FragmentPanCardBinding

    lateinit var panCardViewModel: PanCardViewModel
    @Inject
    lateinit var panCardFactory: PanCardFactory
    private val cameraRequest = 1888

    var pickImage:Boolean=false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activitySDk= context as MainActivitySDk

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPanCardBinding.inflate(inflater, container, false)
        initView()
        return mBinding.root
    }

    private fun initView() {

        val component = DaggerApplicationComponent.builder().build()
        component.injectPanCard(this)
        panCardViewModel = ViewModelProvider(this, panCardFactory)[PanCardViewModel::class.java]

       // var model= PanCardRequestModel("",0)

        mBinding.ivCameraImage.setOnClickListener {

            if (ContextCompat.checkSelfPermission(activitySDk, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(activitySDk, arrayOf(Manifest.permission.CAMERA), cameraRequest)
               // openSomeActivityForResult()
            imageChooser()
        }



    //    panCardViewModel.getPanCard("9522392801")
        panCardViewModel.panCardResponse.observe(activitySDk) {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressDialog.instance!!.show(activitySDk)
                }

                is NetworkResult.Failure -> {
                    ProgressDialog.instance!!.dismiss()
                    Toast.makeText(activitySDk, it.errorMessage, Toast.LENGTH_SHORT).show()
                }

                is NetworkResult.Success -> {
                    ProgressDialog.instance!!.dismiss()
                    val initiateAccountModel =
                        Gson().fromJson(it.data, InitiateAccountModel::class.java)
                    if (initiateAccountModel.Result) {
                        Log.e("TAG", "PanCardFragment Call: ", )

                    } else {
                        activitySDk.toast(initiateAccountModel.Msg)
                    }
                }
            }
        }
    }

    fun openSomeActivityForResult() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (pickImage){
                var FILE_PATH = "filePath"
                val filePath =  data?.getStringExtra(FILE_PATH).toString()
                val imageUri: Uri? = data!!.data;
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(activitySDk.getContentResolver(), Uri.parse(imageUri.toString()))
                mBinding.ivPanCardFrontImage.setImageBitmap(bitmap)
                pickImage=false
            }else{
                val photo: Bitmap = data?.extras?.get("data") as Bitmap
                mBinding.ivPanCardFrontImage.setImageBitmap(photo)
                SaveImage(photo)
            }

        }
    }

    private fun SaveImage(showedImgae: Bitmap) {
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/DCIM/myCapturedImages")
        Log.e("Mydirectory", "$root/DCIM/myCapturedImages: ")
        myDir.mkdirs()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val fname = "FILENAME-$n.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            showedImgae.compress(Bitmap.CompressFormat.JPEG, 100, out)
            Toast.makeText(activitySDk, "Image Saved", Toast.LENGTH_SHORT).show()
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun imageChooser() {
        pickImage=true
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(i)

    }

}