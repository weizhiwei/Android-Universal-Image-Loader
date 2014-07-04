package com.wzw.ic.model.moko;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImageListActivity;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.wzw.ic.model.ViewItem;
import com.wzw.ic.model.ViewNode;

public abstract class MokoViewNode extends ViewNode {

	protected int pageNo;
	protected boolean supportPaging;
	protected static String URL_PREFIX = "http://www.moko.cc";
	
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
			List<ViewItem> pageViewItems = extractViewItemsFromPage(doc);
			if (null != pageViewItems && pageViewItems.size() > 0) {
				pageNo = newPageNo;
				if (reload) {
					viewItems.clear();
				}
				viewItems.addAll(pageViewItems);
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
