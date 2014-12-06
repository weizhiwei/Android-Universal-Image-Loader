package com.nostra13.example.universalimageloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView.OnHeaderClickListener;
import com.wzw.ic.mvc.HeaderViewHolder;
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
			initActionBar(actionBar);
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
		// trigger a initial update of page 0
		myViewItem = null;
	}
	
//	@Override
//	public void onResume() {
//		super.onResume();
//		pager.setCurrentItem((null != parentModel && null != parentModel.getViewItems()) ? parentModel.getViewItems().indexOf(myViewItem) : 0);
//		// trigger a initial update of page 0
//		myViewItem = null;
//	}
	
	private void updateCurrentPage() {
        if (model.supportReloading() && model.getViewItems().isEmpty()) {
        	View contentView = (View) pager.findViewWithTag(pager.getCurrentItem());
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
			
			new GetDataTask(model, swipeRefreshLayout, itemAdapter, null).execute(true);
		}
	}
		
	protected void initActionBar(ActionBar actionBar) {
		if (parentModel.getViewItems().size() <= 1) {
			return;
		}
		
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
            tab.setText(viewItem.getLabel());
            actionBar.addTab(tab);
		}
	    
//		setHasEmbeddedTabs(actionBar, true);
	    
	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	protected void setActionBarSelection(ActionBar actionBar, int position) {
		if (parentModel.getViewItems().size() <= 1) {
			return;
		}
		actionBar.selectTab(actionBar.getTabAt(position));
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
	        
	        updateMenu(model);
	        
	        if (myViewItem == parentModel.getViewItems().get(position)) {
	        	return;
	        }
	        
	        myViewItem = parentModel.getViewItems().get(position);
	        model = myViewItem.getViewNode();
	        
	        updateTitleIconFromViewItem(myViewItem);
	        
	        if (Build.VERSION.SDK_INT >= 11) {
				ActionBar actionBar = getActionBar();
				setActionBarSelection(actionBar, position);
	        }
	        
	        updateCurrentPage();
	    }
		
		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			final ViewItem viewItem = parentModel.getViewItems().get(position);
			final ViewNode childModel = viewItem.getViewNode();
			
			View contentView = null;
			final SwipeRefreshLayout swipeRefreshLayout;
			AbsListView absListView = null;
			final BaseAdapter itemAdapter;
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
				itemAdapter = new GridItemAdapter(childModel, (GridView) absListView);
				if (viewItem.getInitialZoomLevel() > 0 && viewItem.getInitialZoomLevel() <= 3) {
					((GridView) absListView).setNumColumns(viewItem.getInitialZoomLevel());
				}
				((StickyGridHeadersGridView)absListView).setAreHeadersSticky(false);
//				((StickyGridHeadersGridView)absListView).setOnHeaderClickListener(
//						new OnHeaderClickListener () {
//							@Override
//							public void onHeaderClick(AdapterView<?> parent, View view, long id) {
//								HeaderViewHolder holder = (HeaderViewHolder) view.getTag();
//								if (null != holder.viewItem) {
//									ViewItemPagerActivity.this.startViewItemActivity(holder.model, holder.viewItem);
//								}
//							}
//							
//						});
				break;
			default:
				swipeRefreshLayout = null;
				itemAdapter = null;
				break;
			}
			
			assert contentView != null;
			if (null == contentView || null == swipeRefreshLayout || null == absListView) {
				return null;
			}
			
			contentView.setTag(position);
			
			// Set a listener to be invoked when the list should be refreshed.
			swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
				    @Override
		            public void onRefresh() {
		                // Your code to refresh the list here.
		                // Make sure you call swipeContainer.setRefreshing(false) when
		                // once the network request has completed successfully.
						new GetDataTask(childModel, swipeRefreshLayout, itemAdapter, null).execute(true);
		            }
			});
			swipeRefreshLayout.setEnabled(childModel.supportReloading());
			// Configure the refreshing colors
			swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright, 
	                android.R.color.holo_green_light, 
	                android.R.color.holo_orange_light, 
	                android.R.color.holo_red_light);
						
			absListView.setAdapter(itemAdapter);
			absListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					// drill down
					model.onViewItemClicked((ViewItem)itemAdapter.getItem(position), ViewItemPagerActivity.this);
				}
			});
			
			final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(ViewItemPagerActivity.this,
					new ScaleGestureDetector.SimpleOnScaleGestureListener () {
			    @Override
			    public void onScaleEnd(ScaleGestureDetector detector) {
			    	zoomGridView(detector.getScaleFactor() > 1.0, false);
			    }
			});
			
			absListView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					scaleDetector.onTouchEvent(event);
					return false;
				}
			});
			
			if (childModel.supportPaging()) {
				absListView.setOnScrollListener(new EndlessScrollListener() {
				    @Override
				    public void onLoadMore(int page, int totalItemsCount) {
				    	new GetDataTask(childModel, swipeRefreshLayout, itemAdapter, null).execute(false);
				    }
			    });
			}
			
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
	
	private class GridItemAdapter extends BaseAdapter implements StickyGridHeadersBaseAdapter {
		
		private ViewNode model;
		private GridView gridView;
		
		public GridItemAdapter(ViewNode model, GridView gridView) {
			this.model = model;
			this.gridView = gridView;
		}
		
		@Override
		public int getCount() {
			return null == model.getViewItems() ? 0 : model.getViewItems().size();
		}

		@Override
		public Object getItem(int position) {
			if (null == model.getViewItems()) {
				return null;
			}
			ItemPositionStatus ips = getHeaderPositionForItem(position);
			ViewItem viewItem = null;
			if (ips != null) {
				if (!ips.isPaddingItem) {
					viewItem = model.getViewItems().get(ips.itemPositionInModel);
				}
			} else {
				viewItem = model.getViewItems().get(position);
			}
			
			return viewItem;
		}

		@Override
		public long getItemId(int position) {
			Object item = getItem(position);
			return item == null ? 0 : item.hashCode();
		}

		@Override
		public boolean isEnabled (int position) {
			ItemPositionStatus ips = getHeaderPositionForItem(position);
			return ips == null || !ips.isPaddingItem;
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
			
			int rowHeight;
			switch (gridView.getNumColumns()) {
			case 1:
				rowHeight = GridView.LayoutParams.WRAP_CONTENT;
				break;
			case 2:
			case 3:
			default:
				rowHeight = gridView.getWidth()/gridView.getNumColumns();
				break;
			}
			view.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.FILL_PARENT, rowHeight));
			
			ItemPositionStatus ips = getHeaderPositionForItem(position);
			final ViewItem viewItem;
			if (ips != null) {
				int color = randomColorForHeader(ips.header);
				view.setBackgroundColor(color);
				if (ips.isPaddingItem) {
					holder.imageView.setVisibility(View.GONE);
					holder.progressBar.setVisibility(View.GONE);
					holder.text.setVisibility(View.GONE);
					return view;
				} else {
					viewItem = model.getViewItems().get(ips.itemPositionInModel);
				}
			} else {
				viewItem = model.getViewItems().get(position);
			}
			
			switch (viewItem.getViewItemType()) {
			case ViewItem.VIEW_ITEM_TYPE_COLOR:
				view.setBackgroundColor(viewItem.getViewItemColor());
				holder.imageView.setVisibility(View.GONE);
				holder.progressBar.setVisibility(View.GONE);
				break;
			case ViewItem.VIEW_ITEM_TYPE_IMAGE_RES:
				holder.imageView.setVisibility(View.VISIBLE);
				holder.imageView.setImageResource(viewItem.getViewItemImageResId());
				holder.progressBar.setVisibility(View.GONE);
				break;
			case ViewItem.VIEW_ITEM_TYPE_IMAGE_URL:
				if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
					holder.imageView.setVisibility(View.VISIBLE);
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
				}
				break;
			default:
				break;
			}

			SpannableString text = buildPictureText(viewItem, false, false, false, false);
			if (null != text && gridView.getNumColumns() < 3) {
				holder.text.setVisibility(View.VISIBLE);
				holder.text.setText(text);
//				holder.text.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				holder.text.setVisibility(View.GONE);
			}
			
			return view;
		}

		private class ItemPositionStatus {
			public int header;
			public boolean isPaddingItem;
			public int itemPositionInModel;
			public ItemPositionStatus(
					int header,
					boolean isPaddingItem,
					int itemPositionInModel) {
				this.header = header;
				this.isPaddingItem = isPaddingItem;
				this.itemPositionInModel = itemPositionInModel;
			}
		}
		
		private ItemPositionStatus getHeaderPositionForItem(int itemPos) {
			int itemPositionInModel = 0;
			for (int i = 0; i < getNumHeaders(); ++i) {
				itemPos -= getCountForHeader(i);
				itemPositionInModel += model.getHeaders().get(i);
				if (itemPos < 0) {
					itemPos += getCountForHeader(i);
					itemPositionInModel -= model.getHeaders().get(i);
					return new ItemPositionStatus(
							i,
							itemPos >= model.getHeaders().get(i),
							itemPositionInModel + itemPos);
				}
			}
			return null;
		}
		
		private int randomColorForHeader(int header) {
			final int[] COLORS = {Color.GREEN, Color.LTGRAY, Color.CYAN};
			return COLORS[header*314159%COLORS.length];
		}
		
		@Override
		public View getHeaderView(final int position, View convertView, ViewGroup parent) {
			final HeaderViewHolder holder;
	        if (convertView == null) {
	            convertView = getLayoutInflater().inflate(model.getHeaderViewResId(), parent, false);
	            holder = model.createHolderFromHeaderView(convertView);
	            convertView.setTag(holder);
	        } else {
	            holder = (HeaderViewHolder)convertView.getTag();
	        }
	        
	        if (null != holder.footer) {
	        	holder.footer.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
	        	if (position > 0) {
	        		((TextView)holder.footer).setText("See more");
	        		holder.footer.setBackgroundColor(randomColorForHeader(position-1));
	        		holder.footer.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							model.onFooterClicked(position-1, ViewItemPagerActivity.this);
						}
	        		});
	        	}
	        }
	        if (null != holder.divider) {
	        	holder.divider.setVisibility((position == 0 || position >= model.getHeaders().size())
	        			? View.GONE : View.VISIBLE);
	        }
	        if (null != holder.header) {
	        	holder.header.setVisibility(position >= model.getHeaders().size() ? View.GONE : View.VISIBLE);
	        	if (position < model.getHeaders().size()) {
	        		holder.header.setBackgroundColor(randomColorForHeader(position));
	        		model.updateHeaderView(convertView, holder, position);
	        		holder.header.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							model.onHeaderClicked(position, ViewItemPagerActivity.this);
						}
					});
	        	}
	        }
	        
	        return convertView;
	    }

		@Override
		public int getCountForHeader(int header) {
			List<Integer> headers = model.getHeaders();
			if (null == headers) {
				return 0;
			}
			if (header >= headers.size()) {
				return 0;
			}
			int count = headers.get(header);
			int numColumns = gridView.getNumColumns();
			int r = count % numColumns;
			if (0 != r) {
				count += (numColumns - r);
			}
			return count;
		}

		@Override
		public int getNumHeaders() {
			if (null == model.getHeaders()) {
				return 0;
			} else if (0 == model.getHeaders().size()) {
				return 0;
			} else {
				return model.getHeaders().size() + 1;
			}
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
			return null == model.getViewItems() ? null : model.getViewItems().get(position);
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

			final ViewItem viewItem = model.getViewItems().get(position);
			
			holder.text.setText(viewItem.getLabel());

			switch (viewItem.getViewItemType()) {
			case ViewItem.VIEW_ITEM_TYPE_COLOR:
				holder.image.setBackgroundColor(viewItem.getViewItemColor());
				break;
			case ViewItem.VIEW_ITEM_TYPE_IMAGE_RES:
				holder.image.setImageResource(viewItem.getViewItemImageResId());
				break;
			case ViewItem.VIEW_ITEM_TYPE_IMAGE_URL:
				if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
					imageLoader.displayImage(viewItem.getImageUrl(), holder.image, listOptions, animateFirstListener);
				}
				break;
			default:
				break;
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

	private class GetDataTaskFinished implements GetDataTaskFinishedListener {
		
		@Override
		public void onGetDataTaskFinished(ViewNode model) {
//			if (position == pager.getCurrentItem()) {
//				updateMenu(model);
//			}
		}
	}
	
	private interface GetDataTaskFinishedListener {
		public void onGetDataTaskFinished(ViewNode model);
	}
	
	private static class GetDataTask extends AsyncTask<Object, Void, Void> {

		protected ViewNode model;
		protected SwipeRefreshLayout swipeRefreshLayout;
		protected BaseAdapter itemAdapter;
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
		
		@Override
		protected void onPreExecute() {
			swipeRefreshLayout.setRefreshing(true);
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
			swipeRefreshLayout.setRefreshing(false);
        	itemAdapter.notifyDataSetChanged();
			if (null != listener) {
				listener.onGetDataTaskFinished(model);
			}
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
	
//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {
//		return super.onPrepareOptionsMenu(menu);
//	}
	
	@Override
	protected void updateMenu(ViewNode model) {
		super.updateMenu(model);
		if (null != menu) {
			MenuItem item = menu.findItem(R.id.item_zoom_in);
			if (null != myViewItem) {
				item.setVisible(myViewItem.getViewType() == ViewItem.VIEW_TYPE_GRID);
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_zoom_in:
				zoomGridView(true, true);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	protected void setGridViewColumns(GridView gridView, int numColumns) {
		if (numColumns <= 0) {
			numColumns = gridView.getNumColumns() == 1 ? 3 : gridView.getNumColumns() - 1;
		} else if (numColumns > 3) {
			numColumns = gridView.getNumColumns() == 3 ? 1 : gridView.getNumColumns() - 1;
		}
		gridView.setNumColumns(numColumns);
	}
	
	protected void zoomGridView(boolean in, boolean circular) {
		if (myViewItem.getViewType() == ViewItem.VIEW_TYPE_GRID) {
			View contentView = pager.findViewWithTag(pager.getCurrentItem());
			GridView gridView = (GridView) contentView.findViewById(R.id.ic_gridview);
			setGridViewColumns(gridView, in ?
					(gridView.getNumColumns() == 1 ? (circular ? 3 : 1) : gridView.getNumColumns() - 1) :
					(gridView.getNumColumns() == 3 ? (circular ? 1 : 3) : gridView.getNumColumns() + 1));
		}
	}
}