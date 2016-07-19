package com.lizubing.smartcacheforretrofit2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.lizubing.smartcacheforretrofit2.retrofit.ImageListBean;
import com.lizubing.smartcacheforretrofit2.retrofit.MainFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ImageListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        MainFactory.getInstance().getImageList().enqueue(new Callback<ImageListBean>() {
            @Override
            public void onResponse(Call<ImageListBean> call, Response<ImageListBean> response) {
                adapter.setData(response.body().getTngou());
            }

            @Override
            public void onFailure(Call<ImageListBean> call, Throwable t) {

            }
        });
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.list);
        adapter = new ImageListAdapter();
        listView.setAdapter(adapter);
    }


}
