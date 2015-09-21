package com.wzw.ic.mvc.moko;

import android.graphics.Color;

import com.wzw.ic.mvc.ViewNode;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class MokoViewNodeRoot extends MokoViewNode {
	
	public MokoViewNodeRoot(ViewNode parent) {
		super(parent, URL_PREFIX);
		supportPaging = false;
	}

	@Override
	protected List<ViewNode> extractViewItemsFromPage(Document page) {
		List<ViewNode> viewItems = null;
		Elements aElems = page.select("a.mark");
		if (null != aElems && aElems.size() > 0) {
			viewItems = new ArrayList<ViewNode>();
			for (int i = 0; i < aElems.size(); ++i) {
				Element a = aElems.get(i);
                String url = null;
                url = a.attr("href").replace("curPage=1", "curPage=%d");
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
                ViewNode viewItem = new MokoViewNodeChannel(this, url);
                viewItem.setTitle(a.ownText());
                viewItem.setViewItemColor(color);
				viewItem.setViewItemType(VIEW_ITEM_TYPE_COLOR);
				viewItem.setShowingLabelInGrid(true);
				viewItems.add(viewItem);
			}
		}
		return viewItems;
	}

    @Override
    public int getViewType(int container) {
        switch (container) {
            case VIEW_TYPE_PAGER:
                return VIEW_TYPE_GRID;
        }
        return super.getViewType(container);
    }

}
