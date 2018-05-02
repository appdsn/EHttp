package com.appdsn.ehttp.callback;

import com.appdsn.ehttp.EHttp;
import com.appdsn.ehttp.model.ApiException;
import com.appdsn.ehttp.model.ApiResult;
import com.appdsn.ehttp.utils.TypeUtil;
import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;

/**
 * Created by wbz360 on 2018/3/20.
 */

public abstract class ApiCallback<T> extends HttpCallback<T> {
    private Type dataType;
    private Class<?> dataRawType;
    public Class<? extends ApiResult> resultType;
    private Gson gson;

    public ApiCallback() {
        //默认值是是由EHttp统一设置的ApiResult类型，默认为null，
        // 也就是说T是什么类型，就转换成什么类型
        this(EHttp.getInstance().apiResult);
    }

    public ApiCallback(Class<? extends ApiResult> resultType) {
        if (resultType != null) {
            this.resultType = resultType; //配置自定义的ApiResult
        }
        dataType = TypeUtil.getSuperclassTypeParameter(this);
        dataRawType = TypeUtil.getRawType(dataType);
        gson = EHttp.getInstance().gson;
    }

    /*可以解析成2种类型的数据
    * 1：ApiResult类型
    * 2：其他自定义的java bean*/
    @Override
    public T parseResponse(ResponseBody body) throws Exception {
        String jsonStr = body.string();
        T result = null;
        //如果是ApiResult类型，且不为空，则解析成ApiResult类型
        if (resultType != null && ApiResult.class.isAssignableFrom(resultType)) {
            Type rusultType = $Gson$Types.newParameterizedTypeWithOwner(null, resultType, dataType);
            ApiResult apiResult = gson.fromJson(jsonStr, rusultType);//会抛出JsonSyntaxException
            //会抛出ApiException服务端错误，不捕捉，直接返回给上一级处理
            result = parseResult(apiResult);
        } else {//resultType为null,解析成T类型返回
            result = (T) gson.fromJson(jsonStr, dataType);
        }
        return result;
    }

    private T parseResult(ApiResult apiResult) throws Exception {
        if (apiResult == null) {
            throw new ApiException(-1, "json is null");
        }
        if (!apiResult.isResultOk()) {
            throw new ApiException(apiResult.getCode(), apiResult.getMsg());
        }
        T result = (T) apiResult.getData();
        if (result == null) {
            throw new ApiException(apiResult.getCode(), "ApiResult getData is null");
        }
        return result;
    }

}