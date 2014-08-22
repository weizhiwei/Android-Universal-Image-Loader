package com.wzw.ic.mvc.root;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class MetaRootViewNode extends ViewNode {

	public MetaRootViewNode() {
		super("metaRoot");
		viewItems.add(new ViewItem("iC", "ic", null, ViewItem.VIEW_TYPE_LIST, new RootViewNode()));
	}

	@Override
	public boolean supportReloading() {
		return false;
	}

	@Override
	public void reload() {
		// throw new MethodNotSupportedException("RootViewNode does not support reloading");
	}

	@Override
	public boolean supportPaging() {
		return false;
	}

	@Override
	public void loadOneMorePage() {
		// throw new MethodNotSupportedException("RootViewNode does not support paging");
	}
}
