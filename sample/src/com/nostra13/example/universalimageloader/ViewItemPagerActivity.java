package com.nostra13.example.universalimageloader;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersBaseAdapter;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wzw.ic.mvc.HeaderViewHolder;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.SpannableGridLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ViewItemPagerActivity extends BaseActivity {
	DisplayImageOptions gridOptions, listOptions, authorIconOptions;
	ViewPager pager;
    MapView mapView;

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

        authorIconOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(getResources().getDimensionPixelSize(R.dimen.author_icon_dimen)/2))
                .build();

		pager = (ViewPager) findViewById(R.id.ic_viewitem_pagerview);
//		pager.setOffscreenPageLimit(3);
		final PagerAdapter pagerAdapter = new ViewItemPagerAdapter();
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener () {
			@Override
		    public void onPageSelected(int position) {
				if (parentModel.supportPaging() && position >= pagerAdapter.getCount() - 5) {
					new GetDataTask(parentModel, pagerAdapter, new GetDataTask.GetDataTaskFinishedListener() {
							@Override
							public void onGetDataTaskFinished(ViewNode model) {
								if (Build.VERSION.SDK_INT >= 11) {
									ActionBar actionBar = getActionBar();
									initActionBar(actionBar);
								}
							}
						}).execute(false);
				}
		    }
		});
		pager.setCurrentItem((null != parentModel && null != parentModel.getViewItems()) ? parentModel.getViewItems().indexOf(myViewItem) : 0);
		// trigger a initial update of page 0
		myViewItem = null;

        MapsInitializer.initialize(this);
        mapView = new MapView(this);
        mapView.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
        if (null != mapView) {
            mapView.onResume();
        }
	}

    @Override
    public void onPause() {
        super.onPause();
        if (null != mapView) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mapView) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mapView) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (null != mapView) {
            mapView.onLowMemory();
        }
    }

	private void updateCurrentPage() {
        if (model.supportReloading() && model.getViewItems().isEmpty()) {
        	View contentView = (View) pager.findViewWithTag(pager.getCurrentItem());
        	SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_swiperefresh);
			AbsListView absListView = null;
			BaseAdapter itemAdapter = null;
        	switch (myViewItem.getViewType()) {
			case ViewItem.VIEW_TYPE_LIST:
			case ViewItem.VIEW_TYPE_CARD_LIST:
            case ViewItem.VIEW_TYPE_STORY_LIST:
            case ViewItem.VIEW_TYPE_PLACE_LIST:
				absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = (BaseAdapter) absListView.getAdapter();
				break;
			case ViewItem.VIEW_TYPE_GRID:
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
	    
	    for (int i = actionBar.getTabCount(); i < parentModel.getViewItems().size(); ++i) {
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
		actionBar.setSelectedNavigationItem(position);
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
			AbsListView absListView = null;
			final BaseAdapter itemAdapter;
			switch (viewItem.getViewType()) {
			case ViewItem.VIEW_TYPE_LIST:
            case ViewItem.VIEW_TYPE_CARD_LIST:
            case ViewItem.VIEW_TYPE_STORY_LIST:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_list, view, false);
				absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = new ListItemAdapter(childModel, viewItem.getViewType(), (ListView) absListView);
                break;
            case ViewItem.VIEW_TYPE_PLACE_LIST:
                contentView = getLayoutInflater().inflate(R.layout.ac_place_list, view, false);
                absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
                itemAdapter = new ListItemAdapter(childModel, viewItem.getViewType(), (ListView) absListView);
                ViewGroup container = (ViewGroup) contentView.findViewById(R.id.ic_mapcontainer);
                if (null != mapView.getParent()) {
                    ((ViewGroup) mapView.getParent()).removeView(mapView);
                }
                container.addView(mapView, 1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewItemPagerActivity.this.getResources().getDimensionPixelSize(R.dimen.map_view_height))); // after address bar
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition((new CameraPosition.Builder()).target(new LatLng(0, 0)).zoom(0).build()));
                    }
                });
//                ((ListView) absListView).setDividerHeight(0);
                break;
			case ViewItem.VIEW_TYPE_GRID:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_grid, view, false);
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
				itemAdapter = null;
				break;
			}
			
			final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_swiperefresh);
			
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

			SpannableString text = buildPictureText(viewItem, true, true, false, false, false, false);
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
		
		@Override
		public View getHeaderView(final int position, View convertView, ViewGroup parent) {
			final HeaderViewHolder holder;
	        if (convertView == null) {
	            convertView = getLayoutInflater().inflate(model.getHeaderViewResId(position, 0), parent, false);
	            holder = model.createHolderFromHeaderView(convertView);
	            convertView.setTag(holder);
	        } else {
	            holder = (HeaderViewHolder)convertView.getTag();
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
        ProgressBar progressBar;
		TwoWayView spannableGrid;

        HeaderViewHolder headerViewHolder;
    }
	
	private class ListItemAdapter extends BaseAdapter {

		private ViewNode model;
        private int viewType;
        private ListView listView;
		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		
		public ListItemAdapter(ViewNode model, int viewType, ListView listView) {
            this.model = model;
            this.viewType = viewType;
            this.listView = listView;
		}

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if (null != model.getHeaders() && !model.getHeaders().isEmpty()) {
                return model.getHeaders().get(position) == 1 ? 0 : 1;
            } else {
                return 0;
            }
        }

		@Override
		public int getCount() {
			if (null != model.getHeaders() && !model.getHeaders().isEmpty()) {
                return model.getHeaders().size();
			} else {
				return null == model.getViewItems() ? 0 : model.getViewItems().size();
			}
		}

		@Override
		public Object getItem(int position) {
            if (null != model.getHeaders() && !model.getHeaders().isEmpty()) {
                int o = 0;
                for (int i = 0; i < position; ++i) {
                    o += model.getHeaders().get(i);
                }
                return null == model.getViewItems() ? null : model.getViewItems().get(o);
            } else {
                return null == model.getViewItems() ? null : model.getViewItems().get(position);
            }
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
				holder = new ListViewHolder();
				switch (viewType) {
				case ViewItem.VIEW_TYPE_LIST:
					view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
					holder.text = (TextView) view.findViewById(R.id.text);
					holder.image = (ImageView) view.findViewById(R.id.image);
					break;
                case ViewItem.VIEW_TYPE_STORY_LIST:
                    view = getLayoutInflater().inflate(R.layout.item_story_view, parent, false);
                    holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                    holder.image = (ImageView) view.findViewById(R.id.image);
                    holder.text = (TextView) view.findViewById(R.id.story);

                    if (model.getHeaderViewResId(position, getItemViewType(position)) > 0) {
                        holder.headerViewHolder = model.createHolderFromHeaderView(
                                getLayoutInflater().inflate(model.getHeaderViewResId(position, getItemViewType(position)), parent, false)
                        );

                        LinearLayout cardView = new LinearLayout(ViewItemPagerActivity.this);
                        cardView.setLayoutParams(new ListView.LayoutParams(
                                ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
                        cardView.setOrientation(LinearLayout.VERTICAL);

                        if (null != holder.headerViewHolder.header.getParent()) {
                            ((ViewGroup) holder.headerViewHolder.header.getParent()).removeView(holder.headerViewHolder.header);
                        }
                        cardView.addView(holder.headerViewHolder.header);
                        if (null != view.getParent()) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                        cardView.addView(view);
                        if (null != holder.headerViewHolder.footer.getParent()) {
                            ((ViewGroup) holder.headerViewHolder.footer.getParent()).removeView(holder.headerViewHolder.footer);
                        }
                        cardView.addView(holder.headerViewHolder.footer);

                        view = cardView;
                    }
                    break;

				case ViewItem.VIEW_TYPE_CARD_LIST:
                case ViewItem.VIEW_TYPE_PLACE_LIST:
                    int itemViewType = getItemViewType(position);

                    switch (itemViewType) {
                        case 0:
                            view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
                            holder.image = (ImageView) view.findViewById(R.id.image);
                            holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                            holder.text = (TextView) view.findViewById(R.id.text);
                            view.setLayoutParams(new AbsListView.LayoutParams(
                                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT
                            ));
                            break;
                        case 1:
                            view = getLayoutInflater().inflate(R.layout.item_spannable_grid, parent, false);
                            holder.spannableGrid = (TwoWayView) view.findViewById(R.id.ic_spannable_grid);
                            holder.spannableGrid.setHasFixedSize(true);
                            view.setLayoutParams(new AbsListView.LayoutParams(
                                    AbsListView.LayoutParams.MATCH_PARENT, listView.getWidth()
                            ));
                            break;
                        default:
                            break;
                    }

                    if (model.getHeaderViewResId(position, itemViewType) > 0) {
                        switch (itemViewType) {
                            case 0:
                                view.setLayoutParams(new ListView.LayoutParams(
                                        ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT
                                ));
                                break;
                            case 1:
                                view.setLayoutParams(new ListView.LayoutParams(
                                        ListView.LayoutParams.MATCH_PARENT, listView.getWidth()
                                ));
                                break;
                            default:
                                break;
                        }
                        holder.headerViewHolder = model.createHolderFromHeaderView(
                                getLayoutInflater().inflate(model.getHeaderViewResId(position, itemViewType), parent, false)
                        );

                        LinearLayout cardView = new LinearLayout(ViewItemPagerActivity.this);
                        cardView.setLayoutParams(new ListView.LayoutParams(
                                ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));
                        cardView.setOrientation(LinearLayout.VERTICAL);

                        if (null != holder.headerViewHolder.header.getParent()) {
                            ((ViewGroup) holder.headerViewHolder.header.getParent()).removeView(holder.headerViewHolder.header);
                        }
                        cardView.addView(holder.headerViewHolder.header);
                        if (null != view.getParent()) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                        cardView.addView(view);
                        if (null != holder.headerViewHolder.footer.getParent()) {
                            ((ViewGroup) holder.headerViewHolder.footer.getParent()).removeView(holder.headerViewHolder.footer);
                        }
                        cardView.addView(holder.headerViewHolder.footer);

                        view = cardView;
                    }
                    break;
                default:
                    break;
				}

				view.setTag(holder);
			} else {
				holder = (ListViewHolder) view.getTag();
			}

			if (myViewItem.getViewType() == ViewItem.VIEW_TYPE_LIST) {
				
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
			} else if (myViewItem.getViewType() == ViewItem.VIEW_TYPE_STORY_LIST) {
                model.updateHeaderView(view, holder.headerViewHolder, position);

                final ViewItem viewItem = model.getViewItems().get(position);

                view.setBackgroundColor(randomColorForHeader(Math.abs(viewItem.hashCode())));

                SpannableString text = buildPictureText(viewItem, false, false, true, true, true, false);
                holder.text.setText(text);
                holder.text.setMovementMethod(LinkMovementMethod.getInstance());
                holder.text.setVisibility(View.VISIBLE);

                switch (viewItem.getViewItemType()) {
                    case ViewItem.VIEW_ITEM_TYPE_COLOR:
                        view.setBackgroundColor(viewItem.getViewItemColor());
                        holder.image.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                        break;
                    case ViewItem.VIEW_ITEM_TYPE_IMAGE_RES:
                        holder.image.setVisibility(View.VISIBLE);
                        holder.image.setImageResource(viewItem.getViewItemImageResId());
                        holder.progressBar.setVisibility(View.GONE);
                        break;
                    case ViewItem.VIEW_ITEM_TYPE_IMAGE_URL:
                        if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
                            holder.image.setVisibility(View.VISIBLE);
                            imageLoader.displayImage(viewItem.getImageUrl(), holder.image, gridOptions, new SimpleImageLoadingListener() {
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
            } else if (myViewItem.getViewType() == ViewItem.VIEW_TYPE_CARD_LIST ||
                    myViewItem.getViewType() == ViewItem.VIEW_TYPE_PLACE_LIST) {

                model.updateHeaderView(view, holder.headerViewHolder, position);

                int o = 0;
				for (int i = 0; i < position; ++i) {
					o += model.getHeaders().get(i);
				}
				final int offset = o;

                final ViewItem viewItem = model.getViewItems().get(offset);

                view.setBackgroundColor(randomColorForHeader(Math.abs(viewItem.hashCode())));

                if (0 == getItemViewType(position)) {
                    holder.text.setVisibility(View.GONE);

                    switch (viewItem.getViewItemType()) {
                        case ViewItem.VIEW_ITEM_TYPE_COLOR:
                            view.setBackgroundColor(viewItem.getViewItemColor());
                            holder.image.setVisibility(View.GONE);
                            holder.progressBar.setVisibility(View.GONE);
                            break;
                        case ViewItem.VIEW_ITEM_TYPE_IMAGE_RES:
                            holder.image.setVisibility(View.VISIBLE);
                            holder.image.setImageResource(viewItem.getViewItemImageResId());
                            holder.progressBar.setVisibility(View.GONE);
                            break;
                        case ViewItem.VIEW_ITEM_TYPE_IMAGE_URL:
                            if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
                                holder.image.setVisibility(View.VISIBLE);
                                imageLoader.displayImage(viewItem.getImageUrl(), holder.image, gridOptions, new SimpleImageLoadingListener() {
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

                } else {
                    final int albumPicCount = model.getHeaders().get(position);
                    final int hash = Math.abs(model.getViewItems().get(offset).hashCode());

                    RecyclerView.Adapter<SimpleViewHolder> adapter = new RecyclerView.Adapter<SimpleViewHolder>() {

                        @Override
                        public int getItemCount() {
                            final int[] ITEM_COUNT_FOR_LARGE_ALBUMS = {4, 5, 6, 9};
                            switch (albumPicCount) {
                                case 2:
                                    return 2;
                                case 3:
                                    return 3;
                                case 4:
                                    return 4;
                                case 5:
                                    return 5;
                                case 6:
                                case 7:
                                case 8:
                                    return 6;
                                default:
                                    return ITEM_COUNT_FOR_LARGE_ALBUMS[hash%ITEM_COUNT_FOR_LARGE_ALBUMS.length];
                            }
                        }

                        @Override
                        public void onBindViewHolder(final SimpleViewHolder holder, int position) {
                            final View itemView = holder.itemView;
                            final SpannableGridLayoutManager.LayoutParams lp =
                                    (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();

                            final int[] SPANS = generateColRowSpans(getItemCount(), hash);

                            int colSpan = SPANS[position*2];
                            int rowSpan = SPANS[position*2+1];

                            lp.rowSpan = rowSpan;
                            lp.colSpan = colSpan;
                            itemView.setLayoutParams(lp);

                            final ViewItem viewItem = model.getViewItems().get(offset + position);
                            if (!TextUtils.isEmpty(viewItem.getLabel())) {
                                holder.text.setVisibility(View.VISIBLE);
                                holder.text.setText(viewItem.getLabel());
                            } else {
                                holder.text.setVisibility(View.GONE);
                            }
                            switch (viewItem.getViewItemType()) {
                                case ViewItem.VIEW_ITEM_TYPE_COLOR:
                                    itemView.setBackgroundColor(viewItem.getViewItemColor());
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
                        }

                        @Override
                        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
                            final View view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
                            return new SimpleViewHolder(view);
                        }

                    };
                    holder.spannableGrid.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    final ItemClickSupport itemClick = ItemClickSupport.addTo(holder.spannableGrid);

                    itemClick.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClick(RecyclerView parent, View child, int position, long id) {
                            // drill down
                            model.onViewItemClicked(
                                    (ViewItem)model.getViewItems().get(offset + position), ViewItemPagerActivity.this);
                        }
                    });
                }
			}
			
			return view;
		}
	}

	private static class SimpleViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		TextView text;

        public SimpleViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
			progressBar = (ProgressBar) view.findViewById(R.id.progress);
			text = (TextView) view.findViewById(R.id.text);
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

    private static final int[] generateColRowSpans(int itemCount, int hash) {
        final int[][][] SPANS = {
                {}, // 0
                {}, // 1
                {
                        {6, 3, 6, 3}, {3, 6, 3, 6}
                }, // 2
                {
                        {6, 3, 3, 3, 3, 3}, {3, 6, 3, 3, 3, 3}
                }, // 3
                {
                        {3, 3, 3, 3, 3, 3, 3, 3}, {6, 4, 2, 2, 2, 2, 2, 2}, {4, 6, 2, 2, 2, 2, 2, 2},
                        {4, 4, 2, 4, 4, 2, 2, 2}
                }, // 4
                {
                        {4, 2, 2, 4, 2, 4, 2, 2, 4, 2}
                }, // 5
                {
                        {4, 4, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}, {2, 2, 4, 4, 2, 2, 2, 2, 2, 2, 2, 2},
                        {2, 2, 2, 2, 2, 2, 4, 4, 2, 2, 2, 2}, {2, 2, 2, 2, 2, 2, 2, 2, 4, 4, 2, 2}
                }, // 6
                {}, // 7
                {}, // 8
                {
                        {2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2}
                }, // 9
        };
        return SPANS[itemCount][hash%SPANS[itemCount].length];
    }

    private static int randomColorForHeader(int header) {
        final int[] COLORS = {0xFF3D3629, 0xFF5C483D, 0xFF5C583D, 0xFF2C3D29, 0xFF3D5C3D, 0xFF6A7A52, 0xFFB8B37A, 0xFFD6D68F};
        return COLORS[header%COLORS.length];
    }
}