package com.wzw.ic.mvc.root;

import android.content.Context;

import java.util.List;

import com.nostra13.example.universalimageloader.IcDatabase;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;


public class HeartsViewNode extends ViewNode {

	protected int pageNo;
	
	public HeartsViewNode() {
		super("hearts");
	}

	@Override
	public boolean supportReloading() {
		return true;
	}

    @Override
    public List<ViewItem> load(Context context, boolean reload, LoadListener loadListener) {
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

        loadListener.onLoadDone(this);

        return pageViewItems;
	}

	@Override
	public boolean supportPaging() {
		return true;
	}
}
