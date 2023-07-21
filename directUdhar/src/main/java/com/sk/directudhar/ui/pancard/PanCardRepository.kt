package com.sk.directudhar.ui.pancard

import android.util.Log
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class PanCardRepository @Inject constructor(private val apiServices: APIServices) {


    suspend fun uploadPanCard(leadMasterId:Int,body: MultipartBody.Part)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.uploadPanCard(leadMasterId,body)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun updatePanInfo(model: UpdatePanInfoRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.updatePanInfo(model)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}