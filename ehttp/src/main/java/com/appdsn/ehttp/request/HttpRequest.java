package com.appdsn.ehttp.request;


import com.appdsn.ehttp.EHttp;
import com.appdsn.ehttp.intercepter.HeadersInterceptor;
import com.appdsn.ehttp.model.ApiService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by baozhong 2018/03/23
 */
public class HttpRequest {
    public RequestParams requestParams;
    public OkHttpClient okHttpClient;
    public Retrofit retrofit;
    public ApiService apiService;

    public HttpRequest() {
        this(new Builder());
    }

    /*
    * 如果不需要重新设置Okhttp和retrofit，则使用EHttp的全局配置
    * */
    private HttpRequest(HttpRequest.Builder builder) {
        requestParams = builder.requestParams;
        if (builder.isNewOkHttp) {
            //添加头放在最前面方便其他拦截器可能会用到
            if (!requestParams.headerParams.isEmpty()) {
                builder.okHttpBuilder.interceptors().add(0, new HeadersInterceptor(requestParams.headerParams));
            }
            okHttpClient = builder.okHttpBuilder.build();
        } else {
            okHttpClient = EHttp.getInstance().getOkHttpClient();
        }

        if (builder.isNewRetrofit || builder.isNewOkHttp) {
            if (builder.isNewOkHttp) {
                builder.retrofitBuilder.client(okHttpClient);
            }
            retrofit = builder.retrofitBuilder.build();
            apiService = retrofit.create(ApiService.class);
        } else {
            retrofit = EHttp.getInstance().getRetrofit();
            apiService = EHttp.getInstance().getApiService();
        }
    }

    public static final class Builder {
        protected boolean isNewOkHttp = false;
        protected boolean isNewRetrofit = false;
        /*配置ohhttp*/
        protected long readTimeout;//读超时
        protected long writeTimeout;//写超时
        protected long connectTimeout;//链接超时
        protected final List<Interceptor> networkInterceptors = new ArrayList<>();
        protected final List<Interceptor> interceptors = new ArrayList<>();
        protected CookieJar cookieJar;
        protected HostnameVerifier hostnameVerifier;
        protected SSLSocketFactory sslSocketFactory;
        protected OkHttpClient okHttpClient;//自定义的Client
        /*配置retrofit*/
        protected String baseUrl;
        protected List<Converter.Factory> converterFactories = new ArrayList<>();
        protected List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
        /*配置请求数据*/
        protected RequestParams requestParams = new RequestParams();
        private Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
        private OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        /*没有默认值，默认值就是EHttp的全局配置，在HttpRequest构造中判断*/
        public Builder() {
            retrofitBuilder = EHttp.getInstance().retrofitBuilder;
            okHttpBuilder = EHttp.getInstance().okHttpBuilder;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }

        /*下面是okhttp配置*/
        public Builder connectTimeout(long timeout) {
            if (timeout > 0) {
                isNewOkHttp = true;
                okHttpBuilder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
            }
            return this;
        }


        public Builder readTimeout(long timeout) {
            if (timeout > 0) {
                isNewOkHttp = true;
                okHttpBuilder.readTimeout(timeout, TimeUnit.MILLISECONDS);
            }
            return this;
        }

        public Builder writeTimeout(long timeout) {
            if (timeout > 0) {
                isNewOkHttp = true;
                okHttpBuilder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
            }
            return this;
        }

        public Builder cookieJar(CookieJar cookieJar) {
            if (cookieJar != null) {
                isNewOkHttp = true;
                okHttpBuilder.cookieJar(cookieJar);
            }
            return this;
        }

        public Builder sslSocketFactory(SSLSocketFactory sslSocketFactory) {
            if (sslSocketFactory != null) {
                isNewOkHttp = true;
                okHttpBuilder.sslSocketFactory(sslSocketFactory);
            }
            return this;
        }

        public Builder hostnameVerifier(HostnameVerifier hostnameVerifier) {
            if (hostnameVerifier != null) {
                isNewOkHttp = true;
                okHttpBuilder.hostnameVerifier(hostnameVerifier);
            }
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (interceptor != null) {
                isNewOkHttp = true;
                okHttpBuilder.addInterceptor(interceptor);
            }
            return this;
        }


        public Builder addNetworkInterceptor(Interceptor interceptor) {
            if (interceptor != null) {
                isNewOkHttp = true;
                okHttpBuilder.addNetworkInterceptor(interceptor);
            }
            return this;
        }

        public Builder client(OkHttpClient client) {
            if (client != null) {
                isNewOkHttp = true;
                okHttpBuilder = client.newBuilder();
            }
            return this;
        }

        /*下面是retrofit配置*/
        public Builder baseUrl(String baseUrl) {
            if (baseUrl != null) {
                isNewRetrofit = true;
                retrofitBuilder.baseUrl(baseUrl);
            }
            return this;
        }

        public Builder addConverterFactory(Converter.Factory factory) {
            if (factory != null) {
                isNewRetrofit = true;
                retrofitBuilder.addConverterFactory(factory);
            }
            return this;
        }


        public Builder addCallAdapterFactory(CallAdapter.Factory factory) {
            if (factory != null) {
                isNewRetrofit = true;
                retrofitBuilder.addCallAdapterFactory(factory);
            }
            return this;
        }


        /*下面是参数配置*/
         /*添加请求头信息*/
        public Builder addHeaderParams(String name, String value) {
            isNewOkHttp = true;
            requestParams.addHeaderParams(name, value);
            return this;
        }

        //url拼接
        public Builder addUrlParams(String key, String value) {
            requestParams.addUrlParams(key, value);
            return this;
        }

        /*自定义实体，比如Json字符串，各种自定义的RequestBody*/
        public Builder addBodyParams(String stringBody) {
            requestParams.addBodyParams(stringBody);
            return this;
        }

        public Builder addBodyParams(File fileBody) {
            requestParams.addBodyParams(fileBody);
            return this;
        }

        public Builder addBodyParams(RequestBody requestBody) {
            requestParams.addBodyParams(requestBody);
            return this;
        }

        public Builder addFormParams(String key, String value) {
            requestParams.addFormParams(key, value);
            return this;
        }

        public Builder addFileParams(String key, String fileName, String contentType, File file
        ) {
            requestParams.addFileParams(key, fileName, contentType, file);
            return this;
        }

        public Builder addStreamParams(String key, String name, String contentType, byte[] bytes) {
            requestParams.addStreamParams(key, name, contentType, bytes);
            return this;
        }
    }
}
