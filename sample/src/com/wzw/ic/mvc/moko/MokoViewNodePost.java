package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewNode;

public class MokoViewNodePost extends MokoViewNode {

	public MokoViewNodePost(ViewNode parent, String sourceUrl) {
		super(parent, sourceUrl);
		supportPaging = false;
	}

	@Override
	protected List<ViewNode> extractViewItemsFromPage(Document page) {
		if (null == author) {
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
                    author = new MokoViewNodeAuthor(null, userUrl);
                    author.setTitle(e.text());
                    author.setImageUrl(null == i ? "" : i.attr("src"));
                    author.setInitialZoomLevel(2);
				}
			}
		}

        if (TextUtils.isEmpty(title)) {
            Elements a = page.select(".sTitle .video-link");
            if (null != a && a.size() > 0) {
                Element e = a.get(0);
                String t = e.attr("title");
                if (!TextUtils.isEmpty(t)) {
                    title = t;
                }
            }
        }

		List<ViewNode> viewItems = null;
		Elements imgElems = page.select("p.picbox img");
		if (null != imgElems && imgElems.size() > 0) {
			viewItems = new ArrayList<ViewNode>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				ViewNode viewNode = new MokoViewNodePost(this, sourceUrl);
                viewNode.setImageUrl(img.attr("src2"));
                viewNode.setAuthor(author);
				viewItems.add(viewNode);
			}
		}
		return viewItems;
	}

    @Override
    public int getViewType(int container) {
        switch (container) {
            case VIEW_TYPE_PAGER:
                return VIEW_TYPE_GRID;
            case VIEW_TYPE_LIST:
                return VIEW_TYPE_TILE;
        }
        return super.getViewType(container);
    }

}
