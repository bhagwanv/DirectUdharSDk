package com.sk.directudhar.ui.adharcard.aadhaarCardOtp

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import com.sk.directudhar.ui.adharcard.UpdateAadhaarInfoRequestModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject

class AadhaarOtpRepository @Inject constructor(private val apiServices: APIServices) {
    suspend fun aadharVerification(aadharVerificationRequestModel: AadharVerificationRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.aadharVerification(aadharVerificationRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun uploadAadhaarImage(body: MultipartBody.Part,leadMasterId: Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.uploadAadhaarImage(body,leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
    suspend fun updateAadhaarInfo(updateAadhaarInfoRequestModel: UpdateAadhaarInfoRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.updateAadhaarInfo(updateAadhaarInfoRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

}