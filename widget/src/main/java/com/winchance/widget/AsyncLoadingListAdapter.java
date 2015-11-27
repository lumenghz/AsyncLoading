package com.winchance.widget;

import android.content.Context;
import android.util.Log;
import android.widget.BaseAdapter;

import com.winchance.util.bitmap.BitmapLoader;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AsyncLoadingListAdapter extends BaseAdapter {
    private static final String TAG = "AsyncLoadingListAdapter";

    protected final BitmapLoader mBitmapLoader;

    private final Map<String, ImageResource> mUrlImageResourceMap = new HashMap<>();

    public abstract Collection<ImageResource> getImageResources(int position);

    public abstract void onLoadEnd(ImageResource imageResource);

    public AsyncLoadingListAdapter(Context context) {
        mBitmapLoader = new BitmapLoader(context);
        mBitmapLoader.registerBitmapLoaderObserver(mBitmapLoader.new BitmapLoaderObserver() {
            @Override
            public void onLoadEnd(String url) {
                Log.d(TAG, "onLoadEnd, url = " + url);

                ImageResource imageResource = mUrlImageResourceMap.get(url);
                if (!mBitmapLoader.contain(url)) {
                    int loadState = imageResource.getLoadState();
                    if (ImageResource.LOAD_STATE_LOADING == loadState)
                        imageResource.setLoadState(ImageResource.LOAD_STATE_ERROR);

                    Log.d(TAG, "onLoadEnd, imageResource loading has been canceled or error occur, url = " + url + ", lastState = " + loadState);
                } else {
                    imageResource.setLoadState(ImageResource.LOAD_STATE_LOADED);

                    Log.d(TAG, "onLoadEnd, imageResource loading complete, url = " + url);
                }
                AsyncLoadingListAdapter.this.onLoadEnd(imageResource);
            }
        });
    }

    public void onEnter(int position) {
        Log.d(TAG, "onEnter, position = " + position);

        for (ImageResource imageResource : getImageResources(position)) {
            String url = imageResource.getUrl();
            if (mBitmapLoader.contain(url)) {
                imageResource.setLoadState(ImageResource.LOAD_STATE_LOADED);

                Log.d(TAG, "onEnter, imageResource has been loaded, position = " + position + ", url = " + url);
                continue;
            }

            int loadState = imageResource.getLoadState();
            if (ImageResource.LOAD_STATE_INIT == loadState || ImageResource.LOAD_STATE_CANCEL == loadState || ImageResource.LOAD_STATE_ERROR == loadState) {
                imageResource.setLoadState(ImageResource.LOAD_STATE_LOADING);

                mBitmapLoader.download(url);
                if (!mUrlImageResourceMap.containsKey(url))
                    mUrlImageResourceMap.put(url, imageResource);

                Log.d(TAG, "onEnter, start to load imageResource, position = " + position + ", url = " + url);
            }
        }
    }

    public void onExit(int position) {
        Log.d(TAG, "onExit, position = " + position);

        for (ImageResource imageResource : getImageResources(position)) {
            cancelLoading(imageResource);
        }
    }

    public void onCancel() {
        Log.d(TAG, "onCancel");

        for (ImageResource imageResource : mUrlImageResourceMap.values()) {
            cancelLoading(imageResource);
        }
    }

    private void cancelLoading(ImageResource imageResource) {
        int position = imageResource.getPosition();
        String url = imageResource.getUrl();
        if (ImageResource.LOAD_STATE_LOADING ==  imageResource.getLoadState()) {
            imageResource.setLoadState(ImageResource.LOAD_STATE_CANCEL);

            mBitmapLoader.cancel(url);

            Log.d(TAG, "cancelLoading, cancel loading imageResource, position = " + position + ", url = " + url);
        }
    }

    public static class ImageResource {
        public static int LOAD_STATE_INIT = 0;

        public static int LOAD_STATE_LOADING = 1;

        public static int LOAD_STATE_LOADED = 2;

        public static int LOAD_STATE_CANCEL = 3;

        public static int LOAD_STATE_ERROR = 4;

        private int loadState = LOAD_STATE_INIT;

        private int position;

        private String url;

        public int getLoadState() {
            return loadState;
        }

        public void setLoadState(int loadState) {
            this.loadState = loadState;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
