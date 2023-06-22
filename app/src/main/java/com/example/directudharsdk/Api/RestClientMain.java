package com.example.directudharsdk.Api;

import com.example.directudharsdk.MyApplicationMain;
import com.example.directudharsdk.utils.Aes256Main;
import com.google.gson.Gson;
import com.sk.directudhar.BuildConfig;
import com.sk.directudhar.utils.Aes256;
import com.example.directudharsdk.utils.UtilsMain;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClientMain {
    private static Retrofit retrofit = null, retrofit1 = null;
    private static Retrofit retrofit2 = null;
    private static Retrofit retrofit3 = null;

    private static final RestClientMain ourInstance = new RestClientMain();
    private static RestClientMain restClient3;
    private static RestClientMain restClient1;
    private static String mSectionType;
    private Request request;


    public static RestClientMain getInstance() {
        mSectionType = "";
        return ourInstance;
    }

    public static RestClientMain getInstance(String sectionType) {
        mSectionType = sectionType;
        return ourInstance;
    }






    private RestClientMain() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(6, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .retryOnConnectionFailure(false)
                .addInterceptor(chain -> {
                    Response response = null;
                    try {
                        request = chain.request();
                        response = chain.proceed(request);
                        if (response.code() == 401) {
                            MyApplicationMain.getInstance().token();

                        }
                        if (response.code() == 200) {
                            if (!request.url().toString().contains("/GetCompanyDetailsForRetailerWithToken") &&
                                    !request.url().toString().contains("/token") &&
                                    !request.url().toString().contains("/appVersion") &&
                                    !request.url().toString().contains("/imageupload") &&
                                    !request.url().toString().contains("/GenerateToken") &&
                                    !request.url().toString().contains("/UPI/InitiateDUPayInetentReq") &&
                                    !request.url().toString().contains("/place/autocomplete")) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("message", new JSONObject(response.body().string()));
                                    String data = jsonObject.getJSONObject("message").getString("Data");
                                    String destr = Aes256Main.decrypt(data, new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(new Date()) + "1201");
                                    if (BuildConfig.DEBUG) {
                                        printMsg(destr);
                                    }
                                    MediaType contentType = response.body().contentType();
                                    ResponseBody responseBody = ResponseBody.create(contentType, destr);
                                    return response.newBuilder().body(responseBody).build();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response;
                })
                .addInterceptor(chain -> {
                    request = chain.request().newBuilder()
                            .header("username", UtilsMain.UtilsObject.getCustMobile(MyApplicationMain.getInstance()))
                            .header("activity", MyApplicationMain.getInstance().activity == null ? "" : MyApplicationMain.getInstance().activity.getClass().getSimpleName() + "")
                            .addHeader("authorization", "Bearer " + UtilsMain.UtilsObject.getToken(MyApplicationMain.getInstance().activity))
//                            .addHeader("NoEncryption", "1")
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(interceptor)
                .build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(UtilsMain.UtilsObject.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create(new Gson()))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }
    }


    public APIMainServices getService() {
        return retrofit.create(APIMainServices.class);
    }

    public APIMainServices getService1() {
        return retrofit1.create(APIMainServices.class);
    }

    public APIMainServices getService3() {
        return retrofit3.create(APIMainServices.class);
    }

    public APIMainServices getService2() {
        return retrofit2.create(APIMainServices.class);
    }


    private void printMsg(String msg) {
        int chunkCount = msg.length() / 4050;     // integer division
        for (int i = 0; i <= chunkCount; i++) {
            int max = 4050 * (i + 1);
            if (max >= msg.length()) {
                System.out.println(msg.substring(4050 * i));
            } else {
                System.out.println(msg.substring(4050 * i, max));
            }
        }
    }
}