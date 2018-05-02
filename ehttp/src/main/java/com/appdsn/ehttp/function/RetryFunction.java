package com.appdsn.ehttp.function;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;

/**
 * Created by wbz360 on 2018/3/21.
 */
/*处理异常重试*/
public class RetryFunction implements Predicate<Throwable> {

    @Override
    public boolean test(@NonNull Throwable throwable) throws Exception {
        if ((throwable instanceof ConnectException
                || throwable instanceof SocketTimeoutException
                ||throwable instanceof TimeoutException)) {
            return true;
        }
        return false;
    }
}
