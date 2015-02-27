package com.nostra13.example.universalimageloader;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
		parentModel = RootViewNode.getInstance();
		myViewItem = parentModel.getViewItems().get(0);
		model = myViewItem.getViewNode();
		updateTitleIconFromViewItem(myViewItem);
	}
	
	@Override
	protected void initActionBar(final ActionBar actionBar) {
	    // Specify that tabs should be displayed in the action bar.
//	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//
//	    actionBar.setListNavigationCallbacks(new BaseAdapter () {
//
//			@Override
//			public int getCount() {
//				return null == parentModel.getViewItems() ? 0 : parentModel.getViewItems().size();
//			}
//
//			@Override
//			public Object getItem(int position) {
//				return parentModel.getViewItems().get(position);
//			}
//
//			@Override
//			public long getItemId(int position) {
//				return parentModel.getViewItems().get(position).hashCode();
//			}
//
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				ViewItem viewItem = parentModel.getViewItems().get(position);
//				TextView textView = new TextView(EntryActivity.this);
//				textView.setText(viewItem.getLabel());
//				textView.setTextSize(16);
////				textView.setCompoundDrawablesWithIntrinsicBounds(viewItem.getViewItemImageResId(), 0, 0, 0);
////				textView.setCompoundDrawablePadding(20);
//				textView.setTextColor(Color.WHITE);
//				textView.setGravity(Gravity.CENTER_VERTICAL);
//				textView.setHeight(120);
//				textView.setPadding(30, 0, 10, 0);
//				if (actionBar.getSelectedNavigationIndex() == position &&
//					!(parent instanceof Spinner)) {
//					textView.setBackgroundColor(0xFFAAAAFF);
//				}
//				return textView;
//		    }
//
//	    }, new OnNavigationListener () {
//
//			@Override
//			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
//
//				if (null != pager) {
//					pager.setCurrentItem(itemPosition);
//					return true;
//				}
//				return false;
//			}
//
//	    });
		
		actionBar.setDisplayShowHomeEnabled(false);
		setHasEmbeddedTabs(actionBar, true);
		super.initActionBar(actionBar);
		actionBar.getTabAt(0).setText(null);
		actionBar.getTabAt(0).setIcon(R.drawable.ic_pictures);
		actionBar.getTabAt(1).setText(null);
		actionBar.getTabAt(1).setIcon(R.drawable.ic_user);
	}
	
	@Override
	protected void setActionBarSelection(ActionBar actionBar, int position) {
		actionBar.setSelectedNavigationItem(position);
	}
}
