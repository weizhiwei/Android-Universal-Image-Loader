package com.wzw.ic.mvc.moko;

import android.graphics.Color;

import com.wzw.ic.mvc.ViewItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MokoViewNodeRoot extends MokoViewNode {
	
	public MokoViewNodeRoot() {
		super("http://www.moko.cc/");
		supportPaging = false;
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements aElems = page.select("a.mark");
		if (null != aElems && aElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < aElems.size(); ++i) {
				Element a = aElems.get(i);
                String url = null;
                url = a.attr("href").replace("=1", "=%d");
                int color = 0;
				for (String className: a.classNames()) {
					if (className.matches("^c.+-bg$")) {
						String colorString = className.substring(1, className.length() - 3);
						if (3 == colorString.length()) {
							colorString = colorString.replaceAll("(.)", "$1$1"); // double every char
						}
						color = Color.parseColor("#" + colorString);
					}
				}
				ViewItem viewItem = new ViewItem(
						a.ownText(),
						url,
						null,
						ViewItem.VIEW_TYPE_GRID,
						new MokoViewNodeChannel(url));
				viewItem.setViewItemColor(color);
				viewItem.setViewItemType(ViewItem.VIEW_ITEM_TYPE_COLOR);
				viewItem.setShowingLabelInGrid(true);
				viewItem.setInitialZoomLevel(2);
				viewItems.add(viewItem);
			}
		}
		return viewItems;
	}
}
