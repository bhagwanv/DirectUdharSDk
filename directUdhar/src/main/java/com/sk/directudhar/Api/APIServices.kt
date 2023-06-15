package com.sk.directudhar.Api

import com.google.gson.JsonObject
import com.sk.directudhar.model.TokenResponse
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface APIServices {

    @FormUrlEncoded
    @POST("/token")
    fun getToken(
        @Field("grant_type") grant_type: String?,
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Observable<TokenResponse?>?

    @GET
    fun generateLead(@Url url: String?): Observable<JsonObject?>?
}