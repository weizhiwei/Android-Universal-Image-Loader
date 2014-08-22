package com.wzw.ic.mvc.fotopedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

public class FotoViewNodeStory extends FotoViewNode {
	public FotoViewNodeStory(String sourceUrl) {
		super(sourceUrl);
	}

	@Override
	public boolean supportReloading() {
		return true;
	}

	@Override
	public void reload() {
		Document doc = null;
		try {
			doc = Jsoup
					.connect(sourceUrl)
					.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doc != null) {
			List<ViewItem> pageViewItems = null;
			Elements figureElems = doc.select("figure");
			if (null != figureElems && figureElems.size() > 0) {
				pageViewItems = new ArrayList<ViewItem>();
				for (int i = 0; i < figureElems.size(); ++i) {
					Element figure = figureElems.get(i);
					Elements imgElems = figure.select(".PCImageView");
					String imgUrl = null;
					if (null != imgElems && imgElems.size() > 0) {
						imgUrl = imgElems.get(0).attr("about");
					}
					if (!TextUtils.isEmpty(imgUrl)) {
						ViewItem viewItem = new ViewItem("", "", imgUrl, ViewItem.VIEW_TYPE_IMAGE_PAGER, this);
						Elements titleElems = figure.select(".cover-title, .regular-title");
						if (null != titleElems && titleElems.size() > 0) {
							viewItem.setLabel(titleElems.get(0).ownText());
						}
						Elements descElems = figure.select(".slide-text");
						if (null != descElems && descElems.size() > 0) {
							viewItem.setStory(descElems.get(0).ownText());
						}
						pageViewItems.add(viewItem);
					}
				}
			}
			
			if (null != pageViewItems && pageViewItems.size() > 0) {
				viewItems.clear();
				viewItems.addAll(pageViewItems);
			}
		}
	}

	@Override
	public boolean supportPaging() {
		return false;
	}

	@Override
	public void loadOneMorePage() {
	}
}
