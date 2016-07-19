
package com.lizubing.smartcacheforretrofit2.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lizubing.smartcache.BasicCaching;
import com.lizubing.smartcache.SmartCallFactory;
import com.lizubing.smartcacheforretrofit2.MyApplication;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 无缓存get post请求
 * 支持Rxjava Observable
 */
public class MainFactory {
    public static final String HOST = "http://www.tngou.net/";

    private static MeoHttp mGuDong;

    protected static final Object monitor = new Object();

    public static MeoHttp getInstance(){
        synchronized (monitor){
            if(mGuDong==null){
                Gson gson = new GsonBuilder()
                        .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                        .create();
                SmartCallFactory smartFactory = new SmartCallFactory(BasicCaching.fromCtx(MyApplication.getContext()));
                //实现拦截器，设置请求头
                Interceptor interceptorImpl = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request compressedRequest = request.newBuilder()
                                .header("X-Requested-With", "XMLHttpRequest")
                                .build();
                        return chain.proceed(compressedRequest);
                    }
                };
                //设置OKHttpClient
                OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .addInterceptor(interceptorImpl);//创建OKHttpClient的Builder
                //build OKHttpClient
                OkHttpClient okHttpClient = httpClientBuilder.build();
                Retrofit client = new Retrofit.Builder()
                        .baseUrl(HOST)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .addCallAdapterFactory(smartFactory)
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
                mGuDong = client.create(MeoHttp.class);
            }
            return mGuDong;
        }
    }
}
