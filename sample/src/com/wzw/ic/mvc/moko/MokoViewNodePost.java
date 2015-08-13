package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewNode;

public class MokoViewNodePost extends MokoViewNode {

	private ViewNode authorViewNode;
	private String pageTitle;
	
	public MokoViewNodePost(ViewNode parent, String sourceUrl, String pageTitle) {
		super(parent, sourceUrl);
		supportPaging = false;
		this.pageTitle = pageTitle;
	}

	@Override
	protected List<ViewNode> extractViewItemsFromPage(Document page) {
		if (null == authorViewNode) {
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
                    authorViewNode = new MokoViewNodeAuthor(null, userUrl);
                    authorViewNode.setTitle(e.text());
                    authorViewNode.setImageUrl(null == i ? "" : i.attr("src"));
                    authorViewNode.setViewType(VIEW_TYPE_GRID);
                    authorViewNode.setInitialZoomLevel(2);
				}
			}
		}
		
		List<ViewNode> viewItems = null;
		Elements imgElems = page.select("p.picbox img");
		if (null != imgElems && imgElems.size() > 0) {
			viewItems = new ArrayList<ViewNode>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				ViewNode viewNode = new MokoViewNodePost(this, sourceUrl, pageTitle);
                viewNode.setImageUrl(img.attr("src2"));
                viewNode.setViewType(VIEW_TYPE_IMAGE_PAGER);
                viewNode.setAuthor(authorViewNode);
				viewItems.add(viewNode);
			}
		}
		return viewItems;
	}
}
