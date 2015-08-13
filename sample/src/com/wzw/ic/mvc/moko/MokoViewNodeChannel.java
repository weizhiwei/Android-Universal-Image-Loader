package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wzw.ic.mvc.ViewNode;

public class MokoViewNodeChannel extends MokoViewNode {

	public MokoViewNodeChannel(ViewNode parent, String sourceUrl) {
		super(parent, sourceUrl);
		supportPaging = true;
	}
	
	@Override
	protected List<ViewNode> extractViewItemsFromPage(Document page) {
		List<ViewNode> viewItems = null;
		Elements imgElems = page.select("ul.post div.cover img");
		Elements aElems = page.select("ul.post div.cover a");
		if (null != imgElems && imgElems.size() > 0 &&
			null != aElems && aElems.size() > 0 &&
			imgElems.size() == aElems.size()) {
			viewItems = new ArrayList<ViewNode>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				Element a = aElems.get(i);
				String title = img.attr("alt");
                ViewNode viewItem = new MokoViewNodePost(this, URL_PREFIX + a.attr("href"), title);
                viewItem.setTitle(title);
				viewItem.setImageUrl(img.attr("src2"));
				viewItem.setViewType(VIEW_TYPE_GRID);
				viewItem.setInitialZoomLevel(1);
				viewItems.add(viewItem);
			}
		}
		return viewItems;
	}
}
