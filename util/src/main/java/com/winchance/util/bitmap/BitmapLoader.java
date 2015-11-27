package com.winchance.util.bitmap;

import android.content.Context;
import android.database.Observable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BitmapLoader {
    private static final String TAG = "BitmapLoader";

    private final BitmapCache mBitmapCache;

    private final ExecutorService mImageThreadPool = Executors.newFixedThreadPool(2);

    private final Map<String, Future> mTaskMap = new HashMap<>();

    private final Handler mHandler = new BitmapLoaderHandler(new WeakReference<>(this));

    private final  BitmapLoaderOvservable mBitmapLoaderOvservable = new BitmapLoaderOvservable();

    public abstract class BitmapLoaderObserver {
        public void onLoadEnd(String url) {}
    }

    public BitmapLoader(Context context){
        mBitmapCache = new BitmapCache(context);
    }

    public void registerBitmapLoaderObserver(BitmapLoaderObserver bitmapLoadObserver) {
        mBitmapLoaderOvservable.registerObserver(bitmapLoadObserver);
    }

    public void unregisterObserver(BitmapLoaderObserver bitmapLoadObserver) {
        mBitmapLoaderOvservable.unregisterObserver(bitmapLoadObserver);
    }

    public void unregisterAll() {
        mBitmapLoaderOvservable.unregisterAll();
    }

    public Bitmap get(String url) {
        String realName = getRealName(url);
        Bitmap bitmap = mBitmapCache.get(realName);

        Log.d(TAG, "get, url = " + url + ", realName = " + realName + ", result = " + bitmap);
        return bitmap;
    }

    public boolean contain(String url) {
        String realName = getRealName(url);
        boolean result = mBitmapCache.containsKey(realName);

        Log.d(TAG, "contain, url = " + url + ", realName = " + realName + ", result = " + result);
        return result;
    }

    public synchronized void download(final String url){
        mTaskMap.put(url, mImageThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = getBitmapFormUrl(url);
                Log.d(TAG, "download, end downloading, url = " + url + ", result = " + bitmap);

                if (null != bitmap)
                    mBitmapCache.put(getRealName(url), bitmap);

                Message message = mHandler.obtainMessage();
                message.obj = url;
                mHandler.sendMessage(message);
                Log.d(TAG, "download, send message, url = " + url + ", message = " + message);
            }
        }));

        Log.d(TAG, "download, start to download, url = " + url);
    }

    public synchronized boolean cancel(String url) {
        if (!mTaskMap.containsKey(url)) {
            Log.e(TAG, "cancel, can not find url in map, url = " + url);
            return false;
        }

        Future future = mTaskMap.remove(url);
        if (future.isDone()) {
            Log.d(TAG, "cancel, task is done, url = " + url + ", task = " + future);
            return false;
        }

        boolean result = future.cancel(true);
        Log.d(TAG, "cancel, url = " + url + ", task = " + future + ", result = " + result);
        return result;
    }

    private Bitmap getBitmapFormUrl(String url) {
        Bitmap bitmap = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(10 * 1000);
            connection.setReadTimeout(10 * 1000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
        } catch (Exception ignore) {
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
        return bitmap;
    }

    private String getRealName(String url) {
        return url.replaceAll("[^\\w]", "");
    }

    private static class BitmapLoaderHandler extends Handler {
        private final WeakReference<BitmapLoader> bitmapLoaderWeakReference;

        private BitmapLoaderHandler(WeakReference<BitmapLoader> bitmapLoaderWeakReference) {
            this.bitmapLoaderWeakReference = bitmapLoaderWeakReference;
        }

        /**
         * Get the msg pass from download(). If the url which is already download still contains in TaskMap, delete it.
         * Only the task that still not been download can save in TaskMap.
         * @see {@link BitmapLoader}
         * @param msg 从download方法中传过来的消息
         */
        @Override
        public void handleMessage(Message msg) {
            String url = (String) msg.obj;

            BitmapLoader bitmapLoader = bitmapLoaderWeakReference.get();
            if (null != bitmapLoader) {
                synchronized (bitmapLoader) {
                    if (bitmapLoader.mTaskMap.containsKey(url)) {
                        bitmapLoader.mTaskMap.remove(url);
                    }
                }
                bitmapLoader.mBitmapLoaderOvservable.notifyLoadEnd(url);
            }
        }
    }

    /**
     *
     */
    private class BitmapLoaderOvservable extends Observable<BitmapLoaderObserver> {
        public void notifyLoadEnd(String url) {
            synchronized(mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onLoadEnd(url);
                }
            }
        }
    }
}
