package com.wzw.ic.mvc.fotopedia;

import com.wzw.ic.mvc.ViewItem;

public class FotoViewNodeRoot extends FotoViewNode {

	public FotoViewNodeRoot() {
		super("http://www.fotopedia.com");
		viewItems.add(new ViewItem("Magazine", "magazine", FOTO_ICON, ViewItem.VIEW_TYPE_LIST, new FotoViewNodeMagazine()));
	}
}
