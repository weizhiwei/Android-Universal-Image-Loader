package com.wzw.ic.mvc.fotopedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
			Elements imgElems = doc.select("figure div.PCImageView");
			if (null != imgElems && imgElems.size() > 0) {
				pageViewItems = new ArrayList<ViewItem>();
				for (int i = 0; i < imgElems.size(); ++i) {
					Element img = imgElems.get(i);
					pageViewItems.add(new ViewItem("", "", img.attr("about"), 0));
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
