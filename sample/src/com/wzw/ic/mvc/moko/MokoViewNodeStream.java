package com.wzw.ic.mvc.moko;

import com.wzw.ic.mvc.ViewNode;

public class MokoViewNodeStream extends MokoViewNodeChannel {

	public MokoViewNodeStream(ViewNode parent) {
		super(parent, URL_PREFIX + "/postChannel.action?curPage=%d");
	}

    @Override
    public int getViewType(int container) {
        switch (container) {
            case VIEW_TYPE_PAGER:
                return VIEW_TYPE_GRID;
        }
        return super.getViewType(container);
    }
}
