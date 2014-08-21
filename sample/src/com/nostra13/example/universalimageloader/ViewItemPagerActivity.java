package com.nostra13.example.universalimageloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeAction;

public class ViewItemPagerActivity extends BaseActivity {

	public static final int VIEW_TYPE_LIST = 1;
	public static final int VIEW_TYPE_GRID = 2;
	
	DisplayImageOptions options;
	ViewPager pager;
	
	ViewNode subModel;
	BaseAdapter subAdapter;
	PullToRefreshBase subPullRefreshView;
	AbsListView subAbsListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);
		
		setModelControllerFromIntent();
		
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();

		pager = (ViewPager) findViewById(R.id.ic_pagerview);
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(new ViewItemPagerAdapter());
		pager.setCurrentItem(pagerPosition);
		
		setFullscreen(true);
	}	
	
	private class ViewItemPagerAdapter extends PagerAdapter {
		
		ViewItemPagerAdapter() {
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return null == model.getViewItems() ? 0 : model.getViewItems().size();
		}

		@Override
	    public void setPrimaryItem(ViewGroup container, int position, Object object) {
	        super.setPrimaryItem(container, position, object);
	        
	        View contentView = (View) object;
	        ViewItem viewItem = model.getViewItems().get(position);
	        setTitleIconFromViewItem(viewItem);
	        
	        switch (viewItem.getViewType()) {
			case VIEW_TYPE_LIST:
				subPullRefreshView = (PullToRefreshListView) contentView.findViewById(R.id.ic_listview);
				break;
			case VIEW_TYPE_GRID:
				subPullRefreshView = (PullToRefreshGridView) contentView.findViewById(R.id.ic_gridview);
				break;
			default:
				break;
			}
	        subAdapter = (BaseAdapter) ((AbsListView)subPullRefreshView.getRefreshableView()).getAdapter();
	    }
		
		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			ViewItem viewItem = model.getViewItems().get(position);
			ViewNode childModel = null;
			
			View contentView = null;
			PullToRefreshBase pullRefreshView = null;
			BaseAdapter itemAdapter = null;
			switch (viewItem.getViewType()) {
			case VIEW_TYPE_LIST:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_list, view, false);
				pullRefreshView = (PullToRefreshListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = new ListItemAdapter(childModel);
				break;
			case VIEW_TYPE_GRID:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_grid, view, false);
				pullRefreshView = (PullToRefreshGridView) contentView.findViewById(R.id.ic_gridview);
				itemAdapter = new GridItemAdapter(childModel);
				break;
			default:
				break;
			}
			
			assert contentView != null;
			if (null == contentView) {
				return null;
			}
			
			if (childModel.supportReloading()) {
				if (childModel.supportPaging()) {
					pullRefreshView.setMode(Mode.BOTH);
				} else {
					pullRefreshView.setMode(Mode.PULL_FROM_START);				
				}
			} else {
				pullRefreshView.setMode(Mode.DISABLED);
			}
			
			// Set a listener to be invoked when the list should be refreshed.
			pullRefreshView.setOnRefreshListener(new OnRefreshListener2() {
				@Override
				public void onPullDownToRefresh(
						final PullToRefreshBase refreshView) {
					final String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
							DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

					// Update the LastUpdatedLabel
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

					// Do work to refresh the list here.
					new GetDataTask().execute(true);
				}

				@Override
				public void onPullUpToRefresh(
						final PullToRefreshBase refreshView) {
					// Do work to refresh the list here.
					new GetDataTask().execute(false);
				}
			});
			
			AbsListView absListView = (AbsListView) pullRefreshView.getRefreshableView();
			absListView.setAdapter(itemAdapter);
			absListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// drill down
				}
			});
			
			view.addView(contentView, 0);
			return contentView;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}
	
	private static class GridViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		TextView text;
	}
	
	private class GridItemAdapter extends BaseAdapter {
		
		private ViewNode model;
		
		public GridItemAdapter(ViewNode model) {
			this.model = model;
		}
		
		@Override
		public int getCount() {
			return null == model.getViewItems() ? 0 : model.getViewItems().size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final GridViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
				holder = new GridViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				holder.text = (TextView) view.findViewById(R.id.text);
				view.setTag(holder);
			} else {
				holder = (GridViewHolder) view.getTag();
			}

			ViewItem viewItem = model.getViewItems().get(position);
			if (!TextUtils.isEmpty(viewItem.getImageUrl()) && !viewItem.isUsingColorOverImage()) {
				imageLoader.displayImage(viewItem.getImageUrl(), holder.imageView, options, new SimpleImageLoadingListener() {
										 @Override
										 public void onLoadingStarted(String imageUri, View view) {
											 holder.progressBar.setProgress(0);
											 holder.progressBar.setVisibility(View.VISIBLE);
										 }

										 @Override
										 public void onLoadingFailed(String imageUri, View view,
												 FailReason failReason) {
											 holder.progressBar.setVisibility(View.GONE);
										 }

										 @Override
										 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
											 holder.progressBar.setVisibility(View.GONE);
										 }
									 }, new ImageLoadingProgressListener() {
										 @Override
										 public void onProgressUpdate(String imageUri, View view, int current,
												 int total) {
											 holder.progressBar.setProgress(Math.round(100.0f * current / total));
										 }
									 }
				);
			} else {
				holder.imageView.setBackgroundColor(viewItem.getColor());
				holder.progressBar.setVisibility(View.GONE);
			}

			if (viewItem.isShowingLabelInGrid()) {
				holder.text.setVisibility(View.VISIBLE);
				holder.text.setText(viewItem.getLabel());
				holder.text.setTextColor(Color.WHITE);
			} else {
				holder.text.setVisibility(View.GONE);
			}
			
			return view;
		}
	}
	
	private static class ListViewHolder {
		TextView text;
		ImageView image;
	}
	
	private class ListItemAdapter extends BaseAdapter {

		private ViewNode model;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		
		public ListItemAdapter(ViewNode model) {
			this.model = model;
		}
		
		@Override
		public int getCount() {
			return null == model.getViewItems() ? 0 : model.getViewItems().size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;
			final ListViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ListViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else {
				holder = (ListViewHolder) view.getTag();
			}

			ViewItem viewItem = model.getViewItems().get(position);
			
			holder.text.setText(viewItem.getLabel());

			if (!TextUtils.isEmpty(viewItem.getImageUrl()) && !viewItem.isUsingColorOverImage()) {
				imageLoader.displayImage(viewItem.getImageUrl(), holder.image, options, animateFirstListener);
			} else {
				holder.image.setBackgroundColor(viewItem.getColor());
			}

			return view;
		}
	}

	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

	private class GetDataTask extends AsyncTask<Object, Void, Void> {

		@Override
		protected Void doInBackground(Object... params) {
			// Simulates a background job.
			boolean reload = (Boolean) params[0];
			if (reload) {
				subModel.reload();
			} else {
				subModel.loadOneMorePage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			subAdapter.notifyDataSetChanged();
			
			// Call onRefreshComplete when the list has been refreshed.
			subPullRefreshView.onRefreshComplete();
			
			if (null != subModel && null != subModel.getActions()) {
				for (ViewNodeAction action: subModel.getActions()) {
					MenuItem item = menu.findItem(action.getId());
					item.setTitle(action.getTitle());
					item.setVisible(action.isVisible());
				}
			}
			super.onPostExecute(result);
		}
	}
}