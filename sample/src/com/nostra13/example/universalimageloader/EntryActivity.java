package com.nostra13.example.universalimageloader;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class EntryActivity extends BaseActivity {
	
	ViewPager pager;
	
	@Override
	public void onBackPressed() {
		imageLoader.stop();
//		IcDatabase.getInstance().close();
		super.onBackPressed();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.ic_entry);
		
		if (Build.VERSION.SDK_INT >= 11) {
			ActionBar actionBar = getActionBar();
			initActionBar(actionBar);
		}
				
		pager = (ViewPager) findViewById(R.id.ic_viewpager);
		final PagerAdapter pagerAdapter = new EntryPagerAdapter();
		pager.setAdapter(pagerAdapter);
	}
	
//	@Override
//	protected void setModelFromIntent() {
//		parentModel = RootViewNode.getInstance();
//		myViewItem = parentModel.getViewItems().get(0);
//		model = myViewItem.getViewNode();
//		updateTitleIconFromViewItem(myViewItem);
//	}
	
	protected void initActionBar(final ActionBar actionBar) {
//	    // Specify that tabs should be displayed in the action bar.
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
	    
	    actionBar.setDisplayShowHomeEnabled(false);

	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		final Tab tabExplorer = actionBar.newTab();
		tabExplorer.setTabListener(tabListener);
		tabExplorer.setIcon(R.drawable.ic_pictures);
        actionBar.addTab(tabExplorer);

        final Tab tabMine = actionBar.newTab();
        tabMine.setTabListener(tabListener);
        tabMine.setIcon(R.drawable.ic_user);
        actionBar.addTab(tabMine);

        setHasEmbeddedTabs(actionBar, true);
	}
	
	private class EntryPagerAdapter extends PagerAdapter {
		
		EntryPagerAdapter() {
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
	    public void setPrimaryItem(ViewGroup container, int position, Object object) {
	        super.setPrimaryItem(container, position, object);
	        
	        if (Build.VERSION.SDK_INT >= 11) {
				ActionBar actionBar = getActionBar();
				actionBar.selectTab(actionBar.getTabAt(position));
	        }
	    }
		
		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			View contentView = null;
			
			switch (position) {
			case 0:
				contentView = getLayoutInflater().inflate(R.layout.ic_explorer, view, false);
				break;
			case 1:
				contentView = getLayoutInflater().inflate(R.layout.ic_explorer, view, false);
				break;
			default:
				break;
			}
			
			assert contentView != null;
			if (null == contentView) {
				return null;
			}
			
			contentView.setTag(position);
						
			view.addView(contentView, 0);
			
			return contentView;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}
	}
}
