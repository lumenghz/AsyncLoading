package com.winchance.util.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.winchance.util.FileUtil;

public class BitmapCache {
    public static final String DEFAULT_FOLDER = "BitmapCache";

    public static final int DEFAULT_CACHE_SIZE = (int) Runtime.getRuntime().maxMemory() / 8;

    private final LruCache<String, Bitmap> mMemoryCache;

    private final FileUtil mFileUtil;

    public BitmapCache(Context context) {
        this(context, DEFAULT_FOLDER, DEFAULT_CACHE_SIZE);
    }

    public BitmapCache(Context context, String folder) {
        this(context, folder, DEFAULT_CACHE_SIZE);
    }

    public BitmapCache(Context context, int cacheSize){
        this(context, DEFAULT_FOLDER, cacheSize);
    }

    public BitmapCache(Context context, String folder, int cacheSize) {
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
                oldValue.recycle();
            }
        };

        mFileUtil = new FileUtil(context, folder);
    }

    public synchronized void put(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
        try {
            mFileUtil.savaBitmap(key, bitmap);
        } catch (Exception ignore) {
        }
    }

    public synchronized Bitmap get(String key) {
        Bitmap bitmap = mMemoryCache.get(key);
        if (bitmap != null)
            return bitmap;
        if (mFileUtil.exists(key) && mFileUtil.getLength(key) != 0)
            return mFileUtil.getBitmap(key);
        return null;
    }

    public synchronized boolean containsKey(String key) {
        return null != mMemoryCache.get(key) || (mFileUtil.exists(key) && 0 != mFileUtil.getLength(key));
    }
}
