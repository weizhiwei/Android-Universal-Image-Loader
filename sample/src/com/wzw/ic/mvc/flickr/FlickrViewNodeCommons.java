package com.wzw.ic.mvc.flickr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.text.TextUtils;

import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodeCommons extends FlickrViewNode {
	protected int pageNo;
	
	public FlickrViewNodeCommons() {
		super("https://www.flickr.com/commons");
	}

	@Override
	public boolean supportReloading() {
		return true;
	}

	@Override
	public List<ViewItem> reload() {
		return doLoad(true);
	}

	private List<ViewItem> doLoad(boolean reload) {
		List<ViewItem> pageViewItems = null;
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
			Elements instElems = doc.select("#tc_institutions_list a");
			if (null != instElems && instElems.size() > 0) {
				pageViewItems = new ArrayList<ViewItem>();
				for (int i = 0; i < instElems.size(); ++i) {
					Element inst = instElems.get(i);
					Elements imgElems = inst.select("img");
					String imgUrl = null;
					if (null != imgElems && imgElems.size() > 0) {
						imgUrl = imgElems.get(0).attr("src");
					}
					if (!TextUtils.isEmpty(imgUrl)) {
						String id = imgUrl.substring(
								imgUrl.indexOf("/buddyicons/") + 12,
								imgUrl.indexOf(".jpg"));
						
						ViewItem viewItem = new ViewItem(
								inst.attr("title"),
								String.format("https://www.flickr.com/people/%s/", id),
								imgUrl,
								ViewItem.VIEW_TYPE_LIST,
								new FlickrViewNodePeoplePhotosets(id));
						pageViewItems.add(viewItem);
					}
				}
			}
			
			if (null != pageViewItems && pageViewItems.size() > 0) {
				viewItems.clear();
				viewItems.addAll(pageViewItems);
			}
		}
		
		return pageViewItems;
	}
	
	@Override
	public boolean supportPaging() {
		return false;
	}

	@Override
	public List<ViewItem> loadOneMorePage() {
		return null;
	}
}
