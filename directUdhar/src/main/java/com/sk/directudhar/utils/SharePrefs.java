package com.sk.directudhar.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 13-11-2018.
 */
public class SharePrefs {
    public static String PREFERENCE = "SkRetailer";

    public static final String MOBILE_NUMBER = "mobile";
    public static final String IS_SIGN_UP = "is_sign_up";
    public static final String CUST_ACTIVE = "cust_active";
    public static final String SK_CODE = "Skcode";
    public static final String CUSTOMER_ID = "Customer_Id";
    public static final String APP_VERSION = "app_version";
    public static final String CUSTOMER_NAME = "Customer_name";
    public static final String SHOP_NAME = "shop_name";
    public static final String COMPANY_ID = "company_id";
    public static final String CUSTOMER_TYPE = "customer_type";
    public static final String SHIPPING_ADDRESS = "shipping_address";
    public static final String WAREHOUSE_ID = "warehouse_id";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "CITY_NAME";
    public static final String STATE_NAME = "STATE_NAME";
    public static final String PIN_CODE = "pin_code";
    public static final String CUSTOMER_EMAIL = "email_id";
    public static final String WALLET_POINT = "WALLET_POINT";
    public static final String CURRENT_WALLET_POINT = "current_wallet_point";
    public static final String PASSWORD = "PASSWORD";
    public static final String USER_PROFILE_IMAGE = "user_profile_image";
    public static final String CART_AMOUNT_API_CALL = "cart_amount_api_call";
    public static final String IS_REQUIRED_LOCATION = "is_Required_Location";
    public static final String REFERRAL_BY = "referral_by";

    public static final String AVAIL_DIAL = "AvailDial";
    public static String CURRENT_LANGUAGE = "CurrentLanguage";
    public static String ENTER_REWARD_POINT = "EnterRewardPoint";
    public static String AMOUNT_TO_REDUCE = "AmountToReduct";
    public static String ITEM_FLASH_DEAL_USED_JSON = "ItemFlashDealUsedJson";
    public static String SIGNUPLOC = "signuploc";
    public static String DILIVERY_CHARGE = "delivery_charge";
    public static String TOKEN = "empty_token";
    public static String TOKEN_NAME = "token_user_name";
    public static String TOKEN_PASSWORD = "token_password";
    public static String CRITICAL_INFO_MISSING_MSG = "critical_info_missing_msg";
    // local
    public static String CATEGORY_BY_ALL = "category_by_all";
    public static String ALL_CATEGORY_SERACH = "all_catrgory_search";
    public static String BASE_CAT = "base_cat";
    public static final String PROFILE_DATA = "myProfileResponse";
    public static final String MY_UDHAR_GET_DATA = "my_udhar_data";
    public static final String LICENSE_NO = "license_no";
    public static final String GST_NO = "gst_no";
    public static final String DOC_EMPTY = "doc_empty";
    public static final String DOC_DAY_EMPTY = "doc_day_empty";
    public static final String MURLI_API_CALL = "murli_api_call";
    public static final String BASKET_OFFER = "basket_offer";
    public static final String IsSignup = "isSignup";
    public static final String COMPANY_CONTACT = "company_contact";
    // for dialog view
    public static final String IS_DIALOG_SHOWN = "is_dialog_shown";
    // epaylater credentials
    public static final String E_PAY_LATER_URL = "E_PAY_LATER_URL";
    public static final String ENCODED_KEY = "ENCODED_KEY";
    public static final String BEARER_TOKEN = "BEARER_TOKEN";
    public static final String IV = "IV";
    public static final String M_CODE = "M_CODE";
    public static final String CATEGORY = "category";
    //
    public static final String IS_COMPANY_API_CALL = "is_company_api_call";
    public static final String IS_FAV_API_CALL = "is_fav_api_call";
    public static final String IS_PAYMENT_GATWAY = "is_payment_gateway";
    public static final String IS_SHOW_LEDGER = "is_show_ledger";
    public static final String FinboxClientApiKey = "finboxclientkey";
    public static final String IS_SHOW_TARGET = "is_show_target";
    public static final String IS_SHOW_RETURN_ORDER = "is_show_return_order";
    public static final String IS_TERMS_ACCEPT = "is_terms_accepted";
    public static final String MAX_WALLET_POINT_USED = "max_wallet_point_used";
    // HDFC credentials
    public static final String MERCHANT_ID = "merchant_id";
    public static final String ACCESS_CODE = "access_code";
    public static final String REDIRECT_URL = "redirect_url";
    public static final String GATWAY_URL = "gateway_url";
    public static final String CANCEL_URL = "cancel_url";
    // hdfc credit keys
    public static final String IS_SHOW_CREDIT = "c_is_show_credit";
    public static final String C_OPTION_NAME = "credit_option_name";
    public static final String C_MERCHANT_ID = "c_merchant_id";
    public static final String C_ACCESS_CODE = "c_access_code";
    public static final String C_REDIRECT_URL = "c_redirect_url";
    public static final String C_GATEWAY_URL = "c_gateway_url";
    public static final String C_CANCEL_URL = "c_cancel_url";
    public static final String C_WORKING_KEY = "c_working_key";
    // finbox
    public static final String ISFINBOX = "ISFINBOX";
    public static final String IS_SHOW_CREDIT_LINE = "is_show_creditLine";
    public static final String FINBOX_CREDIT_KEY = "creditLineKey";
    //
    public static final String SEARCH_HINT_DATA = "searchhintdata";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "long";
    // Murli story read page count
    public static final String STORY_CURRENT_PAGE = "murli_story_page";
    // Murli story read page count
    public static final String IS_ICON_CREATED = "is_murli_icon_created";
    // notification count
    public static final String NOTIFICATION_COUNT = "notification_count";
    public static final String HISAB_KITAB_ID = "Id";
    // WebView base url
    public static final String WEB_VIEW_BASE_URL = "webView_base_url";
    public static final String IS_SHOW_HISAB = "is_show_hisab_kitab";
    public static final String TRADE_WEB_URL = "trade_webView_url";
    public static final String IS_SHOW_TICKET_MENU = "is_show_ticket_menu";
    //saveContact
    public static final String CONTACT_LIST = "contact_list";
    //clusterID
    public static final String CLUSTER_ID = "cluster_id";
    public static final String CUSTOMER_BALANCE = "customer_balance";
    public static final String CUSTOMER_VERIFY = "cust_verify";
    // check customer premium
    public static final String IS_PRIME_ACTIVE = "is_premium_active";
    public static final String IS_CHECKBOOK_SHOW = "is_check_book_show";
    public static final String IS_HDFC_PAYMENT = "IsOnlinePayment";
    public static final String IS_EPAY_LATER_SHOW = "IsePaylaterShow";
    public static final String IS_PRIME_MEMBER = "is_premium_member";
    public static final String PRIME_NAME = "prime_new_name";
    public static final String PRIME_AMOUNT = "prime_amount";
    public static final String CHECKBOOK_API_KEY = "check_book_api_key";
    public static final String CHECKBOOK_BASE_URL = "check_BookBase_URl";
    public static final String PRIME_DURATION = "prime_duration";     // in months
    public static final String PRIME_EXPIRY = "prime_expiry";
    public static final String IsKPP = "Is_kPP";
    public static final String IS_ChQBOOKMINI_AMT = "ischeckBookMinAmt";

    public static final String PROFILE_LAST_DATE_SHOW = "profile_last_date_show";

    public static final String LAST_MOBILE_NUMBER = "last_mobile_number";
    public static final String LAST_TRUE_CUSTOMER = "last_true_customer";
    public static final String LAST_OTP_TIME = "last_otp_time";
    public static final String LAST_IS_OTP_SENT = "last_is_otp_sent";
    public static final String LAST_OTP_COUNT = "last_otp_count";

    public static final String SELECTED_LANGUAGE = "selected_language";
    public static final String IS_FETCH_LANGUAGE = "is_fetch_language";
    public static final String LAST_LANGUAGE_UPDATE_DATE = "last_language_update_date";
    // gullak
    public static final String GULLAK_BALANCE = "gullak_balance";
    public static final String RTGS_BAL = "rtgs_balance";
    // token create date
    public static final String TOKEN_DATE = "token_create_date";
    // gullak balance fetched or not
    public static final String IS_GULLAK_BAL = "gullak_balance_fetched";
    // var to show change language dialog
    public static final String IS_SHOW_LANG_DIALOG = "show_change_language_dialog";
    public static final String IS_SHOW_VATM = "showHideVAtm";
    public static final String IS_DIRECT_UDHAR = "showDirectUdhar";
    public static final String IS_D_UDHAR_GULLAK = "showDirectUdharGullak";
    public static final String FIRESTORE_APPLICATION_ID = "firestoreApplicationId";
    public static final String FIRESTORE_API_KEY = "firestoreApiKey";
    public static final String FIRESTORE_DATABASE_URL = "firestoreDatabaseUrl";
    public static final String FIRESTORE_PROJECT_ID = "firestoreProjectId";
    public static final String AZURE_ACCOUNT_NAME = "azureAccountName";
    public static final String AZURE_ACCOUNT_KEY = "azureAccountKey";
    // seller view key
    public static final String IS_WAREHOUSE_AVAIL = "isWarehouseLive";
    public static final String IS_SELLER_AVAIL = "isSellerAvailable";

    public static final String IS_UDHAAR_OVERDUE = "is_udhaar_overdue";
    public static final String IS_UDHAAR_ORDER = "is_udhaar_order";

    public static final String IS_UDHAAR_POPUP_OPEN_IN_FIRST_TIME= "is_udhaar_popup_open_in_first_time";


    private final Context ctx;
    private final SharedPreferences sharedPreferences;
    private static SharePrefs instance;


    public SharePrefs(Context context) {
        ctx = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE, 0);
    }

    public static SharePrefs getInstance(Context ctx) {
        if (instance == null) {
            instance = new SharePrefs(ctx);
        }
        return instance;
    }

    public void putString(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }


    public void putInt(String key, Integer val) {
        sharedPreferences.edit().putInt(key, val).apply();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }


    public void putBoolean(String key, Boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }


    public double getDouble(String key) {
        return Double.parseDouble(sharedPreferences.getString(key, "0"));
    }


    public void clear() {
        sharedPreferences.edit().clear().commit();
    }


    public static String getStringSharedPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        return settings.getString(name, "");
    }

    // for username string preferences
    public static void setStringSharedPreference(Context context, String name, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.apply();
    }
}