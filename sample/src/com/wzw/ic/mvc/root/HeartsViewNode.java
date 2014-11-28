package com.wzw.ic.mvc.root;

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
	public List<ViewItem> reload()  {
		return doLoad(true);
	}
	
	private List<ViewItem> doLoad(boolean reload) {
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
		return pageViewItems;
	}
	
	@Override
	public List<ViewItem> loadOneMorePage() {
		return doLoad(false);
	}

	@Override
	public boolean supportPaging() {
		return true;
	}
}
