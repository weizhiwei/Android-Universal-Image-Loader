package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewNode;

public class MokoViewNodeAuthor extends MokoViewNode {
	
	private ViewNode authorViewItem;
	
	public MokoViewNodeAuthor(String sourceUrl) {
		super(sourceUrl);
		supportPaging = true;
	}
	
	@Override
	protected List<ViewNode> extractViewItemsFromPage(Document page) {
		if (null == authorViewItem) {
			Elements a = page.select("a#workNickName");
			if (null != a && a.size() > 0) {
				Element e = a.get(0);
				String userId = e.attr("href").replace("/", "");
				if (!TextUtils.isEmpty(userId)) {
					Elements is = page.select("img#imgUserLogo");
					Element i = null;
					if (null != is && is.size() > 0) {
						i = is.get(0);
					}
					String userUrl = String.format("http://www.moko.cc/post/%s/new/", userId) + "%d.html";
					authorViewItem = new ViewNode(
							e.text(),
							userUrl,
							null == i ? "" : i.attr("src"),
							VIEW_TYPE_GRID,
							new MokoViewNodeAuthor(userUrl));
					authorViewItem.setInitialZoomLevel(2);
				}
			}
		}
		
		List<ViewNode> viewItems = null;
		Elements imgElems = page.select("div.coverbox img.cover");
		Elements aElems = page.select("div.coverbox a.coverBg");
		Elements dateElems = page.select("div.show h6");
		if (null != imgElems && imgElems.size() > 0 &&
			null != aElems && imgElems.size() == aElems.size() &&
			null != dateElems && imgElems.size() == dateElems.size()) {
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
				viewItem.setAuthor(authorViewItem);
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
