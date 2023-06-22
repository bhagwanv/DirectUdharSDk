package com.example.directudharsdk;

import android.app.Activity;
import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.directudharsdk.Api.CommonMainClassForAPI;
import com.example.directudharsdk.utils.SharePrefsMain;
import com.sk.directudhar.Api.CommonClassForAPI;
import com.sk.directudhar.model.TokenResponse;
import com.sk.directudhar.utils.MyApplication;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by user on 5/26/2017.
 */
public class MyApplicationMain extends Application {
    private static MyApplicationMain mInstance;

    public Activity activity;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        MyApplication.initDirectUdhaarApp(activity);
    }


    public static synchronized MyApplicationMain getInstance() {
        return mInstance;
    }


    public void token() {
        CommonMainClassForAPI.getInstance(activity).getTokenClient(
                callAppTokenDes, "client_credentials",
                "b02013e9-b92b-4563-a330-aec123bf13d7",
                "e57f97e0-46ea-4be0-9fdf-c92b410cf022"
        );

    }

    // getting token response
    private final DisposableObserver<TokenResponse> callAppTokenDes = new DisposableObserver<TokenResponse>() {
        @Override
        public void onNext(@NotNull TokenResponse response) {
            try {
                if (response != null) {
                    SharePrefsMain.getInstance(getApplicationContext()).putString(SharePrefsMain.TOKEN, response.getAccess_token());
                    SharePrefsMain.getInstance(getApplicationContext()).putString(SharePrefsMain.TOKEN_DATE, new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date()));
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