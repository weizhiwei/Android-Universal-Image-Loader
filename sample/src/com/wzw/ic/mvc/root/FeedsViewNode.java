package com.wzw.ic.mvc.root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import android.os.AsyncTask;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodePeoplePhotos;
import com.wzw.ic.mvc.flickr.FlickrViewNodeStream;
import com.wzw.ic.mvc.moko.MokoViewNodeStream;
import com.wzw.ic.mvc.moko.MokoViewNodeUser;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeStream;

public class FeedsViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBFEEDS = new ViewNode[] {
//		new MokoViewNodeStream(),
		new FlickrViewNodePeoplePhotos("67764677@N07"),
		new MokoViewNodeUser(String.format("http://www.moko.cc/post/%s/new/", "davei1314") + "%d.html"),
		new MokoViewNodeUser(String.format("http://www.moko.cc/post/%s/new/", "zhangqunyun") + "%d.html"),
	};

	public FeedsViewNode() {
		super("feeds");
	}

	@Override
	public boolean supportReloading() {
		return true;
	}
	
	@Override
	public List<ViewItem> reload()  {
		return doLoad(true);
	}
	
	private List<ViewItem> doLoad(final boolean reload) {
		int newPageNo = reload ? 0 : pageNo + 1;
		
		final Object[] subpages = new Object[SUBFEEDS.length];
		final CountDownLatch latch = new CountDownLatch(SUBFEEDS.length);
		for (int i = 0; i < SUBFEEDS.length; ++i) {
			final int index = i;
			new Thread(new Runnable () {

				@Override
				public void run() {
					ViewNode node = SUBFEEDS[index];
					List<ViewItem> page = reload ? node.reload() : node.loadOneMorePage();
					subpages[index] = page;
					latch.countDown();
				}
				
			}).run();
		}
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<ViewItem> pageViewItems = new ArrayList<ViewItem> ();
		for (Object subpage: subpages) {
			if (null != subpage) {
				pageViewItems.addAll((List<ViewItem>) subpage);
			}
		}
		
		Collections.sort(pageViewItems, new Comparator<ViewItem>() {
			@Override
			public int compare(ViewItem lhs, ViewItem rhs) {
				return rhs.getPostedDate().compareTo(lhs.getPostedDate());
			}
		});
		
		if (null != pageViewItems && pageViewItems.size() > 0) {
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
