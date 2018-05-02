package com.appdsn.ehttp.function;


import android.os.Handler;

import com.appdsn.ehttp.EHttp;
import com.appdsn.ehttp.callback.HttpCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;


/**
 * Created by wbz360 on 2018/3/19.
 */
/*统一处理：包括结果解析，错误处理等*/
@Deprecated
public class ResultObserver<R> extends DisposableObserver<ResponseBody> {
    private HttpCallback<R> callback;
    private Handler mHandler;
    private Object tag;

    public ResultObserver(Object tag, HttpCallback<R> callback) {
        this.callback = callback;
        this.tag = tag;
        mHandler = EHttp.getInstance().getHandler();
        addDisposable(tag);
    }

    /*对返回的结果进行解析，转换成最终CallBack需要的数据类型*/
    @Override
    public void onNext(@NonNull ResponseBody body) {
        try {
            Object result = callback.parseResponse(body);
            sendSuccessResultCallback(result);
        } catch (Exception e) {//解析数据异常
            sendFailResultCallback(e);
        }
    }

    public void sendFailResultCallback(final Throwable e) {
        if (callback == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(e);
                callback.onComplete();
            }
        });
    }

    public void sendSuccessResultCallback(final Object result) {
        if (callback == null) return;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess((R) result);
            }
        });
    }

    @Override
    public void onError(@NonNull Throwable e) {
        sendFailResultCallback(e);
    }

    @Override
    public void onComplete() {
        if (callback != null) {
            callback.onComplete();
        }
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
}
