package com.lizubing.smartcacheforretrofit2.retrofit;


import com.lizubing.smartcache.SmartCall;

import retrofit2.http.GET;

public interface MeoHttp {

    /**
     * 获得图片列表
     */
    @GET("tnfs/api/list")
    SmartCall<ImageListBean> getImageList();

}
