package com.nostra13.example.universalimageloader;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
	protected void initActionBar(ActionBar actionBar) {
	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
	    
	    actionBar.setListNavigationCallbacks(new BaseAdapter () {

			@Override
			public int getCount() {
				return null == parentModel.getViewItems() ? 0 : parentModel.getViewItems().size();
			}

			@Override
			public Object getItem(int position) {
				return parentModel.getViewItems().get(position);
			}

			@Override
			public long getItemId(int position) {
				return parentModel.getViewItems().get(position).hashCode();
			}
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				 TextView textView = new TextView(EntryActivity.this);
				 textView.setText(parentModel.getViewItems().get(position).getLabel());
				 return textView;
		    }

	    }, new OnNavigationListener () {

			@Override
			public boolean onNavigationItemSelected(int itemPosition, long itemId) {
				if (null != pager) {
					pager.setCurrentItem(itemPosition);
					return true;
				}
				return false;
			}
	    	
	    });
	}
	
	@Override
	protected void setActionBarSelection(ActionBar actionBar, int position) {
		actionBar.setSelectedNavigationItem(position);
	}
	
//	@Override
//	protected void setTabTitleIcon(Tab tab, int position, ViewItem viewItem) {
//		final int[] ICONS = new int[] {R.drawable.ic_pictures, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_user, R.drawable.ic_gallery};
//		tab.setIcon(ICONS[position]);
//	}
}
