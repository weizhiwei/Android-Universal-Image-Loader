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
		
		super("root", Arrays.asList(
                new ViewItem("Channels", null, null, VIEW_TYPE_GRID, new MokoViewNodeRoot()),
                new ViewItem("Bookmarks", null, null, VIEW_TYPE_LIST_TILES, new MokoViewNodeBookmarks()),
                new ViewItem("Following", null, null, VIEW_TYPE_LIST_SIMPLE, new MokoViewNodeFollowing()),
                new ViewItem("Update", null, null, VIEW_TYPE_LIST_TILES, new FeedsViewNode()),
                new ViewItem("New", null, null, VIEW_TYPE_LIST_TILES, new MokoViewNodeStream())
                ));
	}
	
	public static RootViewNode getInstance() {
		return theRootNode;
	}
}