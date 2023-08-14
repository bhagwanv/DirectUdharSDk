package com.sk.directudhar.ui.applyloan

import androidx.lifecycle.ViewModelProvider
import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
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


    suspend fun addBusinessDetail(businessDetailsRequestModel: BusinessDetailsRequestModel) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.addBusinessDetail(businessDetailsRequestModel)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getGSTDetails(GSTNo: String) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getGSTDetails(GSTNo)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getBusinessTypeList() = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getBusinessTypeList()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun electricityDocumentUpload(LeadMasterId: Int,body: MultipartBody.Part) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.electricityDocumentUpload(LeadMasterId,body)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun verifyElectricityBill(model: BusinessDetailsVerifyElectricityBillRequestModel) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.verifyElectricityBill(model)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}