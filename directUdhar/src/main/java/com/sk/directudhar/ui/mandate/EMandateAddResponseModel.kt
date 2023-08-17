package com.sk.directudhar.ui.mandate
import com.google.gson.JsonObject
import org.json.JSONObject

data class EMandateAddResponseModel(
    val Msg: String,
    val Result: Boolean,
    val Data: JsonObject
)