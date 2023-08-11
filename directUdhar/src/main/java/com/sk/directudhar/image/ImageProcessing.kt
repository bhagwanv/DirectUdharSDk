package com.sk.directudhar.image
import android.content.Context
import android.graphics.Bitmap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ImageProcessing {
    companion object {
       suspend fun uploadMultipart(filePath: String, context: Context): MultipartBody.Part {
            val compressedImageFile = Compressor.compress(context, File(filePath)) {
                quality(90)
                format(Bitmap.CompressFormat.JPEG)
            }
            val requestFile: RequestBody =
                compressedImageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body: MultipartBody.Part =
                MultipartBody.Part.createFormData("file", compressedImageFile.name, requestFile)
            return body
        }
    }
}

