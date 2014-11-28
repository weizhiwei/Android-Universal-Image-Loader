package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.Date;
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
		Elements dateElems = page.select("div.show h6");
		if (null != imgElems && imgElems.size() > 0 &&
			null != aElems && imgElems.size() == aElems.size() &&
			null != dateElems && imgElems.size() == dateElems.size()) {
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
				viewItem.setOrigin(MOKO_NAME);
				viewItem.setInitialZoomLevel(1);
				try {
					String dateStr = dateElems.get(i).text().split(" ")[1];
					String[] dateStrs = dateStr.split("-");
					viewItem.setPostedDate(new Date(
							Integer.parseInt(dateStrs[0]) - 1900,
							Integer.parseInt(dateStrs[1]) - 1,
							Integer.parseInt(dateStrs[2])));
				} catch (Exception e) {
				}
				viewItems.add(viewItem);
			}
		}
		return viewItems;
	}
}
