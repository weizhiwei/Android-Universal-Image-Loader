package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewNode extends IcObject {
	protected String sourceUrl;
	protected List<ViewItem> viewItems;
	protected List<ViewNodeAction> actions;
	
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
	
	public List<ViewNodeAction> getActions() {
		return actions;
	}
	
	public abstract boolean supportReloading();
	public abstract void reload();
	public abstract boolean supportPaging();
	public abstract void loadOneMorePage();
	public Object onAction(ViewNodeAction action) {
		return null;
	}
}
