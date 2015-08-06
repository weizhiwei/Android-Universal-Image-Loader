package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wzw.ic.mvc.ViewNode;

public class MokoViewNodeChannel extends MokoViewNode {

	public MokoViewNodeChannel(String sourceUrl) {
		super(sourceUrl);
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
                ViewNode viewItem = new ViewNode(
						title,
						URL_PREFIX + a.attr("href"),
						img.attr("src2"),
						VIEW_TYPE_GRID,
						new MokoViewNodePost(URL_PREFIX + a.attr("href"), title));
				viewItem.setInitialZoomLevel(1);
				viewItems.add(viewItem);
			}
		}
		return viewItems;
	}
}
