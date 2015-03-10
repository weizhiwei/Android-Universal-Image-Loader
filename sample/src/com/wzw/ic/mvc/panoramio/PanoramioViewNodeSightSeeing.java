package com.wzw.ic.mvc.panoramio;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodePhotoOfTheDayPost;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PanoramioViewNodeSightSeeing extends PanoramioViewNode {

	public PanoramioViewNodeSightSeeing() {
		super(URL_PREFIX + "/sightseeing");
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements continentElems = page.select("div.continent");
		if (null != continentElems && continentElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
            headers.clear();
            for (Element elem: continentElems) {
                Elements titleElems = elem.select("h3");
				String continentName = null;
				if (null != titleElems && titleElems.size() > 0) {
                    continentName = titleElems.get(0).text();
				}

                if (continentName.equals("Antarctica")) {
                    continue;
                }

                Elements placeElems = elem.select("li a");
                for (Element placeElem: placeElems) {
                    ViewItem viewItem = new ViewItem(placeElem.text(), URL_PREFIX + placeElem.attr("href"), "", ViewItem.VIEW_TYPE_PLACE_LIST, null);
                    viewItems.add(viewItem);
                }
                headers.add(placeElems.size());
			}
		}
		return viewItems;
	}

    @Override
    public List<ViewItem> loadOneMorePage() {
        return null;
    }
}
