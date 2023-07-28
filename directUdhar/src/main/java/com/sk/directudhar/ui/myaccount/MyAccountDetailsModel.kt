package com.sk.directudhar.ui.myaccount

import com.google.gson.annotations.SerializedName

data class MyAccountDetailsModel(
    @SerializedName("LeadMasterId") var leadMasterId: Int? = null,
    @SerializedName("AccountId") var accountId: Int? = null,
    @SerializedName("AccountNo") var accountNo: String? = null,
    @SerializedName("TotalUdharLimit") var totalUdharLimit: Double? = null,
    @SerializedName("AvailableUdharLimit") var availableUdharLimit: Double? = null,
    @SerializedName("Outstanding") var outstanding: Double? = null,
    @SerializedName("NextDueAmount") var nextDueAmount: Double? = null,
    @SerializedName("TotalOverDueAmt") var totalOverDueAmt: Double? = null,
    @SerializedName("NextDueDate") var nextDueDate: String? = null,
    @SerializedName("CreatedDate") var createdDate: String? = null,
    @SerializedName("IsActive") var isActive: Boolean? = null,
    @SerializedName("LeadNo") var leadNo: String? = null,
    @SerializedName("Name") var name: String? = null,
    @SerializedName("MobileNo") var mobileNo: String? = null,
    @SerializedName("UpdatedDate") var updatedDate: String? = null
)
