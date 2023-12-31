package com.sk.directudhar.ui.personalDetails

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ApplayLoanRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun getState() = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.stateMaster()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getCity(statId: Int) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.stateMaster(statId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun postData(applyLoanRequestModel: ApplyLoanRequestModel) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.postData(applyLoanRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getPersonalInformation(leadMasterId: Int) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getPersonalInformation(leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun postCreditBeurau(postCreditBeurauRequestModel: PostCreditBeurauRequestModel) =
        flow {
            emit(NetworkResult.Loading(true))
            val response = apiServices.postCreditBeurau(postCreditBeurauRequestModel)
            emit(NetworkResult.Success(response))
        }.catch { e ->
            emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
        }
}