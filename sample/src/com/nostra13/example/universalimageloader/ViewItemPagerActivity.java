package com.nostra13.example.universalimageloader;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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

import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.root.RootViewNode;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.widget.SpannableGridLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

public class ViewItemPagerActivity extends BaseActivity {

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        final ViewNode viewNode;
        if (null != bundle) {
            viewNode = (ViewNode) bundle.getSerializable(Constants.Extra.VIEWNODE);
        } else {
            viewNode = RootViewNode.getInstance().getChildren().get(0);
        }
        final ViewNode parentNode = viewNode.getParent();

        setContentView(R.layout.ac_view_item_pager);

        final ViewPager pager = (ViewPager) findViewById(R.id.ic_viewitem_pagerview);
        final FancyCoverFlow coverFlow = (FancyCoverFlow) findViewById(R.id.coverflow);

        pager.setAdapter(new ViewItemPagerAdapter(parentNode, getLayoutInflater(), pager));
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position < coverFlow.getAdapter().getCount()) {
                    coverFlow.setSelection(position);
                }

                endlessScrollForPager(parentNode, position, pager.getAdapter(), (BaseAdapter) coverFlow.getAdapter());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        coverFlow.setReflectionEnabled(true);
        coverFlow.setReflectionRatio(0.3f);
        coverFlow.setReflectionGap(0);
        coverFlow.setAdapter(new CoverFlowAdapter(parentNode, getLayoutInflater()));
        coverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < pager.getAdapter().getCount()) {
                    pager.setCurrentItem(position);
                }

                endlessScrollForPager(parentNode, position, pager.getAdapter(), (BaseAdapter) coverFlow.getAdapter());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        pager.setCurrentItem(viewNode.getParent().getChildren().indexOf(viewNode));

        if (ViewNode.VIEW_TYPE_IMAGE == viewNode.getViewType(ViewNode.VIEW_TYPE_PAGER)) {

            setFullscreen(true);
        }
    }

    @Override
    public void setFullscreen(boolean fullscreen) {
        super.setFullscreen(fullscreen);

        // show/hide the coverflow
        final FancyCoverFlow coverFlow = (FancyCoverFlow) findViewById(R.id.coverflow);
        coverFlow.setVisibility(fullscreen ? View.GONE : View.VISIBLE);
    }

    private static void endlessScrollForPager(ViewNode viewNode, int position, final PagerAdapter pagerAdapter,
                                              final BaseAdapter coverFlowAdapter) {
        if (viewNode.supportPaging() && position >= viewNode.getChildren().size() - 5) {
            new GetDataTask(viewNode, pagerAdapter, new GetDataTask.GetDataTaskFinishedListener () {
                @Override
                public void onGetDataTaskFinished(ViewNode model) {
                    coverFlowAdapter.notifyDataSetChanged();
                }
            }, false);
        }
    }

    private static class CoverFlowAdapter extends FancyCoverFlowAdapter {

        private ViewNode model;
        private LayoutInflater layoutInflater;

        CoverFlowAdapter(ViewNode model, LayoutInflater layoutInflater) {
            this.model = model;
            this.layoutInflater = layoutInflater;
        }

        @Override
        public int getCount() {
            return model.getChildren().size();
        }

        @Override
        public Object getItem(int i) {
            return model.getChildren().get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
            ImageView imageView = null;

            if (reuseableView != null) {
                imageView = (ImageView) reuseableView;
            } else {
                imageView = new ImageView(viewGroup.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new FancyCoverFlow.LayoutParams(160, 160));
            }

            ViewNode viewItem = (ViewNode) getItem(i);

            imageView.setVisibility(View.INVISIBLE);
            if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
                imageView.setVisibility(View.VISIBLE);
                MyVolley.getImageLoader().get(viewItem.getImageUrl(),
                        ImageLoader.getImageListener(imageView,
                                R.drawable.ic_stub,
                                R.drawable.ic_error));
            }

            return imageView;
        }
    }

	public class ViewItemPagerAdapter extends PagerAdapter {

        private ViewNode model;
        private LayoutInflater layoutInflater;
        private ViewPager pager;

        private int lastPosition = -1;

		ViewItemPagerAdapter(ViewNode model, LayoutInflater layoutInflater, ViewPager pager) {
            this.model = model;
            this.layoutInflater = layoutInflater;
            this.pager = pager;
		}

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            // avoid being called many times
            if (lastPosition == position) {
                return;
            }
            lastPosition = position;

            ViewNode viewItem = (ViewNode) getItem(position);
            int viewType = viewItem.getViewType(ViewNode.VIEW_TYPE_PAGER);

            if ((viewType == ViewNode.VIEW_TYPE_LIST || viewType == ViewNode.VIEW_TYPE_GRID) &&
                viewItem.getChildren().isEmpty()) {
                View contentView = pager.findViewWithTag(pager.getCurrentItem());
                AbsListView absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
                BaseAdapter itemAdapter = (BaseAdapter) absListView.getAdapter();
                SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_swiperefresh);
                new GetDataTask(viewItem, swipeRefreshLayout, itemAdapter, null, null, true);

            } else if (viewType == ViewNode.VIEW_TYPE_IMAGE) {
                View contentView = pager.findViewWithTag(pager.getCurrentItem());
                ImageView imageView = (ImageView) contentView.findViewById(R.id.image);
                if (null == imageView.getDrawable()) {
                    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.ic_swiperefresh);
                    loadImage(viewItem, imageView, swipeRefreshLayout);
                }
            }
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
            final int viewType = child.getViewType(ViewNode.VIEW_TYPE_PAGER);
			
			View contentView = null;
            final SwipeRefreshLayout swipeRefreshLayout;
            final ImageView imageView;
			AbsListView absListView = null;
            final List<EndlessScrollListener> onScrollListeners = new ArrayList<>();
			final BaseAdapter itemAdapter;
            final RecyclerView.Adapter recyclerViewAdapter;
            final GetDataTask.GetDataTaskFinishedListener getDataTaskFinishedListener;

			switch (viewType) {
            case ViewNode.VIEW_TYPE_IMAGE:
                contentView = layoutInflater.inflate(R.layout.item_pager_image, view, false);
                imageView = (ImageView) contentView.findViewById(R.id.image);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleFullscreen();
                    }
                });
                itemAdapter = null;
                recyclerViewAdapter = null;
                getDataTaskFinishedListener = null;
                break;
			case ViewNode.VIEW_TYPE_LIST:
				contentView = layoutInflater.inflate(R.layout.ac_image_list, view, false);
                imageView = null;
                absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
				itemAdapter = new ListItemAdapter(child, (ListView) absListView, layoutInflater);
                ((ListView) absListView).setAdapter(itemAdapter);
                recyclerViewAdapter = null;
                getDataTaskFinishedListener = null;
                ((ListView) absListView).setDividerHeight(0);
                break;
			case ViewNode.VIEW_TYPE_GRID:
				contentView = layoutInflater.inflate(R.layout.ac_image_grid, view, false);
                imageView = null;
                absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
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
                imageView = null;
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
                imageView = null;
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

			swipeRefreshLayout.setEnabled(false);
            if (child.supportReloading()){
                if (null != imageView) {
                    swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            loadImage(child, imageView, swipeRefreshLayout);
                        }
                    });
                    swipeRefreshLayout.setEnabled(true);
                } else if(null != itemAdapter) {
                    swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            new GetDataTask(child, swipeRefreshLayout, itemAdapter, recyclerViewAdapter, getDataTaskFinishedListener, true);
                        }
                    });
                    swipeRefreshLayout.setEnabled(true);
                }
            }
			// Configure the refreshing colors
//			swipeRefreshLayout.setColorScheme(android.R.color.holo_blue_bright,
//	                android.R.color.holo_green_light,
//	                android.R.color.holo_orange_light,
//	                android.R.color.holo_red_light);

			if (child.supportPaging()) {
                if (null != itemAdapter) {
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
			}

            if (null != absListView) {
                absListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // drill down
                        startViewItemActivity((ViewNode)itemAdapter.getItem(position));
                    }
                });

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
            return ViewNode.VIEW_TYPE_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            return ((ViewNode)getItem(position)).getViewType(ViewNode.VIEW_TYPE_LIST);
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
            final ListItemViewHolder holder;
            final ViewNode child = (ViewNode)getItem(position);
            final int viewType = getItemViewType(position);

            if (view == null) {

				switch (viewType) {

				case ViewNode.VIEW_TYPE_SIMPLE:
					view = layoutInflater.inflate(R.layout.item_list_image, parent, false);
                    holder = new ListItemViewHolder(view);
                    holder.text = (TextView) view.findViewById(R.id.text);
					holder.image = (ImageView) view.findViewById(R.id.image);
					break;

                case ViewNode.VIEW_TYPE_TILE:
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
                        holder.wrapperViewHolder.body.addView(view);

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
            switch (viewType) {

                case ViewNode.VIEW_TYPE_SIMPLE:

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

                case ViewNode.VIEW_TYPE_TILE:

                    view.setBackgroundColor(randomColorForHeader(Math.abs(child.hashCode())));

                    RecyclerViewAdapter adapter = new RecyclerViewAdapter(child, layoutInflater);
                    holder.spannableGrid.setAdapter(adapter);

                    GetDataTask.GetDataTaskFinishedListener updateWrapperView = new GetDataTask.GetDataTaskFinishedListener() {
                        @Override
                        public void onGetDataTaskFinished(ViewNode model) {
                            if(model.getWrapperViewResId()>0) {
                                model.updateWrapperView(holder.wrapperViewHolder);
                            }
                        }
                    };

                    updateWrapperView.onGetDataTaskFinished(child);
                    if (child.getChildren().size() > 0) {
                        adapter.notifyDataSetChanged();
                    } else {
                        new GetDataTask(child, null, null, adapter, updateWrapperView, true);
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
//			if (null != viewNode) {
//				item.setVisible(viewNode.getViewType() == ViewNode.VIEW_TYPE_GRID);
//			}
		}

//		ViewNode viewItem = viewNode.getSibling(pager.getCurrentItem());
//
//		MenuItem heartsItem = menu.findItem(R.id.item_hearts_toggle);
//		heartsItem.setVisible(true);
//		if (viewItem.isHeartsOn()) {
//    		heartsItem.setTitle(R.string.hearts_on);
//    		heartsItem.setIcon(R.drawable.ic_hearts_on);
//		} else {
//    		heartsItem.setTitle(R.string.hearts_off);
//    		heartsItem.setIcon(R.drawable.ic_hearts_off);
//		}
//
//		MenuItem shareItem = menu.findItem(R.id.item_action_share);
//		shareItem.setVisible(true);
//		ShareActionProvider shareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
//	    Intent intent = new Intent(Intent.ACTION_SEND);
//	    intent.setType("image/*");
////	    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageLoader.getDiskCache().get(viewItem.getImageUrl()))); // TODO null check
//	    shareActionProvider.setShareIntent(intent);
//
//	    /*
//	     * ArrayList<Uri> imageUris = new ArrayList<Uri>();
//imageUris.add(imageUri1); // Add your image URIs here
//imageUris.add(imageUri2);
//
//Intent shareIntent = new Intent();
//shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//shareIntent.setType("image/*");
//	     */
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
//		if (viewNode.getViewType() == ViewNode.VIEW_TYPE_GRID) {
//			View contentView = pager.findViewWithTag(pager.getCurrentItem());
//			GridView gridView = (GridView) contentView.findViewById(R.id.ic_gridview);
//			setGridViewColumns(gridView, in ?
//					(getGridViewNumColumns(gridView) == 1 ? (circular ? 3 : 1) : getGridViewNumColumns(gridView) - 1) :
//					(getGridViewNumColumns(gridView) == 3 ? (circular ? 1 : 3) : getGridViewNumColumns(gridView) + 1));
//		}
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

    private static void loadImage(ViewNode viewNode, ImageView imageView, SwipeRefreshLayout swipeRefreshLayout) {
        if (null != swipeRefreshLayout) {
            swipeRefreshLayout.setRefreshing(true);
        }
        MyVolley.getImageLoader().get(viewNode.getImageUrl(),
                ImageLoader.getImageListener(imageView,
                        R.drawable.ic_stub,
                        R.drawable.ic_error));
        swipeRefreshLayout.setRefreshing(false);
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

    public void startViewItemActivity(ViewNode node) {
        Intent intent = new Intent(this, ViewItemPagerActivity.class);
        intent.putExtra(Constants.Extra.VIEWNODE, node);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (1 == requestCode) {
            if (RESULT_OK == resultCode) {
                int position = data.getIntExtra("pager_current_item", -1);
                if (-1 != position) {
                    final ViewPager pager = (ViewPager) findViewById(R.id.ic_viewitem_pagerview);
                    View contentView = pager.findViewWithTag(pager.getCurrentItem());
                    AbsListView absListView = (AbsListView) contentView.findViewById(R.id.ic_listview);
                    if (null != absListView) {
                        absListView.setSelection(position);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        final ViewPager pager = (ViewPager) findViewById(R.id.ic_viewitem_pagerview);
        Intent intent = new Intent();
        intent.putExtra("pager_current_item", pager.getCurrentItem());
        setResult(RESULT_OK, intent);

        super.onBackPressed();
    }
}
