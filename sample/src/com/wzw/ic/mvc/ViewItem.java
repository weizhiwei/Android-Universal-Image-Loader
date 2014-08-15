package com.wzw.ic.mvc;

public class ViewItem extends BaseModel {
	private String label;
	private String nodeUrl;
	private String imageUrl;
	private int color;
	private boolean showingLabelInGrid;
	private boolean usingColorOverImage;
	private String story;
	private boolean heartsOn;
	
	public ViewItem(String label, String nodeUrl, String imageUrl, int color) {
		this.setLabel(label);
		this.setNodeUrl(nodeUrl);
		this.setImageUrl(imageUrl);
		this.setColor(color);
		this.setShowingLabelInGrid(false);
		this.setUsingColorOverImage(false);
		this.setHeartsOn(false);
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
}
