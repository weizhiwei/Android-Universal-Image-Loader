package com.wzw.ic.mvc.lonelyplanet;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LonelyPlanetViewNodeSights extends LonelyPlanetViewNode {

	public LonelyPlanetViewNodeSights(String sourceUrl) {
		super(sourceUrl);
        supportPaging = true;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements articleElems = page.select("article");
		if (null != articleElems && articleElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
            for (Element elem: articleElems) {
                Elements titleElems = elem.select("h1");
                if (null != titleElems && titleElems.size() > 0) {
                    ViewItem viewItem = new ViewItem(titleElems.get(0).text(), "", "", ViewItem.VIEW_TYPE_PLACE_LIST, null);
                    viewItems.add(viewItem);

                    Elements aElems = elem.select("a");
                    if (null != aElems && aElems.size() > 0) {
                        viewItem.setNodeUrl(URL_PREFIX + aElems.get(0).attr("href"));
                    }
                }
			}
		}
		return viewItems;
	}
}
