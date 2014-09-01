package com.wzw.ic.mvc;

import android.text.TextUtils;

public class ViewItem extends IcObject {
	
	public static final int VIEW_TYPE_LIST = 1;
	public static final int VIEW_TYPE_GRID = 2;
	public static final int VIEW_TYPE_IMAGE_PAGER = 3;
	
	private String label;
	private String nodeUrl;
	private String imageUrl;
	private int viewType;
	private int color;
	private boolean showingLabelInGrid;
	private boolean usingColorOverImage;
	private String story;
	private boolean heartsOn;
	private ViewNode viewNode;
	
	public ViewItem(String label, String nodeUrl, String imageUrl, int viewType, ViewNode viewNode) {
		this.setLabel(label);
		this.setNodeUrl(nodeUrl);
		this.setImageUrl(imageUrl);
		this.setViewType(viewType);
		this.setColor(color);
		this.setShowingLabelInGrid(false);
		this.setUsingColorOverImage(false);
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

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ViewItem)) {
			return false;
		}
		ViewItem vi = (ViewItem) o;
		return TextUtils.equals(vi.imageUrl, this.imageUrl) &&
				TextUtils.equals(vi.nodeUrl, this.nodeUrl);
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

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
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

	public boolean isUsingColorOverImage() {
		return usingColorOverImage;
	}

	public void setUsingColorOverImage(boolean usingColorOverImage) {
		this.usingColorOverImage = usingColorOverImage;
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
}
