package com.wzw.ic.mvc.fotopedia;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.flickr.FlickrController;

public class FotoViewNodeRoot extends FotoViewNode {

	public FotoViewNodeRoot(String sourceUrl) {
		super("http://www.fotopedia.com");
		viewItems.add(new ViewItem("Magazine", "magazine", FlickrController.FLICKR_ICON, 0));
	}
}
