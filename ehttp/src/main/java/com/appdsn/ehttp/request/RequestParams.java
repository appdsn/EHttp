package com.appdsn.ehttp.request;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by baozhong 2018/03/19
 */
public class RequestParams {
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public final static String APPLICATION_JSON = "application/json";
    protected ConcurrentHashMap<String, String> urlParams = new ConcurrentHashMap<String, String>();
    protected ConcurrentHashMap<String, String> formParams = new ConcurrentHashMap<String, String>();
    protected ConcurrentHashMap<String, BytesInfo> bytesParams = new ConcurrentHashMap<String, BytesInfo>();
    protected ConcurrentHashMap<String, FileInfo> fileParams = new ConcurrentHashMap<String, FileInfo>();
    protected ConcurrentHashMap<String, String> headerParams = new ConcurrentHashMap<String, String>();
    protected Object requestBody = null;//单个自定义的body实体上传，比如Json字符串，单个文件。

    public RequestParams() {

    }

    /*添加请求头信息*/
    public void addHeaderParams(String name, String value) {
        headerParams.put(name, value);
    }

    //url拼接
    public void addUrlParams(String key, String value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    /*自定义实体，比如Json字符串，各种自定义的RequestBody*/
    public void addBodyParams(String stringBody) {
        this.requestBody = stringBody;
    }

    public void addBodyParams(File fileBody) {
        this.requestBody = fileBody;
    }

    public void addBodyParams(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public void addFormParams(String key, String value) {
        if (key != null && value != null) {
            formParams.put(key, value);
        }
    }

    public void addFileParams(String key, String fileName, String contentType, File file
    ) {
        if (file != null && !file.exists() && key != null) {
            fileParams.put(key, new FileInfo(file, contentType,
                    fileName));
        }
    }

    public void addStreamParams(String key, String name, String contentType, byte[] bytes) {
        if (key != null && bytes != null) {
            bytesParams.put(key, new BytesInfo(bytes, name, contentType));
        }
    }

    public RequestBody getRequestBody() {
        if (!bytesParams.isEmpty() || !fileParams.isEmpty()) {
            return createMultipartBody();
        } else if (!formParams.isEmpty()) {
            return createFormBody();
        } else if (requestBody != null) {
            return createCustomBody();
        }
        return null;
    }

    public Map<String, String> getHeader() {
        return headerParams;
    }

    public Map<String, String> getUrlParams() {
        return urlParams;
    }

    /* 如果是一串字符，可以这样 */
    public static RequestBody createStringBody(String contentType, String content) {
        if (contentType == null) {
            contentType = "application/json;charset=utf-8";
        }

        return RequestBody.create(MediaType.parse(contentType), content);
    }

    /* 如果只有一个文件，可以这样 */
    public static RequestBody createFileBody(String contentType, File file) {
        if (contentType == null) {
            contentType = "application/octet-stream;charset=utf-8";
        }
        return RequestBody.create(MediaType.parse(contentType), file);
    }

    /*自定义实体上传*/
    private RequestBody createCustomBody() {
        if (requestBody instanceof RequestBody) {
            return (RequestBody) requestBody;
        } else if (requestBody instanceof String) {
            return createStringBody(null, (String) requestBody);
        } else if (requestBody instanceof File) {
            return createFileBody(null, (File) requestBody);
        }
        return null;
    }

    /*表单上传*/
    private RequestBody createFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : urlParams.keySet()) {
            builder.add(key, urlParams.get(key));
        }
        return builder.build();
    }

    /*表单，文件等混合上传*/
    private RequestBody createMultipartBody() {
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        // Add string params
        for (ConcurrentHashMap.Entry<String, String> entry : formParams
                .entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        // Add stream params
        for (ConcurrentHashMap.Entry<String, BytesInfo> entry : bytesParams
                .entrySet()) {
            BytesInfo bytesInfo = entry.getValue();
            if (bytesInfo.bytes != null) {
                RequestBody streamBody = RequestBody.create(
                        MediaType.parse(bytesInfo.contentType),
                        bytesInfo.bytes);
                builder.addFormDataPart(entry.getKey(), bytesInfo.name,
                        streamBody);
            }
        }
        // Add file params
        for (ConcurrentHashMap.Entry<String, FileInfo> entry : fileParams
                .entrySet()) {
            FileInfo fileInfo = entry.getValue();
            RequestBody fileBody = RequestBody.create(
                    MediaType.parse(fileInfo.contentType), fileInfo.file);
            builder.addFormDataPart(entry.getKey(), fileInfo.customFileName,
                    fileBody);
        }
        return builder.build();
    }

    public String getUrlWithParams(String url) {
        if (url == null) {
            return null;
        }
        final StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : urlParams
                .entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            if (entry.getValue() != null) {
                result.append("=");
                result.append(entry.getValue());
            }
        }

        if (!result.equals("") && !result.equals("?")) {
            url += url.contains("?") ? "&" : "?";
            url += result.toString();
        }
        return url;

    }

    public static class FileInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public final File file;
        public final String contentType;
        public final String customFileName;

        public FileInfo(File file, String contentType, String customFileName) {
            this.file = file;
            this.contentType = contentType == null ? APPLICATION_OCTET_STREAM
                    : contentType;
            this.customFileName = customFileName;
        }
    }

    public static class BytesInfo {
        public final byte[] bytes;
        public final String name;
        public final String contentType;

        public BytesInfo(byte[] bytes, String name, String contentType) {
            this.bytes = bytes;
            this.name = name;
            this.contentType = contentType == null ? APPLICATION_OCTET_STREAM
                    : contentType;

        }

    }
}
