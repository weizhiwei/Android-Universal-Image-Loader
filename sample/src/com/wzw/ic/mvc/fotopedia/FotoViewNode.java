package com.wzw.ic.mvc.fotopedia;

import com.wzw.ic.mvc.ViewNode;

public abstract class FotoViewNode extends ViewNode {

	public FotoViewNode(String sourceUrl) {
		super(sourceUrl);
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
