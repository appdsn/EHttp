package com.appdsn.ehttp.function;


import com.appdsn.ehttp.model.ApiException;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * Created by wbz360 on 2018/3/21.
 */
/*拦截异常统一处理后，（包括Throwable和Exception），再重新订阅一个异常事件*/
public class ExceptionFunction<R> implements Function<Throwable, Observable<R>> {
    @Override
    public Observable<R> apply(@NonNull Throwable throwable) throws Exception {
        return Observable.error(ApiException.handleException(throwable));
    }
}
