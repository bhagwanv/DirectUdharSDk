package com.sk.directudhar.image

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.sk.directudhar.R
import com.sk.directudhar.databinding.ActivityImageCaptureBinding
import com.sk.directudhar.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class ImageCaptureActivity : AppCompatActivity() {
    private val WRITE_PERMISSION = 0x01
    private var fileName: String = ""
    private var isGalleryOption: Boolean = false
    private lateinit var mBinding: ActivityImageCaptureBinding
    private val GALLERY_REQUST = 200
    private val CAPTURE_IMAGE_CAMERA = 100
    private var IMAGE_FROM = 1
    private var filePath = ""
    private var resultLauncher: ActivityResultLauncher<Intent>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_capture)
        if (intent.extras != null) {
            fileName = intent.getStringExtra(Utils.FILE_NAME).toString()
            isGalleryOption = intent.getBooleanExtra(Utils.IS_GALLERY_OPTION, false)
            if (isGalleryOption) {
                mBinding.llGallery.visibility = View.VISIBLE
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_PERMISSION
                )
            } else {
                mBinding.cartViewImgaeChoose.visibility = View.VISIBLE
            }
        } else {
            mBinding.cartViewImgaeChoose.visibility = View.VISIBLE
        }

        mBinding.llCamera.setOnClickListener {
            IMAGE_FROM = CAPTURE_IMAGE_CAMERA
            pickFromCamera()
        }
        mBinding.llGallery.setOnClickListener {
            IMAGE_FROM = GALLERY_REQUST
            resultLauncher?.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )

        }
        mBinding.llCancel.setOnClickListener {
            val returnIntent = Intent()
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }
        resultHandle()
    }

    private fun resultHandle() {
        //Result for upload photo
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (IMAGE_FROM == CAPTURE_IMAGE_CAMERA && result.resultCode == RESULT_OK) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(Utils.FILE_PATH, filePath)
                    setResult(RESULT_OK, returnIntent)
                    finish()
                } else if (IMAGE_FROM == GALLERY_REQUST && result.resultCode == RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val selectedImageUri = data?.data
                    val path = getPathFromURI(selectedImageUri!!)
                    val returnIntent = Intent()
                    returnIntent.putExtra(Utils.FILE_PATH, path)
                    setResult(RESULT_OK, returnIntent)
                    finish()
                } else if (result.resultCode == RESULT_CANCELED) {
                    setResult(RESULT_CANCELED, Intent())
                    finish()
                }
            }
    }

    private fun getPathFromURI(uri: Uri): String {
        var realPath = String()
        uri.path?.let { _path ->
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            contentResolver?.query(
                uri,
                projection,
                null,
                null,
                null
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                cursor.moveToFirst()
                val path = cursor.getString(idColumn)
                val bm: Bitmap
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(path, options)
                val REQUIRED_SIZE = 200
                var scale = 1
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE && options.outHeight / scale / 2 >= REQUIRED_SIZE) scale *= 2
                options.inSampleSize = scale
                options.inJustDecodeBounds = false
                bm = BitmapFactory.decodeFile(path, options)
                realPath = saveImages(bm)
            }
        }
        return realPath
    }

    private fun saveImages(bm: Bitmap): String {
        val root = Environment.getExternalStorageDirectory().toString()
        var imageUri: Uri? = null
        val fos: OutputStream
        val directory = Environment.DIRECTORY_PICTURES
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            try {
                val resolver = contentResolver
                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image")
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directory)
                imageUri =
                    resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = resolver?.openOutputStream(imageUri!!)!!
                bm.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            Log.d(
                ContentValues.TAG,
                "SavedImages:" + root + File.separator + directory + File.separator + fileName
            )
            root + File.separator + directory + File.separator + fileName
        } else {
            val file: File
            val folderPath = root + "/ShopKirana"
            val folder = File(folderPath)
            if (!folder.exists()) {
                folder.mkdirs()
                file = File(folder, fileName)
            } else {
                file = File(folder, fileName)
                if (file.exists()) file.delete()
            }
            try {
                val out = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                //  val  uploadFilePath = "$root/ShopKirana/$fileName"
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            "$root/ShopKirana/$fileName"
        }
    }

    private fun pickFromCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    return
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        applicationContext,
                        packageName + ".provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    }
                    resultLauncher?.launch(takePictureIntent)
                }
            }
        }
        /*  val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
          if (pictureIntent.resolveActivity(packageManager) != null) {
              val photoFile: File
              try {
                  photoFile = createImageFile()
              } catch (e: IOException) {
                  e.printStackTrace()
                  return
              }
              val photoUri = FileProvider.getUriForFile(
                  applicationContext,
                  BuildConfig.APPLICATION_ID + ".provider",
                  photoFile
              )
              pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                  pictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
              }
              resultLauncher?.launch(pictureIntent)
          }*/
    }

    private fun createImageFile(): File {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val folderPath = getExternalFilesDir(null)!!.absolutePath + "/ShopKirana"
        val folder = File(folderPath)
        if (!folder.exists()) {
            val file = File(folderPath)
            file.mkdirs()
        }
        val fileNew = File(storageDir, fileName)
        filePath = fileNew.absolutePath
        return fileNew
    }

}