package com.sk.directudhar.ui.pancard

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class PanCardRepository @Inject constructor(private val apiServices: APIServices) {


    suspend fun uploadPanCard(body: MultipartBody.Part)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.uploadPanCard(body)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}