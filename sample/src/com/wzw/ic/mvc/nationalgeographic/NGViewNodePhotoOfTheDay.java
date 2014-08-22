package com.wzw.ic.mvc.nationalgeographic;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

public class NGViewNodePhotoOfTheDay extends NGViewNode {

	public NGViewNodePhotoOfTheDay() {
		super("http://photography.nationalgeographic.com/photography/photo-of-the-day/archive/?page=%d");
		supportPaging = true;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements searchResultsElems = page.select("#search_results div");
		if (null != searchResultsElems && searchResultsElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < searchResultsElems.size(); ++i) {
				Element elem = searchResultsElems.get(i);
				Elements imgElems = elem.select("img");
				String imgUrl = null;
				if (null != imgElems && imgElems.size() > 0) {
					imgUrl = imgElems.get(0).attr("src");
				}
				if (!TextUtils.isEmpty(imgUrl)) {
					ViewItem viewItem = new ViewItem("", "", "http:" + imgUrl.replace("100x75", "990x742").replace("/overrides/", "/cache/"), ViewItem.VIEW_TYPE_IMAGE_PAGER, this);
					Elements titleElems = elem.select(".photo_info h4");
					if (null != titleElems && titleElems.size() > 0) {
						viewItem.setLabel(titleElems.get(0).ownText());
					}
					Elements descElems = elem.select(".photo_info p");
					if (null != descElems && descElems.size() > 1) {
						viewItem.setStory(descElems.get(1).ownText());
					}
					viewItems.add(viewItem);
				}
			}
		}
		return viewItems;
	}

}
