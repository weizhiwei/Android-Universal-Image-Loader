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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.koushikdutta.ion.Ion;
import com.wzw.ic.mvc.ViewItem;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImagePagerActivity extends BaseActivity {

	ViewPager pager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_pager);
		
		setModelFromIntent();
		
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;

		pager = (ViewPager) findViewById(R.id.ic_pagerview);
//		pager.setOffscreenPageLimit(3);
		final PagerAdapter pagerAdapter = new ImagePagerAdapter();
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener () {
			@Override
		    public void onPageSelected(int position) {
				if (parentModel.supportPaging() && position >= pagerAdapter.getCount() - 5) {
					new GetDataTask(ImagePagerActivity.this, parentModel, pagerAdapter, null, false);
				}
			}
		});
		pager.setCurrentItem((null != parentModel && null != parentModel.getViewItems()) ? parentModel.getViewItems().indexOf(myViewItem) : 0);
		
		setFullscreen(true);
	}

//	@Override
//	public boolean onPrepareOptionsMenu(Menu menu) {		
//		return super.onPrepareOptionsMenu(menu);
//	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_hearts_toggle:
			{
				ViewItem viewItem = parentModel.getViewItems().get(pager.getCurrentItem());
				viewItem.setHeartsOn(!viewItem.isHeartsOn());
				if (viewItem.isHeartsOn()) {
					IcDatabase.getInstance().addViewItemToHearts(viewItem);
				} else {
					IcDatabase.getInstance().removeViewItemFromHearts(viewItem);
				}
	        	updateMenu();
				return true;
			}
			case R.id.item_set_wallpaper:
			{
				ViewItem viewItem = parentModel.getViewItems().get(pager.getCurrentItem());
				WallpaperAlarmReceiver.setWallpaper(this, viewItem.getImageUrl());
				return true;
			}
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@SuppressLint("NewApi")
	private void updateMenu() {
		if (null == menu)
			return;
		
		ViewItem viewItem = parentModel.getViewItems().get(pager.getCurrentItem());
		
		MenuItem heartsItem = menu.findItem(R.id.item_hearts_toggle);
		heartsItem.setVisible(true);
		if (viewItem.isHeartsOn()) {
    		heartsItem.setTitle(R.string.hearts_on);
    		heartsItem.setIcon(R.drawable.ic_hearts_on);
		} else {
    		heartsItem.setTitle(R.string.hearts_off);
    		heartsItem.setIcon(R.drawable.ic_hearts_off);
		}
		
		MenuItem shareItem = menu.findItem(R.id.item_action_share);
		shareItem.setVisible(true);
		ShareActionProvider shareActionProvider = (ShareActionProvider)shareItem.getActionProvider();
	    Intent intent = new Intent(Intent.ACTION_SEND);
	    intent.setType("image/*");
//	    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageLoader.getDiskCache().get(viewItem.getImageUrl()))); // TODO null check
	    shareActionProvider.setShareIntent(intent);
	    
	    /*
	     * ArrayList<Uri> imageUris = new ArrayList<Uri>();
imageUris.add(imageUri1); // Add your image URIs here
imageUris.add(imageUri2);

Intent shareIntent = new Intent();
shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
shareIntent.setType("image/*");
	     */
	    
	    MenuItem setWallpaperItem = menu.findItem(R.id.item_set_wallpaper);
		setWallpaperItem.setVisible(true);
	}
	
	private class ImagePagerAdapter extends PagerAdapter {
		
		private LayoutInflater inflater;

		ImagePagerAdapter() {
			inflater = getLayoutInflater();
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

	        View imageLayout = (View) object;
	        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
	        
	        if (!isFullscreen()) {
	        	updateTitleIconFromViewItem(myViewItem);
	        	updateMenu();
	        	
	        	final TextView textView = (TextView) imageLayout.findViewById(R.id.story);
	        	if (!TextUtils.isEmpty(textView.getText())) {
					textView.setVisibility(View.VISIBLE);
	        	}
	        	
	        	final ImageView originIconImageView = (ImageView) imageLayout.findViewById(R.id.origin_icon);
	        	if (!TextUtils.isEmpty(myViewItem.getOrigin())) {
	        		originIconImageView.setVisibility(View.VISIBLE);
	        	}
	        }
	    }
		
		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			imageLayout.setTag(position);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			final TextView textView = (TextView) imageLayout.findViewById(R.id.story);
			final ImageView originIconImageView = (ImageView) imageLayout.findViewById(R.id.origin_icon);

			final ViewItem viewItem = parentModel.getViewItems().get(position);
			viewItem.setHeartsOn(IcDatabase.getInstance().isViewItemInHearts(viewItem));
			
			SpannableString text = buildPictureText(viewItem, true, true, true, true, true, true);
			if (null != text) {
				textView.setText(text);
				textView.setMovementMethod(LinkMovementMethod.getInstance());
			}
			
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (View.VISIBLE != textView.getVisibility() && !TextUtils.isEmpty(textView.getText())) {
						textView.setVisibility(View.VISIBLE);
					} else {
						textView.setVisibility(View.GONE);
					}
					if (View.VISIBLE != originIconImageView.getVisibility() && !TextUtils.isEmpty(viewItem.getOrigin())) {
						originIconImageView.setVisibility(View.VISIBLE);
					} else {
						originIconImageView.setVisibility(View.GONE);
					}
					toggleFullscreen();
		        	updateMenu();
				}
			});

//            Ion.with(imageView)
//                    .placeholder(R.drawable.ic_launcher)
//                    .error(R.drawable.ic_error)
//                    .load(viewItem.getImageUrl());

            MyVolley.getImageLoader().get(viewItem.getImageUrl(),
                    ImageLoader.getImageListener(imageView,
                            R.drawable.ic_stub,
                            R.drawable.ic_error));

//			imageLoader.displayImage(viewItem.getImageUrl(), imageView, options, new SimpleImageLoadingListener() {
//				@Override
//				public void onLoadingStarted(String imageUri, View view) {
//					spinner.setVisibility(View.VISIBLE);
//				}
//
//				@Override
//				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//					String message = null;
//					switch (failReason.getType()) {
//						case IO_ERROR:
//							message = "Input/Output error";
//							break;
//						case DECODING_ERROR:
//							message = "Image can't be decoded";
//							break;
//						case NETWORK_DENIED:
//							message = "Downloads are denied";
//							break;
//						case OUT_OF_MEMORY:
//							message = "Out Of Memory error";
//							break;
//						case UNKNOWN:
//							message = "Unknown error";
//							break;
//					}
//					Toast.makeText(ImagePagerActivity.this, message, Toast.LENGTH_SHORT).show();
//
//					spinner.setVisibility(View.GONE);
//				}
//
//				@Override
//				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//					spinner.setVisibility(View.GONE);
//				}
//			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}
}