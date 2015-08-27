package com.wzw.ic.mvc.moko;

import com.wzw.ic.mvc.ViewNode;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MokoViewNodeFollowing extends MokoViewNode {

	public MokoViewNodeFollowing(ViewNode parent) {
		super(parent, URL_PREFIX + "/subscribe/1c36d7bdb3374b2d8a6471d7200e5932/%d.html");
        supportPaging = true;
	}

    @Override
    protected List<ViewNode> extractViewItemsFromPage(Document page) {
        List<ViewNode> viewItems = null;
        Elements imgElems = page.select(".info img.icon");
        Elements aElems = page.select(".info a.mainWhite");
        if (null != imgElems && imgElems.size() > 0 &&
                null != aElems && aElems.size() > 0 &&
                imgElems.size() == aElems.size()) {
            viewItems = new ArrayList<ViewNode>();
            for (int i = 0; i < imgElems.size(); ++i) {
                Element img = imgElems.get(i);
                Element a = aElems.get(i);
                String title = a.ownText();
                String userId = a.attr("href").substring(1);
                ViewNode viewItem = new MokoViewNodeAuthor(this, userId);
                viewItem.setTitle(title);
                viewItem.setImageUrl(img.attr("src"));
                viewItem.setInitialZoomLevel(2);
                viewItems.add(viewItem);
            }
        }
        return viewItems;
    }

    @Override
    public int getViewType(int container) {
        switch (container) {
            case VIEW_TYPE_PAGER:
                return VIEW_TYPE_LIST;
        }
        return super.getViewType(container);
    }

}
