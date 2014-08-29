package com.wzw.ic.mvc.fotopedia;

import com.wzw.ic.mvc.ViewNode;

public abstract class FotoViewNode extends ViewNode {

	public static String FOTO_ICON = "http://images.cdn.fotopedia.com/fotopedia-af3MHAL54rw-original.png";
	
	public FotoViewNode(String sourceUrl) {
		super(sourceUrl);
	}
}
