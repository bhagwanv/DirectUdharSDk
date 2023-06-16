package com.sk.directudhar;

import android.app.Activity;

import android.content.Intent;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.sk.directudhar.Api.CommonClassForAPI;
import com.sk.directudhar.activity.DirectUdharActivity;
import com.sk.directudhar.utils.Utils;

import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;

public class MainDirectUdharActivity {
    public static Activity activityMain;

    public static void SimpleToast(Activity activity,String massge){
        Toast.makeText(activity, massge, Toast.LENGTH_SHORT).show();
    }

    public static void callLeadApi(Activity activity,String url) {
        activityMain = activity;
        CommonClassForAPI commonClassForAPI = CommonClassForAPI.getInstance(activity);
        Utils.UtilsObject.showProgressDialog(activity);
        commonClassForAPI.generateLead(leadObserver, url);
    }

    private static final DisposableObserver<JsonObject> leadObserver = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(@NonNull JsonObject jsonObject) {
            Utils.UtilsObject.hideProgressDialog();
            try {
                if (jsonObject != null) {
                    boolean isSuccess = jsonObject.get("Result").getAsBoolean();
                    if (isSuccess) {
                        String url = jsonObject.get("Data").getAsString();
                        activityMain.startActivity(new Intent(activityMain, DirectUdharActivity.class).putExtra("url", url));
                    } else {
                        String msg = jsonObject.get("Msg").getAsString();
                       // new AlertDialog.Builder(HomeActivity.this).setTitle(MyApplication.getInstance().dbHelper.getString(R.string.alert)).setMessage(msg).setNegativeButton(getString(R.string.ok), null).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {
            e.printStackTrace();
            Utils.UtilsObject.hideProgressDialog();
        }

        @Override
        public void onComplete() {
            dispose();
        }
    };
}
