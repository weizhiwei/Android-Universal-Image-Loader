package com.wzw.ic.mvc.nationalgeographic;

import java.util.List;

import org.jsoup.nodes.Document;

import com.wzw.ic.mvc.ViewItem;

public class NGViewNodeRoot extends NGViewNode {
	public NGViewNodeRoot() {
		super("http://www.nationalgeographic.com");
		supportPaging = false;
		viewItems.add(new ViewItem("Photo of the Day", "photoOfTheDay", NG_ICON, ViewItem.VIEW_TYPE_GRID, new NGViewNodePhotoOfTheDay()));
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean supportReloading() {
		return false;
	}
}
