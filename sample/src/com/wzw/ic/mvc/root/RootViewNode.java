package com.wzw.ic.mvc.root;

import java.util.List;

import org.jsoup.nodes.Document;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class RootViewNode extends ViewNode {

	public RootViewNode() {
		super("/");
		viewItems.add(new ViewItem("MOKO!美空", "http://www.moko.cc/", "http://www.vitbbs.cn/uploads/allimg/c101125/12ZEK63410-14106.gif", 0));
		viewItems.add(new ViewItem("Flickr", "https://www.flickr.com/", "http://www.masstech.org/sites/mtc/files/images/Flickr-Icon.jpg", 0));
	}

	@Override
	public boolean supportReloading() {
		return false;
	}

	@Override
	public void reload() {
		// throw new MethodNotSupportedException("RootViewNode does not support reloading");
	}

	@Override
	public boolean supportPaging() {
		return false;
	}

	@Override
	public void loadOneMorePage() {
		// throw new MethodNotSupportedException("RootViewNode does not support paging");
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		// throw new MethodNotSupportedException("RootViewNode does not support paging");
		return null;
	}
}
