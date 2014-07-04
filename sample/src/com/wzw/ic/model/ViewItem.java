package com.wzw.ic.model;

public class ViewItem extends BaseModel {
	private String label;
	private String nodeUrl;
	private String imageUrl;
	
	public ViewItem(String label, String nodeUrl, String imageUrl) {
		this.setLabel(label);
		this.setNodeUrl(nodeUrl);
		this.setImageUrl(imageUrl);
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
	
	
}
