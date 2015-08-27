package com.wzw.ic.mvc.root;

import java.util.Arrays;

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

        ViewNode bookmarks = new MokoViewNodeBookmarks(this);
        bookmarks.setTitle("Bookmarks");

        ViewNode following = new MokoViewNodeFollowing(this);
        following.setTitle("Following");


//        ViewNode update = new FeedsViewNode(this);
//        update.setTitle("Update");
//        update.setViewType(VIEW_TYPE_LIST_TILES);

        ViewNode new_ = new MokoViewNodeStream(this);
        new_.setTitle("New");

        this.children.addAll(Arrays.asList(channels, new_, bookmarks, following));
	}
	
	public static RootViewNode getInstance() {
		return theRootNode;
	}
}