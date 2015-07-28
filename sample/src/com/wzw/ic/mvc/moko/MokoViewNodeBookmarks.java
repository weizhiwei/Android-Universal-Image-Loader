package com.wzw.ic.mvc.moko;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MokoViewNodeBookmarks extends MokoViewNode {

	public MokoViewNodeBookmarks() {
		super(URL_PREFIX + "/postCollection|postCollection.action?curPage=%d");
        supportPaging = true;
	}

    @Override
    protected List<ViewItem> extractViewItemsFromPage(Document page) {
        List<ViewItem> viewItems = null;
        Elements imgElems = page.select(".favorite img");
        Elements aElems = page.select(".favorite a.imgBorder");
        if (null != imgElems && imgElems.size() > 0 &&
                null != aElems && aElems.size() > 0 &&
                imgElems.size() == aElems.size()) {
            viewItems = new ArrayList<ViewItem>();
            for (int i = 0; i < imgElems.size(); ++i) {
                Element img = imgElems.get(i);
                Element a = aElems.get(i);
                String title = img.attr("alt");
                ViewItem viewItem = new ViewItem(
                        title,
                        URL_PREFIX + a.attr("href"),
                        img.attr("src2"),
                        ViewItem.VIEW_TYPE_GRID,
                        new MokoViewNodePost(URL_PREFIX + a.attr("href"), title));
                viewItem.setInitialZoomLevel(1);
                viewItems.add(viewItem);
            }
        }
        return viewItems;
    }

}
