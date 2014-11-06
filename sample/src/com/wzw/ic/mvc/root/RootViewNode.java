package com.wzw.ic.mvc.root;

import java.util.Arrays;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodeRoot;
import com.wzw.ic.mvc.hearts.HeartsViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.nationalgeographic.NGViewNode;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeRoot;
import com.wzw.ic.mvc.stream.StreamViewNodeRoot;

public class RootViewNode extends ViewNode {

	private static RootViewNode theRootNode = new RootViewNode();
	
	private ViewItem gallery;
	
	protected RootViewNode() {
		
		super("root", null);
		
		ViewItem moko = new ViewItem("MOKO!", MokoViewNode.MOKO_NAME, null, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeRoot());
		moko.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		moko.setViewItemImageResId(R.drawable.moko);
		
		ViewItem flickr = new ViewItem("Flickr", FlickrViewNode.FLICKR_NAME, null, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeRoot());
		flickr.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		flickr.setViewItemImageResId(R.drawable.flickr);
		
//		ViewItem foto = new ViewItem("Fotopedia", "fotopedia", FotoViewNode.FOTO_ICON, ViewItem.VIEW_TYPE_LIST, new FotoViewNodeRoot());
		
		ViewItem ng = new ViewItem("National Geographic", NGViewNode.NG_NAME, null, ViewItem.VIEW_TYPE_GRID, new NGViewNodeRoot());
		ng.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		ng.setViewItemImageResId(R.drawable.ngraphic);
		
		gallery = new ViewItem("Gallery", "gallery", null, ViewItem.VIEW_TYPE_GRID, new ViewNode("", Arrays.asList(
				moko, flickr, ng)));
		gallery.setViewItemImageResId(R.drawable.ic_gallery);
		
		ViewItem stream = new ViewItem("Pictures", "stream", null, ViewItem.VIEW_TYPE_GRID, new StreamViewNodeRoot());
		stream.setInitialZoomLevel(2);
		stream.setViewItemImageResId(R.drawable.ic_pictures);
		
		ViewItem feeds = new ViewItem("Feeds",  "feeds", null, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot());
		feeds.setViewItemImageResId(R.drawable.ic_user);
		
		ViewItem hearts = new ViewItem("Hearts", "hearts", null, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot());
		hearts.setViewItemImageResId(R.drawable.ic_user);
		
		ViewItem stars = new ViewItem("Stars",  "stars", null, ViewItem.VIEW_TYPE_GRID, new HeartsViewNodeRoot());
		stars.setViewItemImageResId(R.drawable.ic_user);
		
		this.viewItems = Arrays.asList(stream, feeds, hearts, stars, gallery);
	}
	
	public static RootViewNode getInstance() {
		return theRootNode;
	}
	
	public ViewItem findGalleryViewItem(String name) {
		for (ViewItem viewItem: gallery.getViewNode().getViewItems()) {
			if (viewItem.getNodeUrl().equals(name)) {
				return viewItem;
			}
		}
		return null;
	}
}
