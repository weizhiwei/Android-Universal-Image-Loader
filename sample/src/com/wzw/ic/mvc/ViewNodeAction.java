package com.wzw.ic.mvc;

public class ViewNodeAction extends IcObject {
	private String title;
	private int id;
	private boolean visible;
	
	public ViewNodeAction(int id, String title) {
		this.id = id;
		this.title = title;
		this.visible = false;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
