package com.sk.directudhar.ui.myaccount

import com.google.gson.annotations.SerializedName

data class MyAccountDetailsModel(
    @SerializedName("LeadMasterId") var LeadMasterId: Int? = null,
    @SerializedName("AccountId") var AccountId: Int? = null,
    @SerializedName("AccountNo") var AccountNo: String? = null,
    @SerializedName("TotalUdharLimit") var TotalUdharLimit: Int? = null,
    @SerializedName("AvailableUdharLimit") var AvailableUdharLimit: Int? = null,
    @SerializedName("Outstanding") var Outstanding: Int? = null,
    @SerializedName("NextDueAmount") var NextDueAmount: Int? = null,
    @SerializedName("TotalOverDueAmt") var TotalOverDueAmt: Int? = null,
    @SerializedName("NextDueDate") var NextDueDate: String? = null,
    @SerializedName("CreatedDate") var CreatedDate: String? = null,
    @SerializedName("IsActive") var IsActive: Boolean? = null,
    @SerializedName("LeadNo") var LeadNo: String? = null,
    @SerializedName("Name") var Name: String? = null,
    @SerializedName("MobileNo") var MobileNo: String? = null,
    @SerializedName("UpdatedDate") var UpdatedDate: String? = null
)
