package com.wzw.ic.mvc;

import java.util.ArrayList;
import java.util.List;

public class ViewNode extends IcObject {
	protected String sourceUrl;
	protected List<ViewItem> viewItems;
	protected List<ViewNodeAction> actions;

	public ViewNode(String sourceUrl) {
		this.sourceUrl = sourceUrl;
		this.viewItems = new ArrayList<ViewItem>();
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
	
	public void reload() {
	}
	
	public boolean supportPaging() {
		return false;
	}
	
	public void loadOneMorePage() {
	}
	
	public Object onAction(ViewNodeAction action) {
		return null;
	}
}
