package com.wzw.ic.mvc;

import java.util.Date;

import android.text.TextUtils;

public class ViewItem extends IcObject {
	
	public static final int VIEW_TYPE_LIST = 1;
	public static final int VIEW_TYPE_GRID = 2;
	public static final int VIEW_TYPE_IMAGE_PAGER = 3;
	
	public static final int VIEW_ITEM_TYPE_COLOR = 1;
	public static final int VIEW_ITEM_TYPE_IMAGE_RES = 2;
	public static final int VIEW_ITEM_TYPE_IMAGE_URL = 3;
	
	private String label;
	private String nodeUrl;
	private String imageUrl;
	private boolean showingLabelInGrid;
	private int viewItemType;
	private int viewItemColor;
	private int viewItemImageResId;
	private String story;
	private boolean heartsOn;
	private ViewItem author;
	private String origin;
	private String webPageUrl;
	private int initialZoomLevel;
	
	private Date postedDate;
	
	private int viewType;
	private ViewNode viewNode;
	
	public ViewItem(String label, String nodeUrl, String imageUrl, int viewType, ViewNode viewNode) {
		this.setLabel(label);
		this.setNodeUrl(nodeUrl);
		this.setImageUrl(imageUrl);
		this.setViewType(viewType);
		this.setViewItemType(VIEW_ITEM_TYPE_IMAGE_URL);
		this.setShowingLabelInGrid(false);
		this.setHeartsOn(false);
		this.setViewNode(viewNode);
	}
	
	@Override
	public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ViewItem))
            return false;
        ViewItem other = (ViewItem) obj;
        return TextUtils.equals(other.imageUrl, this.imageUrl) && TextUtils.equals(other.nodeUrl, this.nodeUrl);
	}
	
	@Override
    public int hashCode() {
		return (this.imageUrl + this.nodeUrl).hashCode();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getNodeUrl() {
		return nodeUrl;
	}

	public void setNodeUrl(String nodeUrl) {
		this.nodeUrl = nodeUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isShowingLabelInGrid() {
		return showingLabelInGrid;
	}

	public void setShowingLabelInGrid(boolean showingLabelInGrid) {
		this.showingLabelInGrid = showingLabelInGrid;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public boolean isHeartsOn() {
		return heartsOn;
	}

	public void setHeartsOn(boolean heartsOn) {
		this.heartsOn = heartsOn;
	}

	public int getViewType() {
		return viewType;
	}

	public void setViewType(int viewType) {
		this.viewType = viewType;
	}

	public ViewNode getViewNode() {
		return viewNode;
	}

	public void setViewNode(ViewNode viewNode) {
		this.viewNode = viewNode;
	}

	public ViewItem getAuthor() {
		return author;
	}

	public void setAuthor(ViewItem author) {
		this.author = author;
	}

	public String getWebPageUrl() {
		return webPageUrl;
	}

	public void setWebPageUrl(String webPageUrl) {
		this.webPageUrl = webPageUrl;
	}

	public int getViewItemType() {
		return viewItemType;
	}

	public void setViewItemType(int viewItemType) {
		this.viewItemType = viewItemType;
	}

	public int getViewItemColor() {
		return viewItemColor;
	}

	public void setViewItemColor(int viewItemColor) {
		this.viewItemColor = viewItemColor;
	}
	
	public int getViewItemImageResId() {
		return viewItemImageResId;
	}

	public void setViewItemImageResId(int viewItemImageResId) {
		this.viewItemImageResId = viewItemImageResId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public int getInitialZoomLevel() {
		return initialZoomLevel;
	}

	public void setInitialZoomLevel(int initialZoomLevel) {
		this.initialZoomLevel = initialZoomLevel;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}
}
