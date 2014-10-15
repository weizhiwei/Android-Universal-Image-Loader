package com.wzw.ic.mvc.stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nostra13.example.universalimageloader.IcDatabase;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodeStream;
import com.wzw.ic.mvc.moko.MokoViewNodeStream;

public class StreamViewNodeRoot extends StreamViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBSTREAMS = new ViewNode[] {
		new MokoViewNodeStream(),
		new FlickrViewNodeStream(),
	};

	public StreamViewNodeRoot() {
		super("stream");
	}

	@Override
	public boolean supportReloading() {
		return true;
	}
	
	@Override
	public List<ViewItem> reload()  {
		return doLoad(true);
	}
	
	private List<ViewItem> doLoad(boolean reload) {
		int newPageNo = reload ? 0 : pageNo + 1;
		
		List<ViewItem> pageViewItems = new ArrayList<ViewItem> ();
		for (ViewNode node: SUBSTREAMS) {
			List<ViewItem> subpage = reload ? node.reload() : node.loadOneMorePage();
			if (null != subpage) {
				pageViewItems.addAll(subpage);
			}
		}
		
		if (null != pageViewItems && pageViewItems.size() > 0) {
			Collections.sort(pageViewItems);
			
			pageNo = newPageNo;
			if (reload) {
				viewItems.clear();
			}
			viewItems.addAll(pageViewItems);
		}
		
		return pageViewItems;
	}
	
	@Override
	public List<ViewItem> loadOneMorePage() {
		return doLoad(false);
	}

	@Override
	public boolean supportPaging() {
		return true;
	}
}
