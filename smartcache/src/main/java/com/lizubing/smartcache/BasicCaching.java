package com.lizubing.smartcache;

import android.content.Context;
import android.util.Log;
import android.util.LruCache;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.CharStreams;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import okhttp3.Request;
import retrofit2.Response;


/**
 * A basic caching system that stores responses in RAM & disk
 * It uses {@link DiskLruCache} and {@link LruCache} to do the former.
 */
public class BasicCaching implements CachingSystem {
    private final String PACKAGE_CACHE_DIR = "cache";
    private final Context context;
    private List<String> packageCaches;
    private DiskLruCache diskCache;
    private LruCache<String, Object> memoryCache;

    public BasicCaching(Context context, File diskDirectory, long maxDiskSize, int memoryEntries){
        this.context = context;
        try {
            packageCaches = Lists.newArrayList(context.getAssets().list(PACKAGE_CACHE_DIR));
        } catch (IOException e) {
            packageCaches = Lists.newArrayList();
        }

        try{
            diskCache = DiskLruCache.open(diskDirectory, 1, 1, maxDiskSize);
        }catch(IOException exc){
            Log.e("SmartCall", "", exc);
            diskCache = null;
        }

        memoryCache = new LruCache<>(memoryEntries);
    }

    private static final long REASONABLE_DISK_SIZE = 1024 * 1024; // 1 MB
    private static final int REASONABLE_MEM_ENTRIES = 50; // 50 entries

    /***
     * Constructs a BasicCaching system using settings that should work for everyone
     * @param context
     * @return
     */
    public static BasicCaching fromCtx(Context context){
        return new BasicCaching(
                context,
                new File(context.getCacheDir(), "retrofit_smartcache"),
                REASONABLE_DISK_SIZE,
                REASONABLE_MEM_ENTRIES);
    }

    @Override
    public <T> void addInCache(Response<T> response, byte[] rawResponse) {
        String cacheKey = urlToKey(response.raw().request().url().url());
        memoryCache.put(cacheKey, rawResponse);

        try {
            DiskLruCache.Editor editor = diskCache.edit(urlToKey(response.raw().request().url().url()));
            editor.set(0, new String(rawResponse, Charset.defaultCharset()));
            editor.commit();
        }catch(IOException exc){
            Log.e("SmartCall", "", exc);
        }
    }

    @Override
    public <T> byte[] getFromCache(Request request) {
        String cacheKey = urlToKey(request.url().url());
        byte[] memoryResponse = (byte[]) memoryCache.get(cacheKey);
        if(memoryResponse != null){
            Log.d("SmartCall", "Memory hit!");
            return memoryResponse;
        }

        try {
            DiskLruCache.Snapshot cacheSnapshot = diskCache.get(cacheKey);
            if(cacheSnapshot != null){
                Log.d("SmartCall", "Disk hit!");
                return cacheSnapshot.getString(0).getBytes();
            }
        }catch(IOException exc){
            // ignore
        }

        if (packageCaches.contains(cacheKey)) {
            try {
                String packageCache = CharStreams.toString(
                        new InputStreamReader(context.getAssets().open(PACKAGE_CACHE_DIR + "/" + cacheKey)));
                Log.d("SmartCall", "Package hit!");
                return packageCache.getBytes();
            } catch (IOException e) {
                // ignore
            }
        }

        return null;
    }

    private String urlToKey(URL url){
        return Hashing.sha1().hashString(url.toString(), Charset.defaultCharset()).toString();
    }
}
