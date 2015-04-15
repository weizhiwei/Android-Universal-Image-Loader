package com.nostra13.example.universalimageloader;

import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.BaseAdapter;

import com.wzw.ic.mvc.ViewNode;

import java.util.HashMap;
import java.util.Map;

class GetDataTask extends AsyncTask<Object, Integer, Void> {

	public interface GetDataTaskFinishedListener {
		public void onGetDataTaskFinished(ViewNode model);
	}
	
	protected ViewNode model;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected BaseAdapter itemAdapter;
	protected PagerAdapter pagerAdapter;
	protected GetDataTaskFinishedListener listener;

    private static Map<ViewNode, GetDataTask> reentrantLocks = new HashMap<ViewNode, GetDataTask>();

    protected void init(ViewNode model,
                        SwipeRefreshLayout swipeRefreshLayout,
                        BaseAdapter itemAdapter,
                        PagerAdapter pagerAdapter,
                        GetDataTaskFinishedListener listener,
                        boolean reload) {
        this.model = model;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.itemAdapter = itemAdapter;
        this.pagerAdapter = pagerAdapter;
        this.listener = listener;
        if (!reentrantLocks.containsKey(model)) {
            reentrantLocks.put(model, this);
            this.execute(reload);
        }
    }

	public GetDataTask(ViewNode model,
			SwipeRefreshLayout swipeRefreshLayout,
			BaseAdapter itemAdapter,
			GetDataTaskFinishedListener listener,
            boolean reload) {
		init(model, swipeRefreshLayout, itemAdapter, null, listener, reload);
	}
	
	public GetDataTask(ViewNode model,
			PagerAdapter pagerAdapter,
			GetDataTaskFinishedListener listener,
            boolean reload) {
        init(model, null, null, pagerAdapter, listener, reload);
	}
	
	@Override
	protected void onPreExecute() {
		model.detach();
		if (null != swipeRefreshLayout) {
			swipeRefreshLayout.setRefreshing(true);
		}
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		// Simulates a background job.
		boolean reload = (Boolean) params[0];
		if (reload) {
            publishProgress(-1);
			model.reload();
		} else {
			model.loadOneMorePage();
		}
		return null;
	}

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (-1 == progress[0]) { // simulates a clear before adding the results
            model.clearDetachment();
            if (null != itemAdapter) {
                itemAdapter.notifyDataSetChanged();
            }
            if (null != pagerAdapter) {
                pagerAdapter.notifyDataSetChanged();
            }
        }
    }

	@Override
	protected void onCancelled(Void result) {
		onPostExecute(result);
	}
	
	@Override
	protected void onPostExecute(Void result) {
		model.attach();
		if (null != itemAdapter) {
			itemAdapter.notifyDataSetChanged();
		}
		if (null != pagerAdapter) {
			pagerAdapter.notifyDataSetChanged();
		}
		if (null != listener) {
			listener.onGetDataTaskFinished(model);
		}
		if (null != swipeRefreshLayout) {
			swipeRefreshLayout.setRefreshing(false);
		}

        reentrantLocks.remove(model);
	}
}
