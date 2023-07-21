package com.sk.directudhar.ui.adharcard

import com.google.gson.annotations.SerializedName

data class UpdateAadhaarInfoRequestModel(
    @SerializedName("AadharNo") var aadharNo: String,
    @SerializedName("LeadMasterId") var leadMasterId: Int
)
