package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNodeAction;

public class MokoViewNodePost extends MokoViewNode {

	private ViewItem userViewItem;
	
	public MokoViewNodePost(String sourceUrl) {
		super(sourceUrl);
		supportPaging = false;
		actions = Arrays.asList(new ViewNodeAction(R.id.action_moko_see_user, "by user"));
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		if (null == userViewItem) {
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
					userViewItem = new ViewItem(
							e.text(),
							userUrl,
							null == i ? "" : i.attr("src"),
							ViewItem.VIEW_TYPE_GRID,
							new MokoViewNodeUser(userUrl));
					ViewNodeAction seeUserAction = actions.get(0);
					seeUserAction.setTitle("by " + userViewItem.getLabel());
					seeUserAction.setVisible(true);
				}
			}
		}
		
		List<ViewItem> viewItems = null;
		Elements imgElems = page.select("p.picbox img");
		if (null != imgElems && imgElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < imgElems.size(); ++i) {
				Element img = imgElems.get(i);
				viewItems.add(new ViewItem("", "", img.attr("src2"), ViewItem.VIEW_TYPE_IMAGE_PAGER, this));
			}
		}
		return viewItems;
	}
	
	@Override
	public Object onAction(ViewNodeAction action) {
		return userViewItem;
	}
}
