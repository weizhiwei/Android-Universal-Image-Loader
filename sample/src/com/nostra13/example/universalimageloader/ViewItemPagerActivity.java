package com.nostra13.example.universalimageloader;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.wzw.ic.mvc.ViewNode;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.SpannableGridLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ViewItemPagerActivity extends BaseActivity {
	ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ac_view_item_pager);
		
		setModelFromIntent();
		
		ActionBar actionBar = getSupportActionBar();
		initActionBar(actionBar);

        Bundle bundle = getIntent().getExtras();
		assert bundle != null;

		pager = (ViewPager) findViewById(R.id.ic_viewitem_pagerview);
//		pager.setOffscreenPageLimit(3);
		final PagerAdapter pagerAdapter = new ViewItemPagerAdapter(viewNode.getParent(), getLayoutInflater());
		pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener () {
			@Override
		    public void onPageSelected(int position) {
                if (viewNode.getParent().supportPaging() && position >= pagerAdapter.getCount() - 5) {
					new GetDataTask(viewNode.getParent(), pagerAdapter, new GetDataTask.GetDataTaskFinishedListener() {
							@Override
							public void onGetDataTaskFinished(ViewNode model) {
								ActionBar actionBar = getSupportActionBar();
								initActionBar(actionBar);
							}
						}, false);
				}
		    }
		});
	}
		
	protected void initActionBar(ActionBar actionBar) {
		if (viewNode.getSiblingCount() <= 1) {
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
	    
	    for (int i = actionBar.getTabCount(); i < viewNode.getSiblingCount(); ++i) {
	    	ViewNode viewItem = viewNode.getSibling(i);
	    	final Tab tab = actionBar.newTab();
            tab.setTabListener(tabListener);
            tab.setText(viewItem.getTitle());
            actionBar.addTab(tab);
		}
	    
//		setHasEmbeddedTabs(actionBar, true);
	    
	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}
	
	protected void setActionBarSelection(ActionBar actionBar, int position) {
		if (null == viewNode.getParent() || viewNode.getParent().getChildren().size() <= 1) {
			return;
		}
		actionBar.setSelectedNavigationItem(position);
	}

	private class ViewItemPagerAdapter extends PagerAdapter {

        private ViewNode model;
        private LayoutInflater layoutInflater;

		ViewItemPagerAdapter(ViewNode model, LayoutInflater layoutInflater) {
            this.model = model;
            this.layoutInflater = layoutInflater;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return model.getChildren().size();
		}

        /* @Override */
        public Object getItem(int position) {
            return model.getChildren().get(position);
        }

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			final ViewNode child = (ViewNode)getItem(position);
			
			View contentView = null;
            final SwipeRefreshLayout swipeRefreshLayout;
			AbsListView absListView = null;
            final List<EndlessScrollListener> onScrollListeners = new ArrayList<>();
			final BaseAdapter itemAdapter;
            final RecyclerView.Adapter recyclerViewAdapter;
            final GetDataTask.GetDataTaskFinishedListener getDataTaskFinishedListener;

			switch (child.getViewType()) {
			case ViewNode.VIEW_TYPE_LIST_SIMPLE:
            case ViewNode.VIEW_TYPE_LIST_TILES:
				contentView = layoutInflater.inflate(R.layout.ac_image_list, view, false);
				absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = new ListItemAdapter(child, (ListView) absListView, layoutInflater);
                ((ListView) absListView).setAdapter(itemAdapter);
                recyclerViewAdapter = null;
                getDataTaskFinishedListener = null;
                ((ListView) absListView).setDividerHeight(0);
                break;
			case ViewNode.VIEW_TYPE_GRID:
				contentView = layoutInflater.inflate(R.layout.ac_image_grid, view, false);
				absListView = (AbsListView) contentView.findViewById(R.id.ic_gridview);
				itemAdapter = new GridItemAdapter(child, (GridView) absListView, layoutInflater);
                ((GridView) absListView).setAdapter(itemAdapter);
                recyclerViewAdapter = null;
                getDataTaskFinishedListener = null;
				if (child.getInitialZoomLevel() > 0 && child.getInitialZoomLevel() <= 3) {
					((GridView) absListView).setNumColumns(child.getInitialZoomLevel());
				}
				break;
            case ViewNode.VIEW_TYPE_WEBVIEW:
                contentView = layoutInflater.inflate(R.layout.ac_web_view, view, false);
                itemAdapter = null;
                recyclerViewAdapter = null;
                final WebView webView = (WebView) contentView.findViewById(R.id.ic_webview);
                webView.setWebViewClient(new WebViewClient());
                getDataTaskFinishedListener = new GetDataTask.GetDataTaskFinishedListener () {

                    @Override
                    public void onGetDataTaskFinished(ViewNode model) {
                        List<ViewNode> viewItems = model.getChildren();
                        if (null != viewItems && !viewItems.isEmpty()) {
                            webView.loadUrl(viewItems.get(0).getWebPageUrl());
                        }
                    }
                };
                break;
			default:
				itemAdapter = null;
                recyclerViewAdapter = null;
                getDataTaskFinishedListener = null;
				break;
			}
			
			swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_swiperefresh);

            assert contentView != null;
			if (null == contentView || null == swipeRefreshLayout) {
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
						new GetDataTask(child, swipeRefreshLayout, itemAdapter, recyclerViewAdapter, getDataTaskFinishedListener, true);
		            }
			});
			swipeRefreshLayout.setEnabled(child.supportReloading());
			// Configure the refreshing colors
//			swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
//	                android.R.color.holo_green_light,
//	                android.R.color.holo_orange_light,
//	                android.R.color.holo_red_light);

			if (child.supportPaging()) {
				onScrollListeners.add(new EndlessScrollListener() {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        new GetDataTask(child, swipeRefreshLayout, itemAdapter, recyclerViewAdapter, new GetDataTask.GetDataTaskFinishedListener() {

                            @Override
                            public void onGetDataTaskFinished(ViewNode model) {
                                setLoading(false);
                            }
                        }, false);
                    }
                });
			}

            if (null != absListView) {
                absListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // drill down
                        startViewItemActivity(child);
                    }
                });

//			final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(ViewItemPagerActivity.this,
//					new ScaleGestureDetector.SimpleOnScaleGestureListener () {
//			    @Override
//			    public void onScaleEnd(ScaleGestureDetector detector) {
//			    	zoomGridView(detector.getScaleFactor() > 1.0, false);
//			    }
//			});
//
//			absListView.setOnTouchListener(new OnTouchListener() {
//
//				@Override
//				public boolean onTouch(View v, MotionEvent event) {
//					scaleDetector.onTouchEvent(event);
//					return false;
//				}
//			});

                absListView.setOnScrollListener(
                        new AbsListView.OnScrollListener() {

                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {
                                if (null != onScrollListeners) {
                                    for (AbsListView.OnScrollListener onScrollListener : onScrollListeners) {
                                        onScrollListener.onScrollStateChanged(view, scrollState);
                                    }
                                }
                            }

                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                if (null != onScrollListeners) {
                                    for (AbsListView.OnScrollListener onScrollListener : onScrollListeners) {
                                        onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                                    }
                                }
                            }
                        }
                );
            }

            view.addView(contentView, 0);
			
			return contentView;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // Check if the key event was the Back button and if there's history
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
//            myWebView.goBack();
//            return true;
//        }
//        // If it wasn't the Back key or there's no web page history, bubble up to the default
//        // system behavior (probably exit the activity)
//        return super.onKeyDown(keyCode, event);
//    }

	private static class GridItemViewHolder extends RecyclerView.ViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		TextView text;

        public GridItemViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image);
            progressBar = (ProgressBar) view.findViewById(R.id.progress);
            text = (TextView) view.findViewById(R.id.text);
        }
	}
	
	private static class GridItemAdapter extends BaseAdapter {
		
		private ViewNode model;
		private GridView gridView;
        private LayoutInflater layoutInflater;
		
		public GridItemAdapter(ViewNode model, GridView gridView, LayoutInflater layoutInflater) {
			this.model = model;
			this.gridView = gridView;
            this.layoutInflater = layoutInflater;
		}
		
		@Override
		public int getCount() {
			return model.getChildren().size();
		}

		@Override
		public Object getItem(int position) {
			return model.getChildren().get(position);
		}

		@Override
		public long getItemId(int position) {
            return position;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final GridItemViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = layoutInflater.inflate(R.layout.item_grid_image, parent, false);
				holder = new GridItemViewHolder(view);
				view.setTag(holder);
			} else {
				holder = (GridItemViewHolder) view.getTag();
			}
			
			int rowHeight;
			switch (getGridViewNumColumns(gridView)) {
			case 1:
				rowHeight = GridView.LayoutParams.WRAP_CONTENT;
				break;
			case 2:
			case 3:
			default:
				rowHeight = gridView.getWidth()/getGridViewNumColumns(gridView);
				break;
			}
			view.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.FILL_PARENT, rowHeight));
			
			final ViewNode child = (ViewNode)getItem(position);
            updateGridItemView(child, holder);
			
			return view;
		}
	}
	
	private static class ListItemViewHolder {
        View itemView;
		TextView text;
		ImageView image;
		TwoWayView spannableGrid;

        ViewNode.WrapperViewHolder wrapperViewHolder;

        public ListItemViewHolder(View view) {
            itemView = view;
        }
    }
	
	private class ListItemAdapter extends BaseAdapter {

		private ViewNode model;
        private ListView listView;
        private LayoutInflater layoutInflater;

		public ListItemAdapter(ViewNode model, ListView listView, LayoutInflater layoutInflater) {
            this.model = model;
            this.listView = listView;
            this.layoutInflater = layoutInflater;
		}

        @Override
        public int getViewTypeCount() {
            return ViewNode.VIEW_TYPE_LIST_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            return ((ViewNode)getItem(position)).getViewType();
        }

		@Override
		public int getCount() {
			return model.getChildren().size();
		}

		@Override
		public Object getItem(int position) {
            return model.getChildren().get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View view, ViewGroup parent) {
            final ViewNode child = (ViewNode)getItem(position);

			final ListItemViewHolder holder;
            if (view == null) {

				switch (getItemViewType(position)) {

				case ViewNode.VIEW_TYPE_LIST_SIMPLE:
					view = layoutInflater.inflate(R.layout.item_list_image, parent, false);
                    holder = new ListItemViewHolder(view);
                    holder.text = (TextView) view.findViewById(R.id.text);
					holder.image = (ImageView) view.findViewById(R.id.image);
					break;

                case ViewNode.VIEW_TYPE_LIST_TILES:
                    view = layoutInflater.inflate(R.layout.item_spannable_grid, parent, false);
                    holder = new ListItemViewHolder(view);
                    holder.spannableGrid = (TwoWayView) view.findViewById(R.id.ic_spannable_grid);
                    holder.spannableGrid.setHasFixedSize(true);
                    view.setLayoutParams(new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT, listView.getWidth()
                    ));

                    if (child.getWrapperViewResId() > 0) {
                        holder.wrapperViewHolder = child.createWrapperView(
                                layoutInflater.inflate(child.getWrapperViewResId(), parent, false)
                        );

                        if (null != view.getParent()) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                        holder.wrapperViewHolder.placeholder.addView(view);

                        view = holder.wrapperViewHolder.wrapperView;
                    }
                    break;

                default:
                    holder = null;
                    break;
				}

				view.setTag(holder);
			} else {
				holder = (ListItemViewHolder) view.getTag();
			}

            // update part
            //
            switch (getItemViewType(position)) {

                case ViewNode.VIEW_TYPE_LIST_SIMPLE:

                    holder.text.setText(child.getTitle());

                    switch (child.getViewItemType()) {
                        case ViewNode.VIEW_ITEM_TYPE_COLOR:
                            holder.image.setBackgroundColor(child.getViewItemColor());
                            break;
                        case ViewNode.VIEW_ITEM_TYPE_IMAGE_RES:
                            holder.image.setImageResource(child.getViewItemImageResId());
                            break;
                        case ViewNode.VIEW_ITEM_TYPE_IMAGE_URL:
                            if (!TextUtils.isEmpty(child.getImageUrl())) {
                                MyVolley.getImageLoader().get(child.getImageUrl(),
                                        ImageLoader.getImageListener(holder.image,
                                                R.drawable.ic_stub,
                                                R.drawable.ic_error));
                            }
                            break;
                        default:
                            break;
                    }
                    break;

                case ViewNode.VIEW_TYPE_LIST_TILES:

                    if (child.getWrapperViewResId() > 0) {
                        child.updateWrapperView(view, holder.wrapperViewHolder, position);
                    }

                    view.setBackgroundColor(randomColorForHeader(Math.abs(child.hashCode())));

                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(child, layoutInflater);
                    holder.spannableGrid.setAdapter(adapter);

                    if (child.getChildren().size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        new GetDataTask(child, null, null, adapter, null, true);
                    }

                    ItemClickSupport.addTo(holder.spannableGrid).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClick(RecyclerView parent, View view, int position, long id) {
                            // drill down
                            startViewItemActivity(child);
                        }
                    });
                    break;
                default:
                    break;
            }
			
			return view;
		}
	}

    private static class RecyclerViewAdapter extends RecyclerView.Adapter<GridItemViewHolder> {

        private ViewNode model;
        private LayoutInflater layoutInflater;

        public RecyclerViewAdapter(ViewNode model, LayoutInflater layoutInflater) {
            this.model = model;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public int getItemCount() {
            if (model.getChildren().size() > 0) {
                return calcAlbumPicCountForHeader(model.getChildren().size(), Math.abs(model.getChildren().get(0).hashCode()));
            } else {
                return 0;
            }
        }

        /* @Override */
        public Object getItem(int position) {
            return model.getChildren().get(position);
        }


        @Override
        public void onBindViewHolder(final GridItemViewHolder holder, int position) {
            final View itemView = holder.itemView;
            final SpannableGridLayoutManager.LayoutParams lp =
                    (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();

            final int[] SPANS = generateColRowSpans(getItemCount(), Math.abs(model.getChildren().get(0).hashCode()));

            int colSpan = SPANS[position*2];
            int rowSpan = SPANS[position*2+1];

            lp.rowSpan = rowSpan;
            lp.colSpan = colSpan;
            itemView.setLayoutParams(lp);

            final ViewNode child = (ViewNode)getItem(position);
            updateGridItemView(child, holder);
        }

        @Override
        public GridItemViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
            final View view = layoutInflater.inflate(R.layout.item_grid_image, parent, false);
            return new GridItemViewHolder(view);
        }
    };

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
			if (null != viewNode) {
				item.setVisible(viewNode.getViewType() == ViewNode.VIEW_TYPE_GRID);
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
			numColumns = getGridViewNumColumns(gridView) == 1 ? 3 : getGridViewNumColumns(gridView) - 1;
		} else if (numColumns > 3) {
			numColumns = getGridViewNumColumns(gridView) == 3 ? 1 : getGridViewNumColumns(gridView) - 1;
		}
		gridView.setNumColumns(numColumns);
	}
	
	protected void zoomGridView(boolean in, boolean circular) {
		if (viewNode.getViewType() == ViewNode.VIEW_TYPE_GRID) {
			View contentView = pager.findViewWithTag(pager.getCurrentItem());
			GridView gridView = (GridView) contentView.findViewById(R.id.ic_gridview);
			setGridViewColumns(gridView, in ?
					(getGridViewNumColumns(gridView) == 1 ? (circular ? 3 : 1) : getGridViewNumColumns(gridView) - 1) :
					(getGridViewNumColumns(gridView) == 3 ? (circular ? 1 : 3) : getGridViewNumColumns(gridView) + 1));
		}
	}

    private static final int[] generateColRowSpans(int itemCount, int hash) {
        final int[][][] SPANS = {
                {}, // 0
                {
                        {6, 6}
                }, // 1
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

    private static int calcAlbumPicCountForHeader(int albumPicCount, int hash) {
        final int[] ITEM_COUNT_FOR_LARGE_ALBUMS = {4, 5, 6, 9};
        switch (albumPicCount) {
            case 1:
                return 1;
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

    private static int getGridViewNumColumns(GridView gv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            return gv.getNumColumns();

        try {
            Field numColumns = gv.getClass().getSuperclass().getDeclaredField("mNumColumns");
            numColumns.setAccessible(true);
            return numColumns.getInt(gv);
        }
        catch (Exception e) {}

        int columns = gv.AUTO_FIT;
        if (gv.getChildCount() > 0) {
            int width = gv.getChildAt(0).getMeasuredWidth();
            if (width > 0) columns = gv.getWidth() / width;
        }
        return columns;
    }

    private static void updateGridItemView(ViewNode viewItem, GridItemViewHolder holder) {
        switch (viewItem.getViewItemType()) {
            case ViewNode.VIEW_ITEM_TYPE_COLOR:
                holder.itemView.setBackgroundColor(viewItem.getViewItemColor());
                holder.imageView.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.GONE);
                break;
            case ViewNode.VIEW_ITEM_TYPE_IMAGE_RES:
                holder.imageView.setVisibility(View.VISIBLE);
                holder.imageView.setImageResource(viewItem.getViewItemImageResId());
                holder.progressBar.setVisibility(View.GONE);
                break;
            case ViewNode.VIEW_ITEM_TYPE_IMAGE_URL:
                if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
                    holder.imageView.setVisibility(View.VISIBLE);
                    MyVolley.getImageLoader().get(viewItem.getImageUrl(),
                            ImageLoader.getImageListener(holder.imageView,
                                    R.drawable.ic_stub,
                                    R.drawable.ic_error));
//                    Ion.with(holder.imageView)
//                            .placeholder(R.drawable.ic_launcher)
//                            .error(R.drawable.ic_error)
//                            .load(viewItem.getImageUrl());
//					imageLoader.displayImage(viewItem.getImageUrl(), holder.imageView, displayImageOptions, new SimpleImageLoadingListener() {
//											 @Override
//											 public void onLoadingStarted(String imageUri, View view) {
//												 holder.progressBar.setProgress(0);
//												 holder.progressBar.setVisibility(View.VISIBLE);
//											 }
//
//											 @Override
//											 public void onLoadingFailed(String imageUri, View view,
//													 FailReason failReason) {
//												 holder.progressBar.setVisibility(View.GONE);
//											 }
//
//											 @Override
//											 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//												 holder.progressBar.setVisibility(View.GONE);
//											 }
//										 }, new ImageLoadingProgressListener() {
//											 @Override
//											 public void onProgressUpdate(String imageUri, View view, int current,
//													 int total) {
//												 holder.progressBar.setProgress(Math.round(100.0f * current / total));
//											 }
//										 }
//					);
                }
                break;
            default:
                break;
        }

        SpannableString text = buildPictureText(viewItem, true, true, false, false, false, false);
//        if (null != text && getGridViewNumColumns(gridView) < 3) {
//            holder.text.setVisibility(View.VISIBLE);
//            holder.text.setText(text);
////				holder.text.setMovementMethod(LinkMovementMethod.getInstance());
//        } else {
//            holder.text.setVisibility(View.GONE);
//        }
    }
}
