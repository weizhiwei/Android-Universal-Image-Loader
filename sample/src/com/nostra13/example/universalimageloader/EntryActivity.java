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
	}
	
	@Override
	protected void setModelFromIntent() {
		parentModel = new RootViewNode();
		myViewItem = parentModel.getViewItems().get(0);
		model = myViewItem.getViewNode();
		updateTitleIconFromViewItem(myViewItem);
	}
	
	@Override
	protected boolean hasEmbeddedTabs() {
		return true;
	}
	
	@Override
	protected void setTabTitleIcon(Tab tab, int position, ViewItem viewItem) {
		final int[] ICONS = new int[] {R.drawable.ic_pictures, R.drawable.ic_user, R.drawable.ic_gallery};
		tab.setIcon(ICONS[position]);
	}
}
