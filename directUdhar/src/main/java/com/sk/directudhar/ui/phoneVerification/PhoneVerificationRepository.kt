package com.sk.directudhar.ui.phoneVerification

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PhoneVerificationRepository @Inject constructor(private val apiServices: APIServices) {
    suspend fun getOtp(mobile: String) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getOtp(mobile)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getOtpVerify(mobile: String,otp: String) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getOtpVerify(mobile,otp)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}