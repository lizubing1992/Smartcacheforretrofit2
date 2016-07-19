package com.lizubing.smartcacheforretrofit2;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by xing on 2016/7/19.
 */
public class MyApplication extends Application{

    private static MyApplication appcontext = null;
    public void onCreate() {
        super.onCreate();
        appcontext = this;
        Fresco.initialize(appcontext, ConfigConstants.getImagePipelineConfig(appcontext));
    }

    // 单例模式
    public static MyApplication getInstance() {
        return appcontext;
    }
    public static Context getContext() {
        return appcontext;
    }
}
