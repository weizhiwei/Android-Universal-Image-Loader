package com.nostra13.example.universalimageloader;

import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.BaseAdapter;

import com.wzw.ic.mvc.ViewNode;

class GetDataTask extends AsyncTask<Object, Void, Void> {

	public interface GetDataTaskFinishedListener {
		public void onGetDataTaskFinished(ViewNode model);
	}
	
	protected ViewNode model;
	protected SwipeRefreshLayout swipeRefreshLayout;
	protected BaseAdapter itemAdapter;
	protected PagerAdapter pagerAdapter;
	protected GetDataTaskFinishedListener listener;
	
	public GetDataTask(ViewNode model,
			SwipeRefreshLayout swipeRefreshLayout,
			BaseAdapter itemAdapter,
			GetDataTaskFinishedListener listener) {
		this.model = model;
		this.swipeRefreshLayout = swipeRefreshLayout;
		this.itemAdapter = itemAdapter;
		this.listener = listener;
	}
	
	public GetDataTask(ViewNode model,
			PagerAdapter pagerAdapter,
			GetDataTaskFinishedListener listener) {
		this.model = model;
		this.pagerAdapter = pagerAdapter;
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		if (null != swipeRefreshLayout) {
			swipeRefreshLayout.setRefreshing(true);
		}
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		// Simulates a background job.
		boolean reload = (Boolean) params[0];
		if (reload) {
			model.reload();
		} else {
			model.loadOneMorePage();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		if (null != swipeRefreshLayout) {
			swipeRefreshLayout.setRefreshing(false);
		}
		if (null != itemAdapter) {
			itemAdapter.notifyDataSetChanged();
		}
		if (null != pagerAdapter) {
			pagerAdapter.notifyDataSetChanged();
		}
		if (null != listener) {
			listener.onGetDataTaskFinished(model);
		}
		// Call onRefreshComplete when the list has been refreshed.
		super.onPostExecute(result);
	}
}
