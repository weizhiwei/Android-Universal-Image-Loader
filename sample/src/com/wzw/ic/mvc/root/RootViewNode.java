package com.wzw.ic.mvc.root;

import java.util.Arrays;

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

	public static final ViewItem ROOT_VIEW_ITEM = new ViewItem("iC", "ic", null, ViewItem.VIEW_TYPE_GRID, new RootViewNode());
	
	public RootViewNode() {
		super("root", Arrays.asList(
			new ViewItem("Seeing", "seeing", HeartsViewNode.HEARTS_ICON, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot()),	
			new ViewItem("Hearts", "hearts", HeartsViewNode.HEARTS_ICON, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot()),
			new ViewItem("Gallery", "gallery", HeartsViewNode.HEARTS_ICON, ViewItem.VIEW_TYPE_GRID, new ViewNode("", Arrays.asList(
					new ViewItem("MOKO!", "moko", MokoViewNode.MOKO_ICON, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeRoot()),
					new ViewItem("Flickr", "flickr", FlickrViewNode.FLICKR_ICON, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeRoot()),
//					new ViewItem("Fotopedia", "fotopedia", FotoViewNode.FOTO_ICON, ViewItem.VIEW_TYPE_LIST, new FotoViewNodeRoot()),
					new ViewItem("National Geographic", "nationalgeographic", NGViewNode.NG_ICON, ViewItem.VIEW_TYPE_GRID, new NGViewNodeRoot()))))
		));
	}
}
