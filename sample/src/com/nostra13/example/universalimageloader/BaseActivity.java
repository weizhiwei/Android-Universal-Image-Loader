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
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
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
import com.wzw.ic.mvc.ViewNodeAction;
import com.wzw.ic.mvc.root.RootController;
import com.wzw.ic.mvc.root.RootViewNode;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class BaseActivity extends Activity {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ViewNode model;
	protected BaseController controller;
	protected Menu menu;
	private boolean wallpaperServiceEnabled = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (Build.VERSION.SDK_INT >= 11) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@SuppressLint("NewApi")
	public boolean isFullscreen() {
		return (getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
	}
	
	@SuppressLint("NewApi")
	public void setFullscreen(boolean fullscreen) {
		if (fullscreen) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
			
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		}
		
		if (Build.VERSION.SDK_INT >= 16) {
			View decorView = getWindow().getDecorView();
			if (fullscreen) {
	        	// Hide the status bar.
	        	int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
	        	decorView.setSystemUiVisibility(uiOptions);
	        } else {
			    int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
			    decorView.setSystemUiVisibility(uiOptions);
	        }
		}
		
		if (Build.VERSION.SDK_INT >= 11) {
			ActionBar actionBar = getActionBar();
			if (fullscreen) {
	        	actionBar.hide();
	        } else {
			    actionBar.show();
	        }
	    }
	}
	
	public void toggleFullscreen() {
		setFullscreen(!isFullscreen());
	}
	
	protected void setTitleIconFromViewItem(ViewItem viewItem) {
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
		setTitleIconFromViewItem(viewItem);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		if (null != model && null != model.getActions()) {
			for (ViewNodeAction action: model.getActions()) {
				MenuItem item = menu.add(Menu.NONE, action.getId(), Menu.FIRST, action.getTitle());
				item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
				item.setVisible(action.isVisible());
			}
		}
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item_wallpaper_toggle:
				wallpaperServiceEnabled = !wallpaperServiceEnabled;
				item.setChecked(wallpaperServiceEnabled);
				enableWallpaperService(wallpaperServiceEnabled);
				return true;
			default:
				if (null != model && null != model.getActions()) {
					for (ViewNodeAction action: model.getActions()) {
						if (action.getId() == item.getItemId()) {
							controller.startAction(this, model, action);
							return true;
						}
					}
				}
				return false;
		}
	}
	
	private void enableWallpaperService(boolean enabled) {
		WallpaperAlarmReceiver.enableWallpaperAlarms(this, enabled);
		
		ComponentName receiver = new ComponentName(this, BootReceiver.class);
		PackageManager pm = getPackageManager();
		pm.setComponentEnabledSetting(receiver,
		        enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		        PackageManager.DONT_KILL_APP);
	}
}
