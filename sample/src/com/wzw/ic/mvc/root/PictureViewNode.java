package com.wzw.ic.mvc.root;

import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeBookmarks;
import com.wzw.ic.mvc.moko.MokoViewNodeFollowing;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoViewNodeStream;

import java.util.Arrays;

public class PictureViewNode extends ViewNode {

	public PictureViewNode(ViewNode parent, String imageUrl) {
		super(parent);
        this.imageUrl = imageUrl;
	}

    @Override
    public int getViewType(int container) {
        return VIEW_TYPE_IMAGE;
    }

    @Override
    public boolean supportReloading() {
        return true;
    }
}