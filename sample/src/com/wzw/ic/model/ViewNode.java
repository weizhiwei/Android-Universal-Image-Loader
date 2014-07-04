package com.wzw.ic.model;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

public abstract class ViewNode {
	protected String sourceUrl;
	protected List<ViewItem> viewItems;
	
	public ViewNode(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		this.viewItems = new ArrayList<ViewItem>();
	}
	
	public List<ViewItem> getViewItems() {
		return viewItems;
	}
	
	public abstract void reload();
	public abstract void loadOneMorePage();
	public abstract boolean supportPaging();
	protected abstract ViewItem getViewItem(Element pageElem);
}
