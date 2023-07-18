package com.sk.directudhar.di

import com.google.gson.JsonObject
import com.sk.directudhar.data.TokenResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface APIServices {

    @FormUrlEncoded
    @POST("/token")
   suspend fun getToken(
        @Field("grant_type") grant_type: String?,
        @Field("client_Id") username: String?,
        @Field("client_secret") password: String?
    ): TokenResponse

    @GET("api/Borrower/Initiate?")
    suspend  fun initiate(@Query("MobileNo") MobileNo: String): JsonObject

 /*   @GET
   suspend fun generateLead(@Url url: String?): Observable<JsonObject?>?

    */

}