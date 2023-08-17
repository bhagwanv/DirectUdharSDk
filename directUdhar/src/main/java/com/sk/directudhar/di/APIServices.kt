package com.sk.directudhar.di

import com.google.gson.JsonObject
import com.sk.directudhar.data.TokenResponse
import com.sk.directudhar.ui.adharcard.AadhaarUpdateResponseModel
import com.sk.directudhar.ui.adharcard.UpdateAadhaarInfoRequestModel
import com.sk.directudhar.ui.adharcard.aadhaarCardOtp.AadharVerificationRequestModel
import com.sk.directudhar.ui.adharcard.aadhaarManullyUpload.AadhaarManuallyUploadResponseModel
import com.sk.directudhar.ui.agreement.AgreementResponseModel
import com.sk.directudhar.ui.agreement.SendOtpResponseModel
import com.sk.directudhar.ui.agreement.agreementOtp.EAgreementOtpResquestModel
import com.sk.directudhar.ui.applyloan.ApplyLoanRequestModel
import com.sk.directudhar.ui.businessDetails.BusinessDetailsVerifyElectricityBillRequestModel
import okhttp3.MultipartBody
import com.sk.directudhar.ui.applyloan.CityModel
import com.sk.directudhar.ui.applyloan.GetPersonalInformationResponseModel
import com.sk.directudhar.ui.applyloan.PostCreditBeurauRequestModel
import com.sk.directudhar.ui.applyloan.PostCreditBeurauResponseModel
import com.sk.directudhar.ui.applyloan.StateModel
import com.sk.directudhar.ui.businessDetails.BusinessDetailsVerifyElectricityBillResponseModel
import com.sk.directudhar.ui.cibilscore.CibilResponseModel
import com.sk.directudhar.ui.cibilscore.cibiotp.CiBilResponceModel
import com.sk.directudhar.ui.cibilscore.cibiotp.GenrateOtpModel
import com.sk.directudhar.ui.approvalpending.DisplayDisbursalAmountResponse
import com.sk.directudhar.ui.businessDetails.BusinessDetailsRequestModel
import com.sk.directudhar.ui.businessDetails.BusinessDetailsResponseModel
import com.sk.directudhar.ui.businessDetails.BusinessTypeListResponse
import com.sk.directudhar.ui.businessDetails.GSTDetailsResponse
import com.sk.directudhar.ui.businessDetails.StatementFileResponse
import com.sk.directudhar.ui.cibilscore.cibiotp.CiBilOTPResponceModel
import com.sk.directudhar.ui.cibilscore.cibiotp.PostOTPRequestModel
import com.sk.directudhar.ui.mainhome.InitiateAccountModel
import com.sk.directudhar.ui.mandate.BankListResponse
import com.sk.directudhar.ui.mandate.EMandateAddRequestModel
import com.sk.directudhar.ui.mandate.EMandateAddResponseModel
import com.sk.directudhar.ui.myaccount.MyAccountDetailsModel
import com.sk.directudhar.ui.myaccount.UdharStatementModel
import com.sk.directudhar.ui.myaccount.udharStatement.DownloadLedgerReportResquestModel
import com.sk.directudhar.ui.myaccount.udharStatement.HistoryResponseModel
import com.sk.directudhar.ui.myaccount.udharStatement.LedgerReportResponseModel
import com.sk.directudhar.ui.myaccount.udharStatement.TransactionDetailResponseModel
import com.sk.directudhar.ui.pancard.UpdatePanInfoRequestModel
import com.sk.directudhar.ui.phoneVerification.OtpVerifyResponseModel
import com.sk.directudhar.ui.phoneVerification.PhoneVerifyResponseModel
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
    suspend fun updatePanInfo(@Body updatePanInfoRequestModel: UpdatePanInfoRequestModel): InitiateAccountModel


    @GET("api/eMandate/BankList")
    suspend fun bankList(): BankListResponse

    @POST("api/ICICIeMandate/EMandateRegistration")
    suspend fun setUpEMandateAdd(@Body eMandateAddRequestModel: EMandateAddRequestModel): EMandateAddResponseModel

    @POST("api/Borrower/UpdateAdhaarInfo")
    suspend fun updateAadhaarInfo(@Body updateAadhaarInfoRequestModel: UpdateAadhaarInfoRequestModel): AadhaarUpdateResponseModel

    @POST("api/Borrower/AadharVerification")
    suspend fun aadharVerification(@Body aadharVerificationRequestModel: AadharVerificationRequestModel): InitiateAccountModel

    @POST("api/Borrower/PostCreditBeurau")
    suspend fun PostCreditScore(@Body cibilRequestModel: CibilResponseModel): CiBilResponceModel

    @POST("api/Borrower/OTPGeneratRequest")
    suspend fun OTPGeneratRequest(@Body genrateOtpModel: GenrateOtpModel): CiBilOTPResponceModel

    @GET("api/Application/DisplayDisbursalAmount")
    suspend fun displayDisbursalAmount(@Query("LeadMasterId") LeadMasterId: Int): DisplayDisbursalAmountResponse

    @GET("api/borrower/UpdateLeadSuccess")
    suspend fun updateLeadSuccess(@Query("LeadMasterId") LeadMasterId: Int): InitiateAccountModel

    @GET("api/Application/DisplayDisbursalAmount")
    suspend fun successDisplayDisbursalAmount(@Query("LeadMasterId") LeadMasterId: Int): SuccessDisplayDisbursalAmountResponse

    @GET("api/borrower/UpdateLeadSuccess")
    suspend fun successUpdateLeadSuccess(@Query("LeadMasterId") LeadMasterId: Int): InitiateAccountModel

    @GET("api/borrower/GetLoanAccountDetail")
    suspend fun getLoanAccountDetail(@Query("LeadId") leadMasterId: Long): MyAccountDetailsModel

    @GET("api/AccountTransaction/GetUdharStatement")
    suspend fun getUdharStatement(
        @Query("AccountId") accountId: Long,
        @Query("flag") flag: Int
    ): ArrayList<UdharStatementModel>

    @POST("api/Borrower/OTPValidationRequest")
    suspend fun OTPPostOTPRequest(@Body postOTPRequestModel: PostOTPRequestModel): InitiateAccountModel

    @GET("api/borrower/GetAgreement")
    suspend fun getAgreement(@Query("LeadMasterId") leadMasterId: Int): AgreementResponseModel

    @GET("api/borrower/SendOtp")
    suspend fun sendOtp(@Query("MobileNo") mobileNo: String): SendOtpResponseModel

    @POST("api/borrower/eAgreement")
    suspend fun eAgreementOtpVerification(@Body eAgreementOtpResquestModel: EAgreementOtpResquestModel): AgreementResponseModel
    @GET("api/Borrower/CreditLimitRequest")
    suspend fun creditLimitRequest(
        @Query("LeadMasterId") leadMasterId: Long,
    ): JsonObject

    @POST("api/AccountTransaction/LedgerReport")
    suspend fun downloadReport(@Body downloadLedgerReportResquestModel: DownloadLedgerReportResquestModel): LedgerReportResponseModel

    @GET("api/AccountTransaction/TransactionDetail")
    suspend fun getTransactionDetail(@Query("TransactionId") transactionId: String): ArrayList<TransactionDetailResponseModel>

    @GET("api/AccountTransaction/GetPaidTransactionHistory")
    suspend fun getPaidTransactionHistory(@Query("TransactionId") transactionId: String): ArrayList<HistoryResponseModel>

    //figma

    @GET("/api/Borrower/GetPersonalInformation")
    suspend fun getPersonalInformation(@Query("LeadMasterId") leadMasterId: Int): GetPersonalInformationResponseModel

    @GET("api/Borrower/SendOtp")
    suspend fun getOtp(@Query("MobileNo") mobile: String): PhoneVerifyResponseModel

    @GET("api/Borrower/ValidateOtp")
    suspend fun getOtpVerify(@Query("MobileNo") mobile: String,@Query("Otp") Otp: String,@Query("TxnNo") txnNo: String): OtpVerifyResponseModel

    @Multipart
    @POST("api/Borrower/ManuallyUploadAadharCard")
    suspend fun uploadAadhaarImage(@Part body: MultipartBody.Part,@Query("LeadMasterId") leadMasterId:Int): AadhaarManuallyUploadResponseModel

    @POST("api/Borrower/PostCreditBeurau")
    suspend fun postCreditBeurau(@Body postCreditBeurauRequestModel: PostCreditBeurauRequestModel): PostCreditBeurauResponseModel

    @POST("api/Borrower/AddBusinessDetail")
    suspend fun addBusinessDetail(@Body businessDetailsRequestModel: BusinessDetailsRequestModel): BusinessDetailsResponseModel

    @GET("api/borrower/GetGSTDetails")
    suspend fun getGSTDetails(@Query("GSTNo") GSTNo: String): GSTDetailsResponse

    @GET("api/Anchor/GetBusinessTypeList")
    suspend fun getBusinessTypeList() : BusinessTypeListResponse

    @Multipart
    @POST("api/Borrower/BankPassBookUpload")
    suspend fun bankPassBookUpload(
        @Query("LeadMasterId") LeadMasterId: Int,
        @Part body: MultipartBody.Part
    ): StatementFileResponse


    @POST("api/Borrower/VerifyElectricityBill")
    suspend fun verifyElectricityBill(@Body businessDetailsVerifyElectricityBillRequestModel: BusinessDetailsVerifyElectricityBillRequestModel): BusinessDetailsVerifyElectricityBillResponseModel

    @GET("api/Borrower/GetCiBilInformation")
    suspend fun getUserCreditInfo(@Query("LeadMasterId") LeadMasterId: Int): CibilResponseModel

}