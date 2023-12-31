package com.sk.directudhar.ui.agreement

import com.google.gson.JsonObject
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EAgreementRepository @Inject constructor(private val apiServices: APIServices) {


    suspend fun getAgreement(leadMasterId:Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getAgreement(leadMasterId)
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

    suspend fun eSignSessionAsync(signSessionRequestModel: SignSessionRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.eSignSessionAsync(signSessionRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun isEsignOrAgreementWithOtp(leadMasterId:Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.isEsignOrAgreementWithOtp(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun eSignDocumentsAsync(jsonObject:JsonObject)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.eSignDocumentsAsync(jsonObject)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}