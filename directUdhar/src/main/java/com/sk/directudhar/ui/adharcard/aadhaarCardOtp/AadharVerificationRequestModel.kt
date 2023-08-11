package com.sk.directudhar.ui.adharcard.aadhaarCardOtp

import com.google.gson.annotations.SerializedName
import com.sk.directudhar.ui.adharcard.UpdateAadhaarInfoRequestModel

data class AadharVerificationRequestModel(
    @SerializedName("otp") var otp: String,
    @SerializedName("request_id") var requestId: String,
    @SerializedName("UpdateAdhaarInfoDc") var updateAadhaarInfoRequestModel: UpdateAadhaarInfoRequestModel
)
