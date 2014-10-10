package com.nostra13.example.universalimageloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class ViewItemPagerActivity extends BaseActivity {
	DisplayImageOptions gridOptions, listOptions;
	ViewPager pager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_view_item_pager);
		
		setModelFromIntent();
		
		if (Build.VERSION.SDK_INT >= 11) {
			ActionBar actionBar = getActionBar();

			// Create a tab listener that is called when the user changes tabs.
		    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	
				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
				}
	
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					if (null != pager) {
						pager.setCurrentItem(tab.getPosition());
					}
				}
	
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				}
		    };
		    
		    for (int i = 0; i < parentModel.getViewItems().size(); i++) {
		    	ViewItem viewItem = parentModel.getViewItems().get(i);
		    	final Tab tab = actionBar.newTab();
                tab.setTabListener(tabListener);
                setTabTitleIcon(tab, i, viewItem);
                actionBar.addTab(tab);
			}
		    
			setHasEmbeddedTabs(actionBar, hasEmbeddedTabs());
		    // Specify that tabs should be displayed in the action bar.
		    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
		
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
//		pager.setOffscreenPageLimit(3);
//		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener () {
//			@Override
//		    public void onPageSelected(int position) {
//		    }
//		});
		pager.setAdapter(new ViewItemPagerAdapter());
		pager.setCurrentItem((null != parentModel && null != parentModel.getViewItems()) ? parentModel.getViewItems().indexOf(myViewItem) : 0);
	}
	
	protected boolean hasEmbeddedTabs() {
		return false;
	}
	
	protected void setTabTitleIcon(Tab tab, int position, ViewItem viewItem) {
		tab.setText(viewItem.getLabel());
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
	        
	        if (myViewItem == parentModel.getViewItems().get(position)) {
	        	return;
	        }
	        
	        myViewItem = parentModel.getViewItems().get(position);
	        model = myViewItem.getViewNode();
	        
	        updateTitleIconFromViewItem(myViewItem);
	        updateMenu(model);
	        
	        if (Build.VERSION.SDK_INT >= 11) {
				ActionBar actionBar = getActionBar();
				actionBar.selectTab(actionBar.getTabAt(position));
	        }
	        
	        if (model.supportReloading() && model.getViewItems().isEmpty()) {
	        	View contentView = (View) object;
	        	SwipeRefreshLayout swipeRefreshLayout = null;
				AbsListView absListView = null;
				BaseAdapter itemAdapter = null;
	        	switch (myViewItem.getViewType()) {
				case ViewItem.VIEW_TYPE_LIST:
					swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_listview_swiperefresh);
					absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
					itemAdapter = (BaseAdapter) absListView.getAdapter();
					break;
				case ViewItem.VIEW_TYPE_GRID:
					swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_gridview_swiperefresh);
					absListView = (AbsListView) contentView.findViewById(R.id.ic_gridview);
					itemAdapter = (BaseAdapter) absListView.getAdapter();
					break;
				default:
					break;
				}
				
	        	swipeRefreshLayout.setRefreshing(true);
				new GetDataTask(model, itemAdapter).execute(true);
			}
	    }
		
		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			final ViewItem viewItem = parentModel.getViewItems().get(position);
			final ViewNode childModel = viewItem.getViewNode();
			
			View contentView = null;
			SwipeRefreshLayout swipeRefreshLayout = null;
			AbsListView absListView = null;
			BaseAdapter itemAdapter = null;
			switch (viewItem.getViewType()) {
			case ViewItem.VIEW_TYPE_LIST:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_list, view, false);
				swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_listview_swiperefresh);
				absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = new ListItemAdapter(childModel);
				break;
			case ViewItem.VIEW_TYPE_GRID:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_grid, view, false);
				swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_gridview_swiperefresh);
				absListView = (AbsListView) contentView.findViewById(R.id.ic_gridview);
				itemAdapter = new GridItemAdapter(childModel);
				break;
			default:
				break;
			}
			
			assert contentView != null;
			if (null == contentView || null == swipeRefreshLayout || null == absListView) {
				return null;
			}
			
			contentView.setTag(position);
			
			final SwipeRefreshLayout swipeRefreshLayoutFinal = swipeRefreshLayout;
			final BaseAdapter itemAdapterFinal = itemAdapter;
			
			// Set a listener to be invoked when the list should be refreshed.
			swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
				    @Override
		            public void onRefresh() {
		                // Your code to refresh the list here.
		                // Make sure you call swipeContainer.setRefreshing(false) when
		                // once the network request has completed successfully.
						new GetDataTask(childModel, itemAdapterFinal).execute(true);
		            }
			});
			swipeRefreshLayout.setEnabled(childModel.supportReloading());
			// Configure the refreshing colors
			swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
	                android.R.color.holo_green_light, 
	                android.R.color.holo_orange_light, 
	                android.R.color.holo_red_light);
			
			itemAdapter.registerDataSetObserver(new DataSetObserver() {
				@Override
	            public void onChanged() {
					swipeRefreshLayoutFinal.setRefreshing(false);
					if (position == pager.getCurrentItem()) {
						updateMenu(childModel);
					}
				}
			});
			
			absListView.setAdapter(itemAdapter);
			absListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// drill down
					ViewItemPagerActivity.this.startViewItemActivity(model, model.getViewItems().get(position));
				}
			});
			if (childModel.supportPaging()) {
				absListView.setOnScrollListener(new EndlessScrollListener() {
				    @Override
				    public void onLoadMore(int page, int totalItemsCount) {
				    	new GetDataTask(childModel, itemAdapterFinal).execute(false);
				    }
			    });
			}
			
			view.addView(contentView, 0);
			
//			if (childModel.supportReloading()) {
//				swipeRefreshLayout.setRefreshing(true);
//				new GetDataTask(childModel, itemAdapter).execute(true);
//			}
			
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

	private static class GetDataTask extends AsyncTask<Object, Void, Void> {

		protected ViewNode model;
		protected BaseAdapter adapter;
		
		public GetDataTask(ViewNode model, BaseAdapter adapter) {
			this.model = model;
			this.adapter = adapter;
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
			adapter.notifyDataSetChanged();
			
			// Call onRefreshComplete when the list has been refreshed.
			super.onPostExecute(result);
		}
	}
	
//	@Override
//	public void onResume() {
//		super.onResume();
//		if (null != currentAbsListView) {
//			currentAbsListView.setOnScrollListener(new PauseOnScrollListener(imageLoader, false /*pauseOnScroll*/, true /*pauseOnFling*/));
//		}
//	}
	
//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus && currentAdapter.getCount() == 0) {
//			subPullRefreshView.setRefreshing();
//		}
//	}
}