package com.wzw.ic.mvc.nationalgeographic;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wzw.ic.mvc.ViewItem;

public class NGViewNodePhotoOfTheDay extends NGViewNode {

	public NGViewNodePhotoOfTheDay() {
		super("http://photography.nationalgeographic.com/photography/photo-of-the-day/archive/?page=%d");
		supportPaging = true;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements imgElems = page.select("div#search_results img");
		if (null != imgElems && imgElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				ViewItem viewItem = new ViewItem("", "", "http:" + img.attr("src").replace("100x75", "990x742").replace("/overrides/", "/cache/"), 0);
				viewItem.setLabel(img.attr("alt"));
				viewItem.setStory(img.attr("alt"));
				viewItems.add(viewItem);
			}
		}
		return viewItems;
	}

}
