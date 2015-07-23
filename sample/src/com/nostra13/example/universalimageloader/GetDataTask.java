package com.nostra13.example.universalimageloader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.widget.BaseAdapter;

import com.wzw.ic.mvc.ViewNode;

import java.util.HashMap;
import java.util.Map;

class GetDataTask {

	public interface GetDataTaskFinishedListener {
		public void onGetDataTaskFinished(ViewNode model);
	}

    protected Context context;
	protected ViewNode model;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected BaseAdapter itemAdapter;
    protected RecyclerView.Adapter recyclerViewAdapter;
	protected PagerAdapter pagerAdapter;
	protected GetDataTaskFinishedListener listener;

    protected void init(Context context,
                        ViewNode model,
                        SwipeRefreshLayout swipeRefreshLayout,
                        BaseAdapter itemAdapter,
                        RecyclerView.Adapter recyclerViewAdapter,
                        PagerAdapter pagerAdapter,
                        GetDataTaskFinishedListener listener,
                        boolean reload) {
        this.context = context;
        this.model = model;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.itemAdapter = itemAdapter;
        this.recyclerViewAdapter = recyclerViewAdapter;
        this.pagerAdapter = pagerAdapter;
        this.listener = listener;

        model.detach();
        if (null != swipeRefreshLayout) {
            swipeRefreshLayout.setRefreshing(true);
        }

        model.load(context, reload, new ViewNode.LoadListener() {
            @Override
            public void onLoadDone(ViewNode model) {
                model.attach();
                if (null != GetDataTask.this.itemAdapter) {
                    GetDataTask.this.itemAdapter.notifyDataSetChanged();
                }
                if (null != GetDataTask.this.recyclerViewAdapter) {
                    GetDataTask.this.recyclerViewAdapter.notifyDataSetChanged();
                }
                if (null != GetDataTask.this.pagerAdapter) {
                    GetDataTask.this.pagerAdapter.notifyDataSetChanged();
                }
                if (null != GetDataTask.this.listener) {
                    GetDataTask.this.listener.onGetDataTaskFinished(model);
                }
                if (null != GetDataTask.this.swipeRefreshLayout) {
                    GetDataTask.this.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

	public GetDataTask(Context context,
            ViewNode model,
			SwipeRefreshLayout swipeRefreshLayout,
			BaseAdapter itemAdapter,
            RecyclerView.Adapter recyclerViewAdapter,
			GetDataTaskFinishedListener listener,
            boolean reload) {
		init(context, model, swipeRefreshLayout, itemAdapter, recyclerViewAdapter, null, listener, reload);
	}
	
	public GetDataTask(Context context,
            ViewNode model,
			PagerAdapter pagerAdapter,
			GetDataTaskFinishedListener listener,
            boolean reload) {
        init(context, model, null, null, null, pagerAdapter, listener, reload);
	}
}
