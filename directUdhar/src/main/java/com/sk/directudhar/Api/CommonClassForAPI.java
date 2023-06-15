package com.sk.directudhar.Api;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.sk.directudhar.model.TokenResponse;
import com.sk.directudhar.utils.MyApplication;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CommonClassForAPI {

    private static CommonClassForAPI commonClassForAPI;


    public static CommonClassForAPI getInstance(Activity activity) {
        MyApplication.getInstance().activity = activity;
        if (commonClassForAPI == null) {
            commonClassForAPI = new CommonClassForAPI(activity);
        }
        return commonClassForAPI;
    }

    private CommonClassForAPI(Activity activity) {
        MyApplication.getInstance().activity = activity;
    }

    public void getToken(DisposableObserver<TokenResponse> fetchTokenDes, String password, String username, String Password) {
        RestClient.getInstance().getService().getToken(password, username, Password)
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
        RestClient.getInstance().getService().generateLead(Url)
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
