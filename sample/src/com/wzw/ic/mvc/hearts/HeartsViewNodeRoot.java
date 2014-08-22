package com.wzw.ic.mvc.hearts;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.nostra13.example.universalimageloader.IcDatabase;
import com.wzw.ic.mvc.ViewItem;


public class HeartsViewNodeRoot extends HeartsViewNode {

	protected int pageNo;
	
	public HeartsViewNodeRoot() {
		super("hearts");
	}

	@Override
	public boolean supportReloading() {
		return true;
	}
	
	@Override
	public void reload()  {
		doLoad(true);
	}
	
	private void doLoad(boolean reload) {
		int newPageNo = reload ? 0 : pageNo + 1;
		final int PER_PAGE = 30;
		
		List<ViewItem> pageViewItems = IcDatabase.getInstance().fetchAllViewItemsInHearts(newPageNo*PER_PAGE, PER_PAGE);
		if (null != pageViewItems && pageViewItems.size() > 0) {
			for (ViewItem item: pageViewItems) {
				item.setViewType(ViewItem.VIEW_TYPE_IMAGE_PAGER);
				item.setViewNode(this);
			}
			
			pageNo = newPageNo;
			if (reload) {
				viewItems.clear();
			}
			viewItems.addAll(pageViewItems);
		}
	}
	
	@Override
	public void loadOneMorePage() {
		doLoad(false);
	}

	@Override
	public boolean supportPaging() {
		return true;
	}
}
