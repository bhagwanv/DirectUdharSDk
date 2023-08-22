package com.sk.directudhar.ui.cibilGenerate

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CibilGenerateRepository @Inject constructor(private val apiServices: APIServices) {
    suspend fun getOtp(leadMasterId: Int) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.cibilOTPGenerate(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getOtpVerify(cibilOTPVerifyRequestModel : CibilOTPVerifyRequestModel) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getCibilOtpVerify(cibilOTPVerifyRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}