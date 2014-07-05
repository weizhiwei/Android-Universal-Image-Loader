/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.nostra13.example.universalimageloader;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.wzw.ic.controller.BaseController;
import com.wzw.ic.model.ViewItem;
import com.wzw.ic.model.ViewNode;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageListActivity extends AbsListViewBaseActivity {

	DisplayImageOptions options;

	ItemAdapter mItemAdapter;

	ViewNode model;
	BaseController controller;

	private PullToRefreshListView mPullRefreshListView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_list);

		Bundle bundle = getIntent().getExtras();
		model = (ViewNode) bundle.getSerializable(Extra.IMAGES);
		controller = (BaseController) bundle.getSerializable(Extra.CONTROLLER);

		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.considerExifParams(true)
			.displayer(new RoundedBitmapDisplayer(20))
			.build();

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
		mPullRefreshListView.setMode(model.supportPaging() ? Mode.BOTH : Mode.PULL_FROM_START);
		
		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				new GetDataTask().execute(true);
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// Do work to refresh the list here.
				new GetDataTask().execute(false);
			}
		});

//		// Add an end-of-list listener
//		mPullRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {
//
//			@Override
//			public void onLastItemVisible() {
//				Toast.makeText(ImageListActivity.this, "End of List!", Toast.LENGTH_SHORT).show();
//				// Do work to refresh the list here.
//				new GetDataTask().execute(++currentPage, false);
//			}
//		});

		listView = mPullRefreshListView.getRefreshableView();
		
		listView = (ListView) findViewById(android.R.id.list);
		mItemAdapter = new ItemAdapter();
		((ListView) listView).setAdapter(mItemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				controller.startItemView(ImageListActivity.this, model, position);
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && mItemAdapter.getCount() == 0) {
			mPullRefreshListView.setRefreshing();
			new GetDataTask().execute(true);
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
			mItemAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(result);
		}
	}
	
	@Override
	public void onBackPressed() {
		AnimateFirstDisplayListener.displayedImages.clear();
		super.onBackPressed();
	}

	private static class ViewHolder {
		TextView text;
		ImageView image;
	}

	class ItemAdapter extends BaseAdapter {

		private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

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
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(R.layout.item_list_image, parent, false);
				holder = new ViewHolder();
				holder.text = (TextView) view.findViewById(R.id.text);
				holder.image = (ImageView) view.findViewById(R.id.image);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			ViewItem viewItem = model.getViewItems().get(position);
			
			holder.text.setText(viewItem.getLabel());

			imageLoader.displayImage(viewItem.getImageUrl(), holder.image, options, animateFirstListener);

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
}