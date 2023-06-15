package com.sk.directudhar.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.appcompat.app.AppCompatDelegate;
import com.sk.directudhar.Api.CommonClassForAPI;
import com.sk.directudhar.model.TokenResponse;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by user on 5/26/2017.
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    public boolean CHECK_FROM_COME = true;

    public Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    /*public void token() {
        CommonClassForAPI.getInstance(activity).getToken(callTokenDes, "password",
                SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.TOKEN_NAME),
                SharePrefs.getInstance(getApplicationContext()).getString(SharePrefs.TOKEN_PASSWORD));

    }*/

    // getting token response
    private final DisposableObserver<TokenResponse> callTokenDes = new DisposableObserver<TokenResponse>() {
        @Override
        public void onNext(@NotNull TokenResponse response) {
            try {
                if (response != null) {
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN, response.getAccess_token());
                    SharePrefs.getInstance(getApplicationContext()).putString(SharePrefs.TOKEN_DATE, new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date()));
                    if (activity != null)
                        activity.recreate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
        }
    };


//    public class MyClickListener implements FirebaseInAppMessagingClickListener {
//
//        @Override
//        public void messageClicked(InAppMessage inAppMessage, Action action) {
//            // Determine which URL the user clicked
//            String url = action.getActionUrl();
//            // Get general information about the campaign
//            CampaignMetadata metadata = inAppMessage.getCampaignMetadata();
//            //
//            System.out.println(url + " " + metadata);
//            if (url != null) {
//                if (url.contains("trade")) {
//                    activity.startActivity(new Intent(activity, TradeActivity.class));
//                    Utils.leftTransaction(activity);
//                } else if (url.contains("skDirect")) {
//                    activity.startActivity(new Intent(activity, TradeActivity.class));
//                    Utils.leftTransaction(activity);
//                } else {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("url", url);
//                    activity.startActivity(new Intent(activity, WebViewActivity.class).putExtras(bundle));
//                }
//            }
//        }
//    }
}