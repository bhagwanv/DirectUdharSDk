package com.sk.directudhar.ui.cibilscore

import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import com.sk.directudhar.ui.applyloan.ApplyLoanRequestModel
import com.sk.directudhar.ui.cibilscore.cibiotp.GenrateOtpModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CibilRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun getState()  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.stateMaster()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getCity(statId: Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.stateMaster(statId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getUserInfo(leadMasterID: Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getUserCreditInfo(leadMasterID)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun postData(cibilRequestModel: CibilRequestModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.PostCreditScore(cibilRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun genrateData(genrateOtpModel: GenrateOtpModel)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.OTPGeneratRequest(genrateOtpModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }


}