package com.winchance.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

public class AsyncLoadingListView extends ListView {
    private static final String TAG = "AsyncLoadingListView";

    public class OnScrollListenerImpl implements OnScrollListener {
        private int firstVisibleItem, visibleItemCount, lastVisibleItem = -1;

        private int scrollState;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            Log.d(TAG, "onScroll, nowFirstVisibleItem = " + firstVisibleItem + ", nowVisibleItemCount = " + visibleItemCount +
                       ", lastFirstVisibleItem = " + this.firstVisibleItem + ", lastVisibleItemCount = " + this.visibleItemCount);

            if (this.firstVisibleItem == firstVisibleItem && this.visibleItemCount == visibleItemCount)
                return;

            ListAdapter adapter = getAdapter();
            if (null == adapter)
                return;
            if (!(adapter instanceof AsyncLoadingListAdapter))
                return;
            AsyncLoadingListAdapter asyncLoadingListAdapter = (AsyncLoadingListAdapter) adapter;

            if (OnScrollListener.SCROLL_STATE_FLING != scrollState) {
                if (firstVisibleItem != this.firstVisibleItem) {
                    if (firstVisibleItem > this.firstVisibleItem) {
                        for (int i = this.firstVisibleItem; i < firstVisibleItem; i++) {
                            asyncLoadingListAdapter.onExit(i);
                            onExit(view, i);
                        }
                    } else {
                        for (int i = this.firstVisibleItem - 1; i >= firstVisibleItem; i--) {
                            asyncLoadingListAdapter.onEnter(i);
                            onEnter(view, i);
                        }
                    }
                }

                int lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
                if (lastVisibleItem != this.lastVisibleItem) {
                    if (lastVisibleItem > this.lastVisibleItem) {
                        for (int i = this.lastVisibleItem + 1; i <= lastVisibleItem; i++) {
                            asyncLoadingListAdapter.onEnter(i);
                            onEnter(view, i);
                        }
                    } else {
                        for (int i = this.lastVisibleItem; i > lastVisibleItem; i--) {
                            asyncLoadingListAdapter.onExit(i);
                            onExit(view, i);
                        }
                    }
                }
            }

            this.firstVisibleItem = firstVisibleItem;
            this.visibleItemCount = visibleItemCount;
            this.lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.d(TAG, "onScrollStateChanged, nowState = " + scrollState + ", lastState = " + this.scrollState);

            ListAdapter adapter = getAdapter();
            if (null == adapter)
                return;
            if (!(adapter instanceof AsyncLoadingListAdapter))
                return;
            AsyncLoadingListAdapter asyncLoadingListAdapter = (AsyncLoadingListAdapter) adapter;

            if (OnScrollListener.SCROLL_STATE_FLING == scrollState) {
                asyncLoadingListAdapter.onCancel();
            } else if (OnScrollListener.SCROLL_STATE_FLING == this.scrollState) {
                for (int position = this.firstVisibleItem; position <= this.lastVisibleItem; position++) {
                    asyncLoadingListAdapter.onEnter(position);
                }
            }
            this.scrollState = scrollState;
        }

        public void onEnter(AbsListView view, int position) {}

        public void onExit(AbsListView view, int position) {}
    }

    public AsyncLoadingListView(Context context) {
        super(context);
    }

    public AsyncLoadingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AsyncLoadingListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
