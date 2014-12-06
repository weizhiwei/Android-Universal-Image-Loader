package com.wzw.ic.mvc.moko;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Color;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNodeRoot;

public class MokoViewNodeRoot extends MokoViewNode implements ViewNodeRoot {
	
	private ViewItem stream;
	
	public MokoViewNodeRoot() {
		super("http://www.moko.cc/");
		supportPaging = false;
		stream = new ViewItem("New", "new", MOKO_ICON, ViewItem.VIEW_TYPE_GRID, new MokoViewNodeStream());
	}

	@Override
	protected List<ViewItem> extractViewItemsFromPage(Document page) {
		List<ViewItem> viewItems = null;
		Elements aElems = page.select("a.mark");
		if (null != aElems && aElems.size() > 0) {
			viewItems = new ArrayList<ViewItem>();
			for (int i = 0; i < aElems.size(); ++i) {
				Element a = aElems.get(i);
				String url = URL_PREFIX + a.attr("href").replace("/1.", "/%d.");
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
						MOKO_ICON,
						ViewItem.VIEW_TYPE_GRID,
						new MokoViewNodeChannel(url));
				viewItem.setViewItemColor(color);
				viewItem.setViewItemType(ViewItem.VIEW_ITEM_TYPE_COLOR);
				viewItem.setShowingLabelInGrid(true);
//				viewItem.setOrigin(MOKO_NAME);
				viewItem.setInitialZoomLevel(2);
				viewItems.add(viewItem);
			}
			viewItems.add(stream);
		}
		return viewItems;
	}

	@Override
	public ViewItem getStream() {
		return stream;
	}
}
