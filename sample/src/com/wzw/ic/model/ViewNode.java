package com.wzw.ic.model;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;

import android.app.Activity;

public abstract class ViewNode extends BaseModel {
	protected String sourceUrl;
	protected List<ViewItem> viewItems;
	
	public ViewNode(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		this.viewItems = new ArrayList<ViewItem>();
	}
	
	public String getSourceUrl() {
		return sourceUrl;
	}
	
	public List<ViewItem> getViewItems() {
		return viewItems;
	}
	
	public abstract void reload();
	public abstract void loadOneMorePage();
	public abstract boolean supportPaging();
	protected abstract List<ViewItem> extractViewItemsFromPage(Document page);
}
