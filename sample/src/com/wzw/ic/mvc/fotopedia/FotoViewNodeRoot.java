package com.wzw.ic.mvc.fotopedia;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.flickr.FlickrController;

public class FotoViewNodeRoot extends FotoViewNode {

	public FotoViewNodeRoot() {
		super("http://www.fotopedia.com");
		viewItems.add(new ViewItem("Magazine", "magazine", FotoController.FOTO_ICON, 0));
	}
}
