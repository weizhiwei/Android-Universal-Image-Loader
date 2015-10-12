package com.wzw.ic.mvc.root;

import java.util.Arrays;
import java.util.WeakHashMap;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeBookmarks;
import com.wzw.ic.mvc.moko.MokoViewNodeFollowing;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoViewNodeStream;

public class RootViewNode extends ViewNode {

	private static RootViewNode theRootNode = new RootViewNode();

    private RootViewNode() {
		
		super(null);

        ViewNode channels = new MokoViewNodeRoot(this);
        channels.setTitle("Channels");
        channels.setViewItemImageResId(android.R.drawable.ic_dialog_dialer);
        channels.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

        ViewNode bookmarks = new MokoViewNodeBookmarks(this);
        bookmarks.setTitle("Bookmarks");
        bookmarks.setViewItemImageResId(android.R.drawable.btn_star_big_on);
        bookmarks.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

        ViewNode following = new MokoViewNodeFollowing(this);
        following.setTitle("Following");
        following.setViewItemImageResId(android.R.drawable.ic_menu_crop);
        following.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

        ViewNode update = new FeedsViewNode(this);
        update.setTitle("Update");

        ViewNode new_ = new MokoViewNodeStream(this);
        new_.setTitle("New");
        new_.setViewItemImageResId(android.R.drawable.ic_menu_gallery);
        new_.setViewItemType(ViewNode.VIEW_ITEM_TYPE_IMAGE_RES);

        this.children.addAll(Arrays.asList(channels, new_, bookmarks, following, update));
	}
	
	public static RootViewNode getInstance() {
		return theRootNode;
	}
}