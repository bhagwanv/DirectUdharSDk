package com.sk.directudhar.ui.mandate
import com.google.gson.JsonObject
import org.json.JSONObject

data class EMandateAddResponseModel(
    val Msg: String,
    val Result: Boolean,
    val Data: JsonObject
)
data class EMandateData(
    val  features :Features,
    val  consumerData :ConsumerData
)

data class Features(
    val  enableAbortResponse :Boolean,
    val  enableExpressPay :Boolean,
    val  enableMerTxnDetails :Boolean,
    val  siDetailsAtMerchantEnd :Boolean,
    val  enableSI :Boolean
)
data class ConsumerData(
    val  deviceId :String,
    val  token :String,
    val  paymentMode :String,
    val  merchantLogoUrl :String,
    val  merchantId :String,
    val  currency :String,
    val  consumerId :String,
    val  consumerMobileNo :String,
    val  consumerEmailId :String,
    val  txnId :String,
    val  items :ArrayList<Items>,
    val  customStyle :CustomStyle,
    val  accountType :String,
    val  debitStartDate :String,
    val  debitEndDate :String,
    val  maxAmount :Double,
    val  amountType :String,
    val  frequency :String,
    val  accountNo :String
)

data class Items(
    val  itemId :String,
    val  amount :Double,
    val  comAmt :Double,
)

data class CustomStyle(
    val  PRIMARY_COLOR_CODE :String,
    val  SECONDARY_COLOR_CODE :String,
    val  BUTTON_COLOR_CODE_1 :String,
    val  BUTTON_COLOR_CODE_2 :String,
)