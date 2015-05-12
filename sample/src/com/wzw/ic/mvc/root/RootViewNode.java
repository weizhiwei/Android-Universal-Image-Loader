package com.wzw.ic.mvc.root;

import java.util.Arrays;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.moko.MokoViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;

public class RootViewNode extends ViewNode {

	private static RootViewNode theRootNode = new RootViewNode();
	
	private ViewItem gallery;
	
	protected RootViewNode() {
		
		super("root", null);
		
		ViewItem moko = new ViewItem("MOKO!", MokoViewNode.MOKO_NAME, null, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeRoot());
		moko.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		moko.setViewItemImageResId(R.drawable.moko);
		
		gallery = new ViewItem("Gallery", "gallery", null, ViewItem.VIEW_TYPE_GRID, new ViewNode("", Arrays.asList(
				moko
                )));
		gallery.setViewItemImageResId(R.drawable.ic_gallery);

        ViewItem stream = new ViewItem("Interestingness", "stream", null, ViewItem.VIEW_TYPE_CARD_LIST, new StreamViewNode2(gallery));
        stream.setViewItemImageResId(R.drawable.ic_pictures);

        ViewItem feeds = new ViewItem("Following", "feeds", null, ViewItem.VIEW_TYPE_CARD_LIST, new FeedsViewNode());
		feeds.setViewItemImageResId(R.drawable.ic_user);

        this.viewItems = Arrays.asList(gallery, stream, feeds);
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
	
	public ViewItem getGalleryViewItem() {
		return gallery;
	}
}