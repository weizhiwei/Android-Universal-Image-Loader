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

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.root.RootController;
import com.wzw.ic.mvc.root.RootViewNode;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class BaseActivity extends Activity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ViewNode model;
	protected BaseController controller;
	
	public void toggleFullscreen() {
		if (Build.VERSION.SDK_INT < 16) {
			int fs = WindowManager.LayoutParams.FLAG_FULLSCREEN;
			if ((getWindow().getAttributes().flags & fs) == 0) {
				getWindow().setFlags(fs, fs);
			} else {
				getWindow().clearFlags(fs);
			}
		} else {
        	View decorView = getWindow().getDecorView();
			ActionBar actionBar = getActionBar();
			if (actionBar.isShowing()) {
	        	// Hide the status bar.
	        	int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
	        	decorView.setSystemUiVisibility(uiOptions);
	        	// Remember that you should never show the action bar if the
	        	// status bar is hidden, so hide that too if necessary.
	        	actionBar.hide();
	        } else {
			    // Show the status bar.
			    int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
			    decorView.setSystemUiVisibility(uiOptions);
			    // Remember that you should never show the action bar if the
			    // status bar is hidden, so hide that too if necessary.
			    actionBar.show();
	        }
		}
	}
	
	protected void setModelControllerFromIntent() {
		Bundle bundle = getIntent().getExtras();
		ViewItem viewItem = null;
		if (null != bundle) {
			model = (ViewNode) bundle.getSerializable(Extra.MODEL);
			controller = (BaseController) bundle.getSerializable(Extra.CONTROLLER);
			viewItem = (ViewItem) bundle.getSerializable(Extra.VIEW_ITEM);
		}
		if (null == model || null == controller) {
			model = new RootViewNode();
			controller = new RootController();
		}
		if (null != viewItem) {
			setTitle(viewItem.getLabel());
			if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
				imageLoader.loadImage(viewItem.getImageUrl(), new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						ImageView logo = (ImageView) findViewById(android.R.id.home);
						logo.setImageBitmap(loadedImage);
					}
					
					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_clear_memory_cache:
				imageLoader.clearMemoryCache();
				return true;
			case R.id.item_clear_disc_cache:
				imageLoader.clearDiscCache();
				return true;
			default:
				return false;
		}
	}
}
