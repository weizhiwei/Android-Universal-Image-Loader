package com.nostra13.example.universalimageloader;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.root.RootViewNode;

public class EntryActivity extends ViewItemPagerActivity {
	
	@Override
	public void onBackPressed() {
		imageLoader.stop();
//		IcDatabase.getInstance().close();
		super.onBackPressed();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= 11) {
			final ActionBar actionBar = getActionBar();
//			setHasEmbeddedTabs(actionBar, true);
		    for (int i = 0; i < parentModel.getViewItems().size(); i++) {
		    	ViewItem viewItem = parentModel.getViewItems().get(i);
		    	final Tab tab = actionBar.getTabAt(i);
//		    	tab.setText("我");
//		    	tab.setIcon(R.drawable.ic_gallery);
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
//							tab.setIcon(new BitmapDrawable(loadedImage));
						}
						
						@Override
						public void onLoadingCancelled(String imageUri, View view) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		}
	}
	
	@Override
	protected void setModelFromIntent() {
		parentModel = new RootViewNode();
		myViewItem = parentModel.getViewItems().get(0);
		model = myViewItem.getViewNode();
		updateTitleIconFromViewItem(myViewItem);
	}
}
