package com.wzw.ic.mvc.root;

import java.util.Arrays;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeBookmarks;
import com.wzw.ic.mvc.moko.MokoViewNodeFollowing;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoViewNodeStream;

public class RootViewNode extends ViewNode {

	private static RootViewNode theRootNode = new RootViewNode();
	
	protected RootViewNode() {
		
		super(null);

        ViewNode channels = new MokoViewNodeRoot(this);
        channels.setTitle("Channels");
        channels.setViewItemImageResId(R.drawable.tiles_on);
        channels.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

        ViewNode bookmarks = new MokoViewNodeBookmarks(this);
        bookmarks.setTitle("Bookmarks");
        bookmarks.setViewItemImageResId(R.drawable.heart_on);
        bookmarks.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

        ViewNode following = new MokoViewNodeFollowing(this);
        following.setTitle("Following");
        following.setViewItemImageResId(R.drawable.smiley_on);
        following.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

//        ViewNode update = new FeedsViewNode(this);
//        update.setTitle("Update");
//        update.setViewType(VIEW_TYPE_LIST_TILES);

        ViewNode new_ = new MokoViewNodeStream(this);
        new_.setTitle("New");
        new_.setViewItemImageResId(R.drawable.crescent_on);
        new_.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

        this.children.addAll(Arrays.asList(channels, new_, bookmarks, following));
	}
	
	public static RootViewNode getInstance() {
		return theRootNode;
	}
}