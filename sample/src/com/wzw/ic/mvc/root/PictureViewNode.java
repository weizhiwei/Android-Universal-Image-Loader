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
        switch (container) {
            case VIEW_TYPE_PAGER:
                return VIEW_TYPE_IMAGE;
//            case VIEW_TYPE_LIST:
//                return VIEW_TYPE_TILE;
        }
        return super.getViewType(container);
    }

    @Override
    public boolean supportReloading() {
        return true;
    }
}