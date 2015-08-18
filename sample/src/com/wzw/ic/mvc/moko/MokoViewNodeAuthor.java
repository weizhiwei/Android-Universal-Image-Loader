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
	
	public MokoViewNodeAuthor(ViewNode parent, String userId) {
        super(parent, String.format(URL_PREFIX+"/post/%s/new/%%d.html", userId));
		supportPaging = true;
	}
	
	@Override
	protected List<ViewNode> extractViewItemsFromPage(Document page) {
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
                ViewNode viewNode = new MokoViewNodePost(this, URL_PREFIX + a.attr("href"), title);
                viewNode.setTitle(title);
                viewNode.setImageUrl(img.attr("src2"));
                viewNode.setViewType(VIEW_TYPE_GRID);
				viewNode.setInitialZoomLevel(1);
				viewNode.setAuthor(this);
				try {
					String dateStr = dateElems.get(i).text().split(" ")[1];
					String[] dateStrs = dateStr.split("-");
					viewNode.setPostedDate(new Date(
                            Integer.parseInt(dateStrs[0]) - 1900,
                            Integer.parseInt(dateStrs[1]) - 1,
                            Integer.parseInt(dateStrs[2])));
				} catch (Exception e) {
				}
				viewItems.add(viewNode);
			}
		}
		return viewItems;
	}
}
