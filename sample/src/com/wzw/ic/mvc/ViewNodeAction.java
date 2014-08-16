package com.wzw.ic.mvc;

public class ViewNodeAction extends BaseModel {
	private String title;
	private int id;
	
	public ViewNodeAction(int id, String title) {
		this.id = id;
		this.title = title;
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
}
