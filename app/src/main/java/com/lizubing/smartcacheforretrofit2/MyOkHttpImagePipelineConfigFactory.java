package com.lizubing.smartcacheforretrofit2;

import android.content.Context;

import com.facebook.imagepipeline.core.ImagePipelineConfig;

import okhttp3.OkHttpClient;

/**
 * Created by admin on 2016/1/8.
 * <p>
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
public class MyOkHttpImagePipelineConfigFactory {

    public static ImagePipelineConfig.Builder newBuilder(Context context, OkHttpClient okHttpClient) {
        return ImagePipelineConfig.newBuilder(context).setNetworkFetcher(new MyOkHttpNetworkFetcher(okHttpClient));
    }

}
