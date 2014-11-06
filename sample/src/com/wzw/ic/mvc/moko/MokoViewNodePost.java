package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

public class MokoViewNodePost extends MokoViewNode {

	private ViewItem authorViewItem;
	private String pageTitle;
	
	public MokoViewNodePost(String sourceUrl, String pageTitle) {
		super(sourceUrl);
		supportPaging = false;
		this.pageTitle = pageTitle;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
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
					authorViewItem = new ViewItem(
							e.text(),
							userUrl,
							null == i ? "" : i.attr("src"),
							ViewItem.VIEW_TYPE_GRID,
							new MokoViewNodeUser(userUrl));
					authorViewItem.setOrigin(MOKO_NAME);
					authorViewItem.setInitialZoomLevel(2);
				}
			}
		}
		
		List<ViewItem> viewItems = null;
		Elements imgElems = page.select("p.picbox img");
		if (null != imgElems && imgElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				ViewItem viewItem = new ViewItem(pageTitle, sourceUrl, img.attr("src2"), ViewItem.VIEW_TYPE_IMAGE_PAGER, this);
				viewItem.setAuthor(authorViewItem);
				viewItem.setOrigin(MOKO_NAME);
				viewItems.add(viewItem);
			}
		}
		return viewItems;
	}
}
