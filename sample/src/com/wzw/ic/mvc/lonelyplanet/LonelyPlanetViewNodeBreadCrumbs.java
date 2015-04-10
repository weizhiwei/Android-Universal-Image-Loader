package com.wzw.ic.mvc.lonelyplanet;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LonelyPlanetViewNodeBreadCrumbs extends LonelyPlanetViewNode {

	public LonelyPlanetViewNodeBreadCrumbs(String sourceUrl) {
		super(sourceUrl);
        supportPaging = true;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
        Elements navElems = page.select("[class*=nav__item--breadcrumbs]");
		if (null != navElems && navElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
            for (Element elem: navElems) {
                String name = elem.text();
                if (name.equals("Home") ||
                    name.startsWith("Sights in")) {
                    continue;
                }
                ViewItem viewItem = new ViewItem(name, TextUtils.isEmpty(elem.attr("href")) ? null: URL_PREFIX + elem.attr("href"), "", ViewItem.VIEW_TYPE_PLACE_LIST, null);
                viewItems.add(viewItem);
			}
		}
		return viewItems;
	}
}
