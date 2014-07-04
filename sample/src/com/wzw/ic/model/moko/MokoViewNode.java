package com.wzw.ic.model.moko;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.wzw.ic.model.ViewItem;
import com.wzw.ic.model.ViewNode;

public abstract class MokoViewNode extends ViewNode {

	protected int pageNo;
	protected boolean supportPaging;
	protected String selector;
	
	public MokoViewNode(String sourceUrl) {
		super(sourceUrl);
	}

	@Override
	public void reload()  {
		doLoad(true);
	}

	private void doLoad(boolean reload) {
		Document doc = null;
		int newPageNo = reload ? 1 : pageNo + 1;
		
		try {
			doc = Jsoup.connect(String.format(sourceUrl, newPageNo)).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doc != null) {
			Elements elems = doc.select(selector);
			if (elems.size() > 0) {
				pageNo = newPageNo;
				if (reload) {
					viewItems.clear();
				}
				
				for (Element elem : elems) {
					viewItems.add(getViewItem(elem));
				}
			}
		}
	}
	
	@Override
	public void loadOneMorePage() {
		doLoad(false);
	}

	@Override
	public boolean supportPaging() {
		return supportPaging;
	}
}
