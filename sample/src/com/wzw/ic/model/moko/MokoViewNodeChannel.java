package com.wzw.ic.model.moko;

import org.jsoup.nodes.Element;

import com.wzw.ic.model.ViewItem;

public class MokoViewNodeChannel extends MokoViewNode {

	public MokoViewNodeChannel(String sourceUrl) {
		super(sourceUrl);
		selector = "div.cover img[src2]";
		supportPaging = true;
	}

	@Override
	protected ViewItem getViewItem(Element pageElem) {
		return null;
	}
}
