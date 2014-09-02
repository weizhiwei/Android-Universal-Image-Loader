package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wzw.ic.mvc.ViewItem;

public class MokoViewNodeUser extends MokoViewNode {
	public MokoViewNodeUser(String sourceUrl) {
		super(sourceUrl);
		supportPaging = true;
	}
	
	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements imgElems = page.select("div.coverbox img.cover");
		Elements aElems = page.select("div.coverbox a.coverBg");
		if (null != imgElems && imgElems.size() > 0 &&
			null != aElems && aElems.size() > 0 &&
			imgElems.size() == aElems.size()) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				Element a = aElems.get(i);
				String title = img.attr("alt");
				viewItems.add(new ViewItem(
						title,
						URL_PREFIX + a.attr("href"),
						img.attr("src2"),
						ViewItem.VIEW_TYPE_GRID,
						new MokoViewNodePost(URL_PREFIX + a.attr("href"), title)));
			}
		}
		return viewItems;
	}
}
