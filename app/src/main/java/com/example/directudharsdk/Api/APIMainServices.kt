package com.example.directudharsdk.Api

import com.google.gson.JsonObject
import com.sk.directudhar.model.TokenResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface APIMainServices {

    @FormUrlEncoded
    @POST("/token")
    fun getTokenClient(
        @Field("grant_type") grant_type: String?,
        @Field("client_Id") username: String?,
        @Field("client_secret") password: String?
    ): Observable<TokenResponse?>?

    @GET
    fun generateLead(@Url url: String?): Observable<JsonObject?>?

}