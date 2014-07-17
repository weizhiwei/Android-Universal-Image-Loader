package com.wzw.ic.mvc.root;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrController;
import com.wzw.ic.mvc.fotopedia.FotoController;

public class RootViewNode extends ViewNode {

	public RootViewNode() {
		super("/");
		viewItems.add(new ViewItem("MOKO!", "moko", "http://www.vitbbs.cn/uploads/allimg/c101125/12ZEK63410-14106.gif", 0));
		viewItems.add(new ViewItem("Flickr", "flickr", FlickrController.FLICKR_ICON, 0));
		viewItems.add(new ViewItem("Fotopedia", "fotopedia", FotoController.FOTO_ICON, 0));
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
}
