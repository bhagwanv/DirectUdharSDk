package com.sk.directudhar.ui.adharcard.aadhaarCardOtp

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AadhaarOtpRepository @Inject constructor(private val apiServices: APIServices) {
    suspend fun aadharVerification(aadharVerificationRequestModel: AadharVerificationRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.aadharVerification(aadharVerificationRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}