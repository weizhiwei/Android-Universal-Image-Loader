package com.wzw.ic.mvc.root;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrController;
import com.wzw.ic.mvc.fotopedia.FotoController;
import com.wzw.ic.mvc.hearts.HeartsController;
import com.wzw.ic.mvc.moko.MokoController;
import com.wzw.ic.mvc.nationalgeographic.NGController;

public class RootViewNode extends ViewNode {

	public RootViewNode() {
		super("/");
		viewItems.add(new ViewItem("Hearts", "hearts", HeartsController.HEARTS_ICON, 0));
		viewItems.add(new ViewItem("MOKO!", "moko", MokoController.MOKO_ICON, 0));
		viewItems.add(new ViewItem("Flickr", "flickr", FlickrController.FLICKR_ICON, 0));
		viewItems.add(new ViewItem("Fotopedia", "fotopedia", FotoController.FOTO_ICON, 0));
		viewItems.add(new ViewItem("National Geographic", "nationalgeographic", NGController.NG_ICON, 0));
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
