package com.wzw.ic.model.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wzw.ic.model.ViewItem;

public class MokoViewNodePost extends MokoViewNode {

	public MokoViewNodePost(String sourceUrl) {
		super(sourceUrl);
		supportPaging = false;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements imgElems = page.select("p.picbox img");
		if (null != imgElems && imgElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				viewItems.add(new ViewItem("", "", img.attr("src2")));
			}
		}
		return viewItems;
	}
}
