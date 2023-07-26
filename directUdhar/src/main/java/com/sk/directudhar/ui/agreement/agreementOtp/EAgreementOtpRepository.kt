package com.sk.directudhar.ui.agreement.agreementOtp

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EAgreementOtpRepository @Inject constructor(private val apiServices: APIServices) {
    suspend fun eAgreementOtpVerification(model: EAgreementOtpResquestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.eAgreementOtpVerification(model)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun sendOtp(mobileNo: String)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.sendOtp(mobileNo)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}