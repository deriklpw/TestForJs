package com.derik.test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by derik on 18-8-25
 * <p>
 * Email: weilai0314@163.com
 */
public class OkHttpClientUtils {
    private static OkHttpClient client;

    private OkHttpClientUtils() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
//                .addInterceptor() //应用拦截器，请求过程即使重定向，只会调用一次拦截器
                .addNetworkInterceptor(new HeaderInterceptor()) //网络拦截器，每次请求，包括重定向，都会调用
                .build();
    }

    public static OkHttpClientUtils getClient() {
        return new OkHttpClientUtils();
    }

    public void get(String url, Callback callback) {
        client.newCall(new Request.Builder().url(url).build()).enqueue(callback);
    }


    public void postFormBody(String url, FormBody formBody, Callback callback) {
        client.newCall(new Request.Builder().url(url).post(formBody).build()).enqueue(callback);
    }

    //把RequestBody暴露给外面，按需设置
    public void postRequsetBody(String url, RequestBody requestBody, Callback callback) {
        client.newCall(new Request.Builder().url(url).post(requestBody).build()).enqueue(callback);
    }

    //RequestBody用来发送Json数据
    public void postJson(String url, String json, Callback callback) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        client.newCall(new Request.Builder().url(url).post(requestBody).build()).enqueue(callback);
    }

    //RequestBody用来发送单个文件
    public void postFile(String url, File file, Callback callback){
        RequestBody requestBody = RequestBody.create(MediaType.parse("file/*"), file);
        client.newCall(new Request.Builder().url(url).post(requestBody).build()).enqueue(callback);
    }

    /**
     * 处理请求头用的拦截器
     */
    public class HeaderInterceptor implements Interceptor{
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request newRequest = originalRequest.newBuilder()
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("Accept-Charset", "utf-8")
                    .build();

            return chain.proceed(newRequest);
        }
    }

    /**
     * 加密用的拦截器
     */
    /*public class EncryptInterceptor implements Interceptor{
        private static final String FORM_NAME = "content";
        private static final String CHARSET = "UTF-8";
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            RequestBody body = request.body();
            if (body instanceof FormBody) {
                FormBody formBody = (FormBody) body;
                Map<String, String> formMap = new HashMap<>();
                // 从 formBody 中拿到请求参数，放入 formMap 中
                for (int i = 0; i < formBody.size(); i++) {
                    formMap.put(formBody.name(i), formBody.value(i));
                }
                // 将 formMap 转化为 json 然后 AES 加密
                Gson gson = new Gson();
                String jsonParams = gson.toJson(formMap);
                String encryptParams = null;
                try {
                    encryptParams = new String(AESCryptUtils.encrypt(jsonParams, "derik2018"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 重新修改 body 的内容
                body = new FormBody.Builder().add(FORM_NAME, encryptParams).build();
            }
            if (body != null) {
                request = request.newBuilder()
                        .post(body)
                        .build();
            }
            return chain.proceed(request);
        }
    }*/

}