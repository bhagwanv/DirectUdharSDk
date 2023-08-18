package com.sk.directudhar.ui.adharcard

import com.google.gson.annotations.SerializedName

data class AadhaarUpdateResponseModel(
    var Msg: String? = null,
    var Result: Boolean? = null,
    var DynamicData: DynamicData? = DynamicData()
)

data class DynamicData(
    var request_id: String? = null
)