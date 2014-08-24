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
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeAction;

public class ViewItemPagerActivity extends BaseActivity {
	DisplayImageOptions gridOptions, listOptions;
	ViewPager pager;
	
	BaseAdapter currentAdapter;
	PullToRefreshBase currentPullRefreshView;
	AbsListView currentAbsListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_view_item_pager);
		
		setModelFromIntent();
		
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;

		gridOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();

		listOptions = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(20))
			.build();
		
		pager = (ViewPager) findViewById(R.id.ic_viewitem_pagerview);
		pager.setOffscreenPageLimit(3);
		pager.setAdapter(new ViewItemPagerAdapter());
		pager.setCurrentItem((null != parentModel && null != parentModel.getViewItems()) ? parentModel.getViewItems().indexOf(myViewItem) : 0);
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
			return null == parentModel.getViewItems() ? 0 : parentModel.getViewItems().size();
		}

		@Override
	    public void setPrimaryItem(ViewGroup container, int position, Object object) {
	        super.setPrimaryItem(container, position, object);
	        
	        View contentView = (View) object;
	        myViewItem = parentModel.getViewItems().get(position);
	        model = myViewItem.getViewNode();
	        switch (myViewItem.getViewType()) {
			case ViewItem.VIEW_TYPE_LIST:
				currentPullRefreshView = (PullToRefreshListView) contentView.findViewById(R.id.ic_listview);
				break;
			case ViewItem.VIEW_TYPE_GRID:
				currentPullRefreshView = (PullToRefreshGridView) contentView.findViewById(R.id.ic_gridview);
				break;
			default:
				break;
			}
	        if (null != currentPullRefreshView) {
	        	currentAbsListView = (AbsListView)currentPullRefreshView.getRefreshableView();
	        	if (currentAbsListView.getAdapter() instanceof HeaderViewListAdapter) {
	        		currentAdapter = (BaseAdapter) ((HeaderViewListAdapter) currentAbsListView.getAdapter()).getWrappedAdapter();
	        	} else {
	        		currentAdapter = (BaseAdapter) currentAbsListView.getAdapter();
	        	}
	        }
	        
	        setTitleIconFromViewItem(myViewItem);
	    }
		
		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			final ViewItem viewItem = parentModel.getViewItems().get(position);
			ViewNode childModel = viewItem.getViewNode();
			
			View contentView = null;
			PullToRefreshBase pullRefreshView = null;
			BaseAdapter itemAdapter = null;
			switch (viewItem.getViewType()) {
			case ViewItem.VIEW_TYPE_LIST:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_list, view, false);
				pullRefreshView = (PullToRefreshListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = new ListItemAdapter(childModel);
				break;
			case ViewItem.VIEW_TYPE_GRID:
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
					if (viewItem.getViewType() == ViewItem.VIEW_TYPE_LIST) {
						position -= 1;
					}
					ViewItemPagerActivity.this.startViewItemActivity(model.getViewItems().get(position));
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
				imageLoader.displayImage(viewItem.getImageUrl(), holder.imageView, gridOptions, new SimpleImageLoadingListener() {
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
				imageLoader.displayImage(viewItem.getImageUrl(), holder.image, listOptions, animateFirstListener);
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
				model.reload();
			} else {
				model.loadOneMorePage();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			currentAdapter.notifyDataSetChanged();
			
			// Call onRefreshComplete when the list has been refreshed.
			currentPullRefreshView.onRefreshComplete();
			if (null != model && null != model.getActions()) {
				for (ViewNodeAction action: model.getActions()) {
					MenuItem item = menu.findItem(action.getId());
					item.setTitle(action.getTitle());
//					item.setVisible(action.isVisible());
				}
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (null != currentAbsListView) {
			currentAbsListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, false /*pauseOnScroll*/, true /*pauseOnFling*/));
		}
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus && currentAdapter.getCount() == 0) {
//			subPullRefreshView.setRefreshing();
//		}
	}
}