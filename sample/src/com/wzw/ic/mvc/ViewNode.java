package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.MethodNotSupportedException;
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
	
	public abstract boolean supportReloading();
	public abstract void reload();
	public abstract boolean supportPaging();
	public abstract void loadOneMorePage();
	protected abstract List<ViewItem> extractViewItemsFromPage(Document page);
}
