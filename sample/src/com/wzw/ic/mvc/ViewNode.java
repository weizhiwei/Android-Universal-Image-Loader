package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

public class ViewNode extends IcObject {
	protected String sourceUrl;
	
	protected List<ViewItem> viewItems, viewItemsCopy;
	protected List<ViewNodeAction> actions, actionsCopy;
	protected List<Integer> headers, headersCopy;
    protected List<ViewItem> headerItems, headerItemsCopy;

    protected boolean isDetached = false;
	
	public ViewNode(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		this.viewItems = new ArrayList<ViewItem>();
		this.headers = new ArrayList<Integer>();
		this.actions = new ArrayList<ViewNodeAction>();
        this.headerItems = new ArrayList<ViewItem>();
	}

	public ViewNode(String sourceUrl, List<ViewItem> viewItems) {
		this.sourceUrl = sourceUrl;
		this.viewItems = viewItems;
		this.headers = new ArrayList<Integer>();
		this.actions = new ArrayList<ViewNodeAction>();
        this.headerItems = new ArrayList<ViewItem>();
	}
	
	public void detach() {
		viewItemsCopy = new ArrayList<ViewItem>(viewItems.size());
		viewItemsCopy.addAll(viewItems);
		headersCopy = new ArrayList<Integer>(headers.size());
		headersCopy.addAll(headers);
		actionsCopy = new ArrayList<ViewNodeAction>(actions.size());
		actionsCopy.addAll(actions);
        headerItemsCopy = new ArrayList<ViewItem>(headerItems.size());
        headerItemsCopy.addAll(headerItems);
        isDetached = true;
	}

    public void clearDetachment() {
        viewItemsCopy.clear();
        headersCopy.clear();
        actionsCopy.clear();
        headerItemsCopy.clear();
    }

	public void attach() {
		isDetached = false;
		viewItemsCopy = null;
		headersCopy = null;
		actionsCopy = null;
        headerItemsCopy = null;
	}
	
	public String getSourceUrl() {
		return sourceUrl;
	}
	
	public List<ViewItem> getViewItems() {
		return isDetached ? viewItemsCopy : viewItems;
	}
	
	public List<Integer> getHeaders() {
		return isDetached ? headersCopy : headers;
	}
	
	public List<ViewNodeAction> getActions() {
		return isDetached ? actionsCopy : actions;
	}

    public List<ViewItem> getHeaderItems() {
        return isDetached ? headerItemsCopy : headerItems;
    }

    public boolean supportReloading() {
		return false;
	}
	
	public List<ViewItem> reload() {
		return null;
	}
	
	public boolean supportPaging() {
		return false;
	}
	
	public List<ViewItem> loadOneMorePage() {
		return null;
	}
	
	public Object onAction(ViewNodeAction action) {
		return null;
	}
		
	public int getHeaderViewResId(int header, int itemViewType /* card type */) {
		return 0;
	}
	
	public HeaderViewHolder createHolderFromHeaderView(View headerView) {
		return null;
	}
	
	public void updateHeaderView(View headerView, HeaderViewHolder holder, int position) {
	}
	
	public interface ViewItemActivityStarter {
		public void startViewItemActivity(ViewNode parent, ViewItem viewItem);
	}
	
	public void onViewItemClicked(ViewItem viewItem, ViewItemActivityStarter starter) {
		starter.startViewItemActivity(this, viewItem);
	}

    protected int perturbPageNo(int pageNo, boolean reload) {
        return pageNo;
    }
}
