package com.sk.directudhar.di

import com.google.gson.JsonObject
import com.sk.directudhar.data.TokenResponse
import com.sk.directudhar.ui.adharcard.AadhaarUpdateResponseModel
import com.sk.directudhar.ui.adharcard.UpdateAadhaarInfoRequestModel
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadharVerificationRequestModel
import com.sk.directudhar.ui.applyloan.ApplyLoanRequestModel
import okhttp3.MultipartBody
import com.sk.directudhar.ui.applyloan.CityModel
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.approvalpending.DisplayDisbursalAmountResponse
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.ui.mandate.BankListResponse
import com.sk.directudhar.ui.mandate.EMandateAddRequestModel
import com.sk.directudhar.ui.mandate.EMandateAddResponseModel
import com.sk.directudhar.ui.myaccount.MyAccountDetailsModel
import com.sk.directudhar.ui.myaccount.UdharStatementModel
import com.sk.directudhar.ui.pancard.UpdatePanInfoRequestModel
import com.sk.directudhar.ui.pancard.UpdatePanInfoResponseModel
import com.sk.directudhar.ui.success.SuccessDisplayDisbursalAmountResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface APIServices {

    @FormUrlEncoded
    @POST("/token")
    suspend fun getToken(
        @Field("grant_type") grant_type: String?,
        @Field("client_Id") username: String?,
        @Field("client_secret") password: String?
    ): TokenResponse

    @GET("api/Borrower/Initiate?")
    suspend fun initiate(@Query("MobileNo") MobileNo: String): JsonObject

    @GET("api/StateMaster")
    suspend fun stateMaster(): ArrayList<StateModel>

    @GET("api/CityMaster/GetCityByStateId")
    suspend fun stateMaster(@Query("StateId") StateId: Int): ArrayList<CityModel>


    @POST("api/Borrower/addlead")
    suspend fun postData(@Body applyLoanRequestModel: ApplyLoanRequestModel): InitiateAccountModel

    @Multipart
    @POST("api/Borrower/PanImageUpload")
    suspend fun uploadPanCard(
        @Query("LeadMasterId") LeadMasterId: Int,
        @Part body: MultipartBody.Part
    ): JsonObject

    @POST("api/Borrower/UpdatePanInfo")
    suspend fun updatePanInfo(@Body updatePanInfoRequestModel: UpdatePanInfoRequestModel): UpdatePanInfoResponseModel

    @GET("api/eMandate/BankList")
    suspend fun bankList(): BankListResponse

    @POST("api/eMandate/Add")
    suspend fun setUpEMandateAdd(@Body eMandateAddRequestModel: EMandateAddRequestModel): EMandateAddResponseModel

    @POST("api/Borrower/UpdateAdhaarInfo")
    suspend fun updateAadhaarInfo(@Body updateAadhaarInfoRequestModel: UpdateAadhaarInfoRequestModel): AadhaarUpdateResponseModel

    @POST("api/Borrower/AadharVerification")
    suspend fun aadharVerification(@Body aadharVerificationRequestModel: AadharVerificationRequestModel): AadhaarUpdateResponseModel

    @GET("api/Application/DisplayDisbursalAmount")
    suspend fun displayDisbursalAmount(@Query("LeadMasterId") LeadMasterId: Int): DisplayDisbursalAmountResponse

    @GET("api/borrower/UpdateLeadSuccess")
    suspend fun updateLeadSuccess(@Query("LeadMasterId") LeadMasterId: Int): InitiateAccountModel

    @GET("api/Application/DisplayDisbursalAmount")
    suspend fun successDisplayDisbursalAmount(@Query("LeadMasterId") LeadMasterId: Int): SuccessDisplayDisbursalAmountResponse

    @GET("api/borrower/UpdateLeadSuccess")
    suspend fun successUpdateLeadSuccess(@Query("LeadMasterId") LeadMasterId: Int): InitiateAccountModel

    @GET("api/borrower/GetLoanAccountDetail")
    suspend fun getLoanAccountDetail(@Query("LeadId") leadMasterId: Int): MyAccountDetailsModel

    @GET("api/AccountTransaction/GetUdharStatement")
    suspend fun getUdharStatement(
        @Query("AccountId") accountId: Int,
        @Query("flag") flag: Int
    ): ArrayList<UdharStatementModel>

}