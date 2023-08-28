package com.sk.directudhar.ui.agreement

import com.google.gson.annotations.SerializedName

data class AgreementResponseModel(
   var Data: AgreementData,
   var Msg: String,
   var Result: Boolean,
   var DynamicData: String
)
data class AgreementData(
    var AgreementfilePath: String,
    var Agreementhtml: String
)