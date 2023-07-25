package com.sk.directudhar.ui.cibilscore.cibiotp

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import com.sk.directudhar.ui.applyloan.ApplyLoanRequestModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CiBilOtpRespository @Inject constructor(val apiServices: APIServices) {


    suspend fun postOtpData(postOTPRequestModel: PostOTPRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.OTPPostOTPRequest(postOTPRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}