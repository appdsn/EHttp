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

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {

    /*表单上传*/
    @POST()
    @FormUrlEncoded
    Observable<ResponseBody> postForm(@Url String url, @QueryMap Map<String, String> urlMaps, @FieldMap Map<String, String> fieldMaps);

    /*多文件，图文上传*/
    @Multipart
    @POST()
    Observable<ResponseBody> postMulti(@Url String url, @QueryMap Map<String, String> urlMaps, @Part() List<MultipartBody.Part> parts);

    /*自定义实体上传,可以是RequestBody类型（图文上传），可以是java bean（自动转化为json字符串）*/
    @POST()
    Observable<ResponseBody> postBody(@Url String url, @QueryMap Map<String, String> urlMaps, @Body RequestBody body);

    /*通用get请求*/
    @Streaming
    @GET()
    Observable<ResponseBody> get(@Url String url, @QueryMap Map<String, String> urlMaps);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url, @QueryMap Map<String, String> urlMaps);

}
