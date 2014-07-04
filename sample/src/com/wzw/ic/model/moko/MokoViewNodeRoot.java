package com.wzw.ic.model.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wzw.ic.model.ViewItem;

public class MokoViewNodeRoot extends MokoViewNode {

	public MokoViewNodeRoot(String sourceUrl) {
		super(sourceUrl);
		supportPaging = false;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements aElems = page.select("a.mark");
		if (null != aElems && aElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < aElems.size(); ++i) {
				Element a = aElems.get(i);
				viewItems.add(new ViewItem(a.ownText(), URL_PREFIX + a.attr("href"), ""));
			}
		}
		return viewItems;
	}
}
