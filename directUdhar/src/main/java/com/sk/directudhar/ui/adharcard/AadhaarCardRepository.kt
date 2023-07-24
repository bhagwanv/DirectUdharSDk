package com.sk.directudhar.ui.adharcard

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AadhaarCardRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun updateAadhaarInfo(updateAadhaarInfoRequestModel: UpdateAadhaarInfoRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.updateAadhaarInfo(updateAadhaarInfoRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

}