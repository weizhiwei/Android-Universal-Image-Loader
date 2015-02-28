package com.wzw.ic.mvc.nationalgeographic;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public abstract class NGViewNode extends ViewNode {

	public static String NG_NAME = "nationalgeographic";
	public static String NG_ICON = "http://a266.phobos.apple.com/us/r1000/087/Purple2/v4/6d/20/4b/6d204b68-468b-22c8-a831-8d26535edbbb/mzl.keeyqfwh.png";
	protected static String URL_PREFIX = "http://photography.nationalgeographic.com";
	
	public NGViewNode(String sourceUrl) {
		super(sourceUrl);
        supportPaging = false;
	}
	protected int pageNo;
	protected boolean supportPaging;
	
	@Override
	public boolean supportReloading() {
		return true;
	}
	
	@Override
	public List<ViewItem> reload()  {
		return doLoad(true);
	}

	private List<ViewItem> doLoad(boolean reload) {
		List<ViewItem> pageViewItems = null;
		Document doc = null;
		int newPageNo = reload ? 1 : pageNo + 1;
		
		try {
			doc = Jsoup
					.connect(String.format(sourceUrl, newPageNo))
					.get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doc != null) {
			pageViewItems = extractViewItemsFromPage(doc);
			if (null != pageViewItems && pageViewItems.size() > 0) {
				pageNo = newPageNo;
				if (reload) {
					viewItems.clear();
				}
				viewItems.addAll(pageViewItems);
			}
		}
		return pageViewItems;
	}
	
	@Override
	public List<ViewItem> loadOneMorePage() {
		return doLoad(false);
	}

	@Override
	public boolean supportPaging() {
		return supportPaging;
	}

	protected abstract List<ViewItem> extractViewItemsFromPage(Document page);
}
