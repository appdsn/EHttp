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

package com.appdsn.ehttp.intercepter;


import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 配置请求头部信息

 */
public class HeadersInterceptor implements Interceptor {
    private Map<String, String> commonHeaders;
    public HeadersInterceptor(Map<String, String> commonHeaders) {
        this.commonHeaders = commonHeaders;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        if (commonHeaders!=null&&!commonHeaders.isEmpty()) {
            Request.Builder builder = original.newBuilder();
            for (String key : commonHeaders.keySet()) {
                builder.addHeader(key, commonHeaders.get(key));
            }
            original=builder.build();
        }
        return chain.proceed(original);
    }
}
