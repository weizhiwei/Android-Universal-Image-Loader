package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

public class ViewNode extends IcObject {
	protected String sourceUrl;
	protected List<ViewItem> viewItems;
	protected List<ViewNodeAction> actions;
	protected List<Integer> headers;
	
	public ViewNode(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		this.viewItems = new ArrayList<ViewItem>();
		this.headers = new ArrayList<Integer>();
		this.actions = new ArrayList<ViewNodeAction>();
	}

	public ViewNode(String sourceUrl, List<ViewItem> viewItems) {
		this.sourceUrl = sourceUrl;
		this.viewItems = viewItems;
		this.actions = new ArrayList<ViewNodeAction>();
	}
	
	public String getSourceUrl() {
		return sourceUrl;
	}
	
	public List<ViewItem> getViewItems() {
		return viewItems;
	}
	
	public List<ViewNodeAction> getActions() {
		return actions;
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
	
	public List<Integer> getHeaders() {
		return headers;
	}
	
	public int getHeaderViewResId(int header) {
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
	
	public void onHeaderClicked(int header, ViewItemActivityStarter starter) {
		
	}
	
	public void onFooterClicked(int footer, ViewItemActivityStarter starter) {
		
	}
}
