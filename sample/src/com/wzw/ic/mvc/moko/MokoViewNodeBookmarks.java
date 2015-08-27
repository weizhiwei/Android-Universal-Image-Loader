package com.wzw.ic.mvc.moko;

import com.wzw.ic.mvc.ViewNode;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MokoViewNodeBookmarks extends MokoViewNode {

	public MokoViewNodeBookmarks(ViewNode parent) {
		super(parent, URL_PREFIX + "/postCollection|postCollection.action?curPage=%d");
        supportPaging = true;
	}

    @Override
    protected List<ViewNode> extractViewItemsFromPage(Document page) {
        List<ViewNode> viewItems = null;
        Elements imgElems = page.select(".favorite img");
        Elements aElems = page.select(".favorite a.imgBorder");
        if (null != imgElems && imgElems.size() > 0 &&
                null != aElems && aElems.size() > 0 &&
                imgElems.size() == aElems.size()) {
            viewItems = new ArrayList<ViewNode>();
            for (int i = 0; i < imgElems.size(); ++i) {
                Element img = imgElems.get(i);
                Element a = aElems.get(i);
                ViewNode viewNode = new MokoViewNodePost(this, URL_PREFIX + a.attr("href"));
                viewNode.setImageUrl(img.attr("src2"));
                viewNode.setInitialZoomLevel(1);
                viewItems.add(viewNode);
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
