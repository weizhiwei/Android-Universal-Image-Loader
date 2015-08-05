package com.wzw.ic.mvc.moko;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MokoViewNodeFollowing extends MokoViewNode {

	public MokoViewNodeFollowing() {
		super(URL_PREFIX + "/subscribe/1c36d7bdb3374b2d8a6471d7200e5932/%d.html");
        supportPaging = true;
	}

    @Override
    protected List<ViewItem> extractViewItemsFromPage(Document page) {
        List<ViewItem> viewItems = null;
        Elements imgElems = page.select(".info img.icon");
        Elements aElems = page.select(".info a.mainWhite");
        if (null != imgElems && imgElems.size() > 0 &&
                null != aElems && aElems.size() > 0 &&
                imgElems.size() == aElems.size()) {
            viewItems = new ArrayList<ViewItem>();
            for (int i = 0; i < imgElems.size(); ++i) {
                Element img = imgElems.get(i);
                Element a = aElems.get(i);
                String title = a.ownText();
                String url = URL_PREFIX + a.attr("href");
                ViewItem viewItem = new ViewItem(
                        title,
                        url,
                        img.attr("src"),
                        ViewItem.VIEW_TYPE_LIST_SIMPLE,
                        new MokoViewNodeAuthor(url));
                viewItem.setInitialZoomLevel(1);
                viewItems.add(viewItem);
            }
        }
        return viewItems;
    }

}
