package com.wzw.ic.mvc.root;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodeRoot;
import com.wzw.ic.mvc.hearts.HeartsViewNode;
import com.wzw.ic.mvc.hearts.HeartsViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.nationalgeographic.NGViewNode;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeRoot;

public class RootViewNode extends ViewNode {

	public RootViewNode() {
		super("root");
		viewItems.add(new ViewItem("Hearts", "hearts", HeartsViewNode.HEARTS_ICON, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot()));
		viewItems.add(new ViewItem("MOKO!", "moko", MokoViewNode.MOKO_ICON, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeRoot()));
		viewItems.add(new ViewItem("Flickr", "flickr", FlickrViewNode.FLICKR_ICON, ViewItem.VIEW_TYPE_LIST, new FlickrViewNodeRoot()));
//		viewItems.add(new ViewItem("Fotopedia", "fotopedia", FotoViewNode.FOTO_ICON, ViewItem.VIEW_TYPE_LIST, new FotoViewNodeRoot()));
		viewItems.add(new ViewItem("National Geographic", "nationalgeographic", NGViewNode.NG_ICON, ViewItem.VIEW_TYPE_LIST, new NGViewNodeRoot()));
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
