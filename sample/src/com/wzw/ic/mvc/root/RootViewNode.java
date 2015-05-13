package com.wzw.ic.mvc.root;

import java.util.Arrays;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.moko.MokoViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;

public class RootViewNode extends ViewNode {

	private static RootViewNode theRootNode = new RootViewNode();
	
	protected RootViewNode() {
		
		super("root", null);
		
		ViewItem moko = new ViewItem("Channels", MokoViewNode.MOKO_NAME, null, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeRoot());
		moko.setViewItemType(ViewItem.VIEW_ITEM_TYPE_IMAGE_RES);
		moko.setViewItemImageResId(R.drawable.moko);
		
		ViewItem stream = new ViewItem("New", "stream", null, ViewItem.VIEW_TYPE_CARD_LIST, new StreamViewNode2(
                new ViewItem("Gallery", "gallery", null, ViewItem.VIEW_TYPE_GRID, new ViewNode("", Arrays.asList(moko)))
        ));
        stream.setViewItemImageResId(R.drawable.ic_pictures);

        ViewItem feeds = new ViewItem("Following", "feeds", null, ViewItem.VIEW_TYPE_CARD_LIST, new FeedsViewNode());
		feeds.setViewItemImageResId(R.drawable.ic_user);

        this.viewItems = Arrays.asList(feeds, moko, stream);
	}
	
	public static RootViewNode getInstance() {
		return theRootNode;
	}
}