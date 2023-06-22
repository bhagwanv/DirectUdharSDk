package com.example.directudharsdk.Api;

import android.app.Activity;

import androidx.annotation.NonNull;
import com.example.directudharsdk.MyApplicationMain;
import com.google.gson.JsonObject;
import com.sk.directudhar.model.TokenResponse;
import com.sk.directudhar.utils.MyApplication;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CommonMainClassForAPI {

    private static CommonMainClassForAPI commonClassForAPI;


    public static CommonMainClassForAPI getInstance(Activity activity) {
        MyApplicationMain.getInstance().activity = activity;
        if (commonClassForAPI == null) {
            commonClassForAPI = new CommonMainClassForAPI(activity);
        }
        return commonClassForAPI;
    }

    private CommonMainClassForAPI(Activity activity) {
        MyApplicationMain.getInstance().activity = activity;
    }

    public void getTokenClient(DisposableObserver<TokenResponse> fetchTokenDes, String password, String username, String Password) {
        RestClientMain.getInstance().getService().getTokenClient(password, username, Password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TokenResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull TokenResponse object) {
                        fetchTokenDes.onNext(object);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        fetchTokenDes.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        fetchTokenDes.onComplete();
                    }
                });
    }

    public void generateLead(final DisposableObserver<JsonObject> observer, String Url) {
        RestClientMain.getInstance().getService().generateLead(Url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(JsonObject o) {
                        observer.onNext(o);
                    }

                    @Override
                    public void onError(Throwable e) {
                        observer.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        observer.onComplete();
                    }
                });
    }

}
