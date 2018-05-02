/*
 * Copyright (C) 2017 zhouyou(478319399@qq.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appdsn.ehttp.model;

import android.util.Log;

import retrofit2.HttpException;


/**
 * 统一处理了API异常错误EHttp
 */
public class ApiException extends Exception {
    private int code=-1;
    public ApiException(String message, Throwable cause) {
        super(message,cause);
    }
    public ApiException(int code,String message){
        super(message);
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public static Throwable handleException(Throwable e) {
        e.printStackTrace();//打印错误信息
        Log.i("123",e.toString());
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            //统一处理，或者转化错误等

        }
        return e;
    }

}