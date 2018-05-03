框架简介<br>
=
EHttp是一个基于OkHttp3.0，RxJava2.0和Retrofit2.0封装的Http网络请求框架，
它使用简单，配置灵活，可以快速实现网络请求。目前支持：get请求，post请求，
多文件上传，文件下载，图片加载，上传下载进度监听，设置自定义拦截器，
cookie管理，https证书设置，自定义Retrofit接口，多种Callback处理返回的不同数据类型。

添加依赖
=
```java
allprojects {
  repositories {
      ...
      maven { url 'https://jitpack.io' }
  }
}

dependencies {
  compile 'com.github.appdsn:EHttp:2.0'
}
```
使用方法
==

（1）简单get请求
--
>EHttp的get和post静态方法都返回一个Disposable对象，可以用它来取消一个网络请求，
tag标记一类网络请求订阅，可以用来取消所有带有此tag的订阅，一般传入Context对象
```java 
    Disposable disposable;
    disposable=EHttp.get(tag, url, new StringCallback() {
            @Override
            public void onStart() {
                super.onStart();
                //初始化操作，当前调用get方法的线程，一般是主线程
            }
            @Override
            public void onFailure(Throwable e) {
               //失败处理
            }
            @Override
            public void onSuccess(String rusult) {
                //成功处理
            }
    });
    disposable.dispose();//取消当前一个请求
    EHttp.cancelTag(tag);//取消所有带tag的请求
 ```
 
（2）简单post请求
-
>post有请求体，需要在HttpRequest中设置，它还有其他设置参数，下面会详细说明。ApiCallback会返回传递进去的Java Bean类型的对象。<br>
```java   
    //请求参数设置：比如一个json字符串
    HttpRequest request=new HttpRequest.Builder()
                .addBodyParams(json)
                .build();
    EHttp.post(tag,url,request, new ApiCallback<UserInfo>() {
            @Override
            public void onFailure(Throwable e) {
               
            }
            @Override
            public void onSuccess(UserInfo response) {
               
            }
            @Override
            public void onUpProgress(long bytesWritten, long totalSize) {
                super.onUpProgress(bytesWritten, totalSize);
                //上传进度监听
            }
            @Override
            public void onStart() {
                super.onStart();
            }
    });
 ```

（3）下载一个文件
-
>文件下载就是一个get请求，只是传入的HttpCallBack是FileCallBack类型，会返回指定类型的数据，这里是File。
```java
EHttp.get(tag, url, new FileCallBack(getExternalCacheDir().getPath()+"/ehttp","ehttp.apk") {
            @Override
            public void onStart() {
                super.onStart();
            }
            @Override
            public void onDownProgress(long bytesWritten, long totalSize) {
                //下载进度监听
            }
            @Override
            public void onFailure(Throwable e) {
                
            }
            @Override
            public void onSuccess(File rusult) {
               
            }
    });
 
 ```

（4）下载一个图片
-
```java
EHttp.get(this, url, new BitmapCallback() {
            @Override
            public void onFailure(Throwable e) {
                
            }
            @Override
            public void onSuccess(Bitmap rusult) {
            }
    });
 ```
（5）自定义HttpCallBack
-
>上面的简单get请求，简单post请求，下载文件以及下载图片等，它们返回的结果数据类型不同，归根结底都是传递的HttpCallBack类型的不同，上面是内置的四种类型结果，如果不能满足你的需要，你可以继承HttpCallBack抽象类，重写parseResponse方法，实现自己的解析逻辑，返回你需要的类型。
```java
public abstract class HttpCallback<T> {
    /**
     * UI Thread
     */
    public void onStart() {
    }
    public abstract void onFailure(Throwable e);
    public abstract void onSuccess(T rusult);
    public void onUpProgress(long bytesWritten, long totalSize) {
    }
    public void onComplete() {
    }
    /**
     * Thread Pool Thread
     */
    public abstract T parseResponse(ResponseBody body) throws Exception;
}
```

（6）请求参数设置
-
>不管是get请求还是post请求都可以设置请求参数，它由HttpRequest类设置，注意：每次设置的参数只对当前请求有效。post请求只比get请求多了一个请求实体，其他参数都是一样的。

* Post请求实体设置

>post请求可以设置多种类型的实体，每次请求只能设置一种请求实体
 ```java
 HttpRequest request=new HttpRequest.Builder()
                .addBodyParams(String json)//post一个json字符串
                .addBodyParams(File file)//post一个没有key的文件
                .addFormParams(String key, String value)//post一个表单文件
                .addFileParams(String key, String fileName, String contentType, File file)//多文件上传
                .addStreamParams(String key, String name, String contentType, byte[] bytes)//多文件上传字节数组型
                .build();
 ```

* 请求头信息
```java
HttpRequest request=new HttpRequest.Builder()
     .addHeaderParams(String name, String value)//添加当前这个请求的请求头
     .build();
```
* 其他配置
>connectTimeout（链接超时），readTimeout，writeTimeout，cookieJar，sslSocketFactory，hostnameVerifier，addInterceptor，addNetworkInterceptor，OkHttpClient，baseUrl，addUrlParams等

（7）全局参数配置
-
>这里的配置参数是全局的默认配置，如果某个请求没有单独配置请求参数，都将默认使用这个全局配置参数
```java
     EHttp.Builder builder=new EHttp.Builder(); 
        builder.addCallAdapterFactory();
        builder.addConverterFactory();
        builder.addInterceptor();
        builder.addNetworkInterceptor();
        builder.cookieJar();
        builder.baseUrl();
        builder.client();
        builder.connectTimeout();
        builder.debug();
        builder.gson();
        builder.header();
        builder.hostnameVerifier();
        builder.connectTimeout();
        builder.readTimeout();
        builder.retryCount();
        builder.sslSocketFactory();
        builder.writeTimeout();
    EHttp.init(builder);
```

（8）自定义ApiResult
-
>android开发中，服务端经常返回的是一个json类型字符串，且可以转换成具有一定格式的Java Bean对象，例如下面格式：
```java
public  class CommonResult<T>{
    private int code;
    private String message;
    private T data;
}
```
>T是我们动态传入的数据类型，它也是我们所关心的数据（data，code和message变量名也可以是其他的字符），如果想在HttpCallback中返回T类型的数据，而不是CommonResult<T>类型，那么EHttp也是可以实现的，只需要让你的CommonResult<T>继承自ApiResult<T>，实现对应的抽象方法即可：
```java
public  class CommonResult<T> extends ApiResult<T>{
    private int code;
    private String message;
    private T data;
    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public String getMsg() {
        return message;
    }
    public void setMessage(String msg) {
        this.message = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public boolean isResultOk() {
        return code == 0;
    }
}
```
>最后将这个CommonResult.class类型设置为全局参数，或者某个ApiCallback的参数，比如：这里的T是一个UserInfo类型，为了得到CommonResult中的UserInfo对象，可以像下面这样：
```java
//全局设置，所有请求起作用
EHttp.Builder builder=new EHttp.Builder();
        builder.apiResult(CommonResult.class);
        EHttp.init(builder);
//也可以像下面这样设置，只是当前请求起作用
EHttp.post(tag,url,request, new ApiCallback<UserInfo>(CommonResult.class) {
            @Override
            public void onFailure(Throwable e) {
               
            }
            @Override
            public void onSuccess(UserInfo response) {
               
            }
    });
 ```
（9）自定义Retrofit风格Api
-
>如果你想使用Retrofit风格的Api配置你的请求参数，那么EHttp还支持自定义api
```java
//自定义api
public interface MyApi {
  @GET("url")
  Observable<ResponseBody> getdata(@QueryMap Map<String, String> maps);
 }
//调用execute执行网络请求
MyApi myApi=EHttp.create(MyApi.class);
EHttp.execute(tag,myApi.getdata(maps),callback);
```

联系方式
-
* Email：2792889279@qq.com
* qq： 2792889279

Licenses
-
        
        Copyright 2018 wbz360(王宝忠)

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

         　　　　http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.






