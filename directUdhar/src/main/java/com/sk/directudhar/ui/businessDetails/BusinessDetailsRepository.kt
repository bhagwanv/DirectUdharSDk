package com.sk.directudhar.ui.businessDetails

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject


class BusinessDetailsRepository @Inject constructor(private val apiServices: APIServices) {

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

    suspend fun bankPassBookUpload(LeadMasterId: Int,body: MultipartBody.Part) = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.bankPassBookUpload(LeadMasterId,body)
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

    suspend fun uploadBillManual(body: MultipartBody.Part,leadMasterId: Int)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.uploadBillManual(body,leadMasterId)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }
}