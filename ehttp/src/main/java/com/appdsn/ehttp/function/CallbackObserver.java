package com.appdsn.ehttp.function;


import com.appdsn.ehttp.EHttp;
import com.appdsn.ehttp.callback.HttpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;


/**
 * Created by wbz360 on 2018/3/19.
 */

public class CallbackObserver<R> extends DisposableObserver<R> {
    private Object tag;
    private HttpCallback<R> callback;

    public CallbackObserver(Object tag, HttpCallback<R> callback) {
        this.tag = tag;
        this.callback = callback;
        addDisposable(tag);
    }

    public void addDisposable(Object tag) {
        HashMap<Object, List<Disposable>> disposables = EHttp.getInstance().disposables;
        List<Disposable> tagList = disposables.get(tag);
        if (tagList == null) {
            tagList = new ArrayList<>();
            disposables.put(tag, tagList);
        }
        tagList.add(this);
    }

    public void removeDisposable() {
        HashMap<Object, List<Disposable>> disposables = EHttp.getInstance().disposables;
        List<Disposable> tagList = disposables.get(tag);
        if (tagList == null) {
            tagList = new ArrayList<>();
            disposables.put(tag, tagList);
        }
        tagList.remove(this);
    }

    @Override
    protected void onStart() {
        if (callback != null) {
            callback.onStart();
        }
    }

    @Override
    public void onNext(@NonNull R result) {
        if (callback != null) {
            callback.onSuccess(result);
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        if (callback != null) {
            callback.onFailure(e);
            onComplete();//出错也调用完成
        }
    }

    @Override
    public void onComplete() {
        removeDisposable();
        if (callback != null) {
            callback.onComplete();
        }
    }
}
