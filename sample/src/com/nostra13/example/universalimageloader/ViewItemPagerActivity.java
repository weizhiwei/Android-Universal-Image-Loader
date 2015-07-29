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
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.SpannableGridLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
		final PagerAdapter pagerAdapter = new ViewItemPagerAdapter();
		pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener () {
			@Override
		    public void onPageSelected(int position) {
                updateCurrentPage();
				if (parentModel.supportPaging() && position >= pagerAdapter.getCount() - 5) {
					new GetDataTask(ViewItemPagerActivity.this, parentModel, pagerAdapter, new GetDataTask.GetDataTaskFinishedListener() {
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

	private void updateCurrentPage() {
        int position = pager.getCurrentItem();

        myViewItem = parentModel.getViewItems().get(position);
        model = myViewItem.getViewNode();

        updateMenu(model);

        ActionBar actionBar = getSupportActionBar();
        setActionBarSelection(actionBar, position);

        if (model.supportReloading() && model.getViewItems().isEmpty()) {
        	View contentView = pager.findViewWithTag(position);
        	SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_swiperefresh);
            if (!swipeRefreshLayout.isRefreshing()) {
                AbsListView absListView = null;
                BaseAdapter itemAdapter = null;
                RecyclerView.Adapter recyclerViewAdapter = null;
                GetDataTask.GetDataTaskFinishedListener getDataTaskFinishedListener = null;
                switch (myViewItem.getViewType()) {
                    case ViewItem.VIEW_TYPE_LIST:
                    case ViewItem.VIEW_TYPE_CARD_LIST:
                    case ViewItem.VIEW_TYPE_STORY_LIST:
                        absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
                        itemAdapter = (BaseAdapter) absListView.getAdapter();
                        break;
                    case ViewItem.VIEW_TYPE_GRID:
                        absListView = (AbsListView) contentView.findViewById(R.id.ic_gridview);
                        itemAdapter = (BaseAdapter) absListView.getAdapter();
                        break;
                    case ViewItem.VIEW_TYPE_WEBVIEW:
                        final WebView webView = (WebView) contentView.findViewById(R.id.ic_webview);
                        getDataTaskFinishedListener = new GetDataTask.GetDataTaskFinishedListener () {

                            @Override
                            public void onGetDataTaskFinished(ViewNode model) {
                                List<ViewItem> viewItems = model.getViewItems();
                                if (null != viewItems && !viewItems.isEmpty()) {
                                    webView.loadUrl(viewItems.get(0).getNodeUrl());
                                }
                            }
                        };
                        break;
                    default:
                        break;
                }

                new GetDataTask(this, model, swipeRefreshLayout, itemAdapter, recyclerViewAdapter, getDataTaskFinishedListener, true);
            }
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
		public Object instantiateItem(ViewGroup view, final int position) {
			final ViewItem viewItem = parentModel.getViewItems().get(position);
			final ViewNode childModel = viewItem.getViewNode();
			
			View contentView = null;
            final SwipeRefreshLayout swipeRefreshLayout;
			AbsListView absListView = null;
            final List<EndlessScrollListener> onScrollListeners = new ArrayList<EndlessScrollListener>();
			final BaseAdapter itemAdapter;
            final RecyclerView.Adapter recyclerViewAdapter;
            final GetDataTask.GetDataTaskFinishedListener getDataTaskFinishedListener;

			switch (viewItem.getViewType()) {
			case ViewItem.VIEW_TYPE_LIST:
            case ViewItem.VIEW_TYPE_CARD_LIST:
            case ViewItem.VIEW_TYPE_STORY_LIST:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_list, view, false);
				absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = new ListItemAdapter(childModel, viewItem.getViewType(), (ListView) absListView);
                ((ListView) absListView).setAdapter(itemAdapter);
                recyclerViewAdapter = null;
                getDataTaskFinishedListener = null;
                ((ListView) absListView).setDividerHeight(0);
                break;
			case ViewItem.VIEW_TYPE_GRID:
				contentView = getLayoutInflater().inflate(R.layout.ac_image_grid, view, false);
				absListView = (AbsListView) contentView.findViewById(R.id.ic_gridview);
				itemAdapter = new GridItemAdapter(childModel, (GridView) absListView);
                ((GridView) absListView).setAdapter(itemAdapter);
                recyclerViewAdapter = null;
                getDataTaskFinishedListener = null;
				if (viewItem.getInitialZoomLevel() > 0 && viewItem.getInitialZoomLevel() <= 3) {
					((GridView) absListView).setNumColumns(viewItem.getInitialZoomLevel());
				}
				break;
            case ViewItem.VIEW_TYPE_WEBVIEW:
                contentView = getLayoutInflater().inflate(R.layout.ac_web_view, view, false);
                itemAdapter = null;
                recyclerViewAdapter = null;
                final WebView webView = (WebView) contentView.findViewById(R.id.ic_webview);
                webView.setWebViewClient(new WebViewClient());
                getDataTaskFinishedListener = new GetDataTask.GetDataTaskFinishedListener () {

                    @Override
                    public void onGetDataTaskFinished(ViewNode model) {
                        List<ViewItem> viewItems = model.getViewItems();
                        if (null != viewItems && !viewItems.isEmpty()) {
                            webView.loadUrl(viewItems.get(0).getNodeUrl());
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
						new GetDataTask(ViewItemPagerActivity.this, childModel, swipeRefreshLayout, itemAdapter, recyclerViewAdapter, getDataTaskFinishedListener, true);
		            }
			});
			swipeRefreshLayout.setEnabled(childModel.supportReloading());
			// Configure the refreshing colors
//			swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
//	                android.R.color.holo_green_light,
//	                android.R.color.holo_orange_light,
//	                android.R.color.holo_red_light);

			if (childModel.supportPaging()) {
				onScrollListeners.add(new EndlessScrollListener() {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        new GetDataTask(ViewItemPagerActivity.this, childModel, swipeRefreshLayout, itemAdapter, recyclerViewAdapter, new GetDataTask.GetDataTaskFinishedListener() {

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
                        childModel.onViewItemClicked((ViewItem) itemAdapter.getItem(position), ViewItemPagerActivity.this);
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

	private static class GridViewHolder {
		ImageView imageView;
		ProgressBar progressBar;
		TextView text;
	}
	
	private class GridItemAdapter extends BaseAdapter {
		
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
			ViewItem viewItem = model.getViewItems().get(position);
			return viewItem;
		}

		@Override
		public long getItemId(int position) {
			Object item = getItem(position);
			return item == null ? 0 : item.hashCode();
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
			
			final ViewItem viewItem = model.getViewItems().get(position);
			
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
			if (null != text && getGridViewNumColumns(gridView) < 3) {
				holder.text.setVisibility(View.VISIBLE);
				holder.text.setText(text);
//				holder.text.setMovementMethod(LinkMovementMethod.getInstance());
			} else {
				holder.text.setVisibility(View.GONE);
			}
			
			return view;
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
            ViewItem item = (ViewItem)getItem(position);
            if (null == item) {
                return 0;
            } else {
                return item.getViewType() == ViewItem.VIEW_TYPE_IMAGE_PAGER ? 0 : 1;
            }
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

                    ViewGroup.LayoutParams lp = holder.image.getLayoutParams();
                    lp.height = listView.getHeight();
                    holder.image.setLayoutParams(lp);

                    if (model.getHeaderViewResId(position, getItemViewType(position)) > 0) {
                        holder.headerViewHolder = model.createHolderFromHeaderView(
                                getLayoutInflater().inflate(model.getHeaderViewResId(position, getItemViewType(position)), parent, false)
                        );

                        FrameLayout cardView = new FrameLayout(ViewItemPagerActivity.this);
                        cardView.setLayoutParams(new ListView.LayoutParams(
                                ListView.LayoutParams.FILL_PARENT, ListView.LayoutParams.WRAP_CONTENT));

                        if (null != view.getParent()) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                        cardView.addView(view);
                        if (null != holder.headerViewHolder.header) {
                            if (null != holder.headerViewHolder.header.getParent()) {
                                ((ViewGroup) holder.headerViewHolder.header.getParent()).removeView(holder.headerViewHolder.header);
                            }

                            FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT,
                                    Gravity.LEFT | Gravity.TOP);
                            holder.headerViewHolder.header.setLayoutParams(lp2);
                            cardView.addView(holder.headerViewHolder.header);
                            ((FrameLayout.LayoutParams) holder.headerViewHolder.header.getLayoutParams()).setMargins(20, listView.getHeight() - 200, 0, 0);

                            holder.headerViewHolder.header.setBackgroundColor(randomColorForHeader(Math.abs((new Random()).nextInt())) - 0x55000000);
                        }

                        view = cardView;
                    }
                    break;

				case ViewItem.VIEW_TYPE_CARD_LIST:
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
                                        ListView.LayoutParams.MATCH_PARENT, (listView.getWidth())
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

                        if (null != holder.headerViewHolder.header) {
                            if (null != holder.headerViewHolder.header.getParent()) {
                                ((ViewGroup) holder.headerViewHolder.header.getParent()).removeView(holder.headerViewHolder.header);
                            }
                            cardView.addView(holder.headerViewHolder.header);
                        }
                        if (null != view.getParent()) {
                            ((ViewGroup) view.getParent()).removeView(view);
                        }
                        cardView.addView(view);
                        if (null != holder.headerViewHolder.footer) {
                            if (null != holder.headerViewHolder.footer.getParent()) {
                                ((ViewGroup) holder.headerViewHolder.footer.getParent()).removeView(holder.headerViewHolder.footer);
                            }
                            cardView.addView(holder.headerViewHolder.footer);
                        }
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

            if (viewType == ViewItem.VIEW_TYPE_LIST) {
				
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
//                        Ion.with(holder.image)
//                            .placeholder(R.drawable.ic_launcher)
//                            .error(R.drawable.ic_error)
//                            .load(viewItem.getImageUrl());
                          MyVolley.getImageLoader().get(viewItem.getImageUrl(),
                                ImageLoader.getImageListener(holder.image,
                                        R.drawable.ic_stub,
                                        R.drawable.ic_error));
					}
					break;
				default:
					break;
				}
			} else if (viewType == ViewItem.VIEW_TYPE_STORY_LIST) {
                if (model.getHeaderViewResId(position, getItemViewType(position)) > 0) {
                    model.updateHeaderView(view, holder.headerViewHolder, position);
                }

                final ViewItem viewItem = model.getViewItems().get(position);

                view.setBackgroundColor(randomColorForHeader(Math.abs(viewItem.hashCode())));

                SpannableString text = buildPictureText(viewItem, true, false, true, true, true, false);
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
                            MyVolley.getImageLoader().get(viewItem.getImageUrl(),
                                    ImageLoader.getImageListener(holder.image,
                                            R.drawable.ic_stub,
                                            R.drawable.ic_error));
//                            Ion.with(holder.image)
//                                    .placeholder(R.drawable.ic_launcher)
//                                    .error(R.drawable.ic_error)
//                                    .load(viewItem.getImageUrl());
//                            imageLoader.displayImage(viewItem.getImageUrl(), holder.image, displayImageOptions, new SimpleImageLoadingListener() {
//                                        @Override
//                                        public void onLoadingStarted(String imageUri, View view) {
//                                            holder.progressBar.setProgress(0);
//                                            holder.progressBar.setVisibility(View.VISIBLE);
//                                        }
//
//                                        @Override
//                                        public void onLoadingFailed(String imageUri, View view,
//                                                                    FailReason failReason) {
//                                            holder.progressBar.setVisibility(View.GONE);
//                                        }
//
//                                        @Override
//                                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                            holder.progressBar.setVisibility(View.GONE);
//                                        }
//                                    }, new ImageLoadingProgressListener() {
//                                        @Override
//                                        public void onProgressUpdate(String imageUri, View view, int current,
//                                                                     int total) {
//                                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
//                                        }
//                                    }
//                            );
                        }
                        break;
                    default:
                        break;
                }
            } else if (viewType == ViewItem.VIEW_TYPE_CARD_LIST) {

                if (model.getHeaderViewResId(position, getItemViewType(position)) > 0) {
                    model.updateHeaderView(view, holder.headerViewHolder, position);
                }

                final ViewItem viewItem = model.getViewItems().get(position);

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
                                MyVolley.getImageLoader().get(viewItem.getImageUrl(),
                                        ImageLoader.getImageListener(holder.image,
                                                R.drawable.ic_stub,
                                                R.drawable.ic_error));
//                                imageLoader.displayImage(viewItem.getImageUrl(), holder.image, displayImageOptions, new SimpleImageLoadingListener() {
//                                            @Override
//                                            public void onLoadingStarted(String imageUri, View view) {
//                                                holder.progressBar.setProgress(0);
//                                                holder.progressBar.setVisibility(View.VISIBLE);
//                                            }
//
//                                            @Override
//                                            public void onLoadingFailed(String imageUri, View view,
//                                                                        FailReason failReason) {
//                                                holder.progressBar.setVisibility(View.GONE);
//                                            }
//
//                                            @Override
//                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                                holder.progressBar.setVisibility(View.GONE);
//                                            }
//                                        }, new ImageLoadingProgressListener() {
//                                            @Override
//                                            public void onProgressUpdate(String imageUri, View view, int current,
//                                                                         int total) {
//                                                holder.progressBar.setProgress(Math.round(100.0f * current / total));
//                                            }
//                                        }
//                                );
                            }
                            break;
                        default:
                            break;
                    }

                } else if (1 == getItemViewType(position)) {

                    ViewNode model2 = model.getViewItems().get(position).getViewNode();

                    RecyclerView.Adapter<SimpleViewHolder> adapter = new RecyclerViewAdapter(model2, holder.spannableGrid);
                    holder.spannableGrid.setAdapter(adapter);

                    if (null != model2.getViewItems() && model2.getViewItems().size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        new GetDataTask(ViewItemPagerActivity.this, model2, null, null, adapter, null, true);
                    }

                    ItemClickSupport.addTo(holder.spannableGrid).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClick(RecyclerView parent, View child, int position, long id) {
                            // drill down
                            model.onViewItemClicked(
                                    (ViewItem)model.getViewItems().get(position), ViewItemPagerActivity.this);
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

    private class RecyclerViewAdapter extends RecyclerView.Adapter<SimpleViewHolder> {

        private ViewNode model;
        private RecyclerView recyclerView;

        public RecyclerViewAdapter(ViewNode model, RecyclerView recyclerView) {
            this.model = model;
            this.recyclerView = recyclerView;
        }

        @Override
        public int getItemCount() {
            if (null != model.getViewItems() && model.getViewItems().size() > 0) {
                return calcAlbumPicCountForHeader(model.getViewItems().size(), Math.abs(model.getViewItems().get(0).hashCode()));
            } else {
                return 0;
            }
        }

        @Override
        public void onBindViewHolder(final SimpleViewHolder holder, int position) {
            final View itemView = holder.itemView;
            final SpannableGridLayoutManager.LayoutParams lp =
                    (SpannableGridLayoutManager.LayoutParams) itemView.getLayoutParams();

            final int[] SPANS = generateColRowSpans(getItemCount(), Math.abs(model.getViewItems().get(0).hashCode()));

            int colSpan = SPANS[position*2];
            int rowSpan = SPANS[position*2+1];

            lp.rowSpan = rowSpan;
            lp.colSpan = colSpan;
            itemView.setLayoutParams(lp);

            final ViewItem viewItem = model.getViewItems().get(position);
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
                        MyVolley.getImageLoader().get(viewItem.getImageUrl(),
                                ImageLoader.getImageListener(holder.imageView,
                                        R.drawable.ic_stub,
                                        R.drawable.ic_error));
//                        Ion.with(holder.imageView)
//                                .placeholder(R.drawable.ic_launcher)
//                                .error(R.drawable.ic_error)
//                                .load(viewItem.getImageUrl());
//                                        imageLoader.displayImage(viewItem.getImageUrl(), holder.imageView, displayImageOptions, new SimpleImageLoadingListener() {
//                                                    @Override
//                                                    public void onLoadingStarted(String imageUri, View view) {
//                                                        holder.progressBar.setProgress(0);
//                                                        holder.progressBar.setVisibility(View.VISIBLE);
//                                                    }
//
//                                                    @Override
//                                                    public void onLoadingFailed(String imageUri, View view,
//                                                                                FailReason failReason) {
//                                                        holder.progressBar.setVisibility(View.GONE);
//                                                    }
//
//                                                    @Override
//                                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                                                        holder.progressBar.setVisibility(View.GONE);
//                                                    }
//                                                }, new ImageLoadingProgressListener() {
//                                                    @Override
//                                                    public void onProgressUpdate(String imageUri, View view, int current,
//                                                                                 int total) {
//                                                        holder.progressBar.setProgress(Math.round(100.0f * current / total));
//                                                    }
//                                                }
//                                        );
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
			numColumns = getGridViewNumColumns(gridView) == 1 ? 3 : getGridViewNumColumns(gridView) - 1;
		} else if (numColumns > 3) {
			numColumns = getGridViewNumColumns(gridView) == 3 ? 1 : getGridViewNumColumns(gridView) - 1;
		}
		gridView.setNumColumns(numColumns);
	}
	
	protected void zoomGridView(boolean in, boolean circular) {
		if (myViewItem.getViewType() == ViewItem.VIEW_TYPE_GRID) {
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

    private static int calcAlbumPicCountForHeader(int albumPicCount, int hash) {
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
}
