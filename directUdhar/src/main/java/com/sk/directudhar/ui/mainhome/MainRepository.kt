package com.sk.directudhar.ui.mainhome

import com.sk.directudhar.data.NetworkResult
import com.sk.directudhar.di.APIServices
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiServices: APIServices) {

    suspend fun getToken(password:String,secretkey:String,apikey:String)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getToken(password,secretkey,apikey)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getAccountInitiate(mobileNumber:String)  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.initiate(mobileNumber)
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

    suspend fun getPrivacyPolicy()  = flow {
        emit(NetworkResult.Loading(true))
        val response = apiServices.getPrivacyPolicy()
        emit(NetworkResult.Success(response))
    }.catch { e ->
        emit(NetworkResult.Failure(e.message ?: "Unknown Error"))
    }

}