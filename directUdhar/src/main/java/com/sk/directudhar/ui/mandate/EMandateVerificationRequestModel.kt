package com.sk.directudhar.ui.mandate

import com.google.gson.JsonObject
import org.json.JSONObject

data class EMandateVerificationRequestModel(var LeadMasterId: Int, var eMandateRegisterResDc:MandateRegisterResDc)
data class MandateRegisterResDc(var msg :String,var merchant_code:String)