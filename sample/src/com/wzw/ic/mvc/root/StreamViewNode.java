package com.wzw.ic.mvc.root;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

import android.os.AsyncTask;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodeStream;
import com.wzw.ic.mvc.moko.MokoViewNodeStream;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeStream;

public class StreamViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBSTREAMS = new ViewNode[] {
//		new MokoViewNodeStream(),
		new FlickrViewNodeStream(),
		new NGViewNodeStream(),
	};

	public StreamViewNode() {
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
	
	private List<ViewItem> doLoad(final boolean reload) {
		int newPageNo = reload ? 0 : pageNo + 1;
		
		final Object[] subpages = new Object[SUBSTREAMS.length];
		final CountDownLatch latch = new CountDownLatch(SUBSTREAMS.length);
		for (int i = 0; i < SUBSTREAMS.length; ++i) {
			final int index = i;
			new Thread(new Runnable () {

				@Override
				public void run() {
					ViewNode node = SUBSTREAMS[index];
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
		int round = 0;
		boolean itemsAdded = false;
		do {
			itemsAdded = false;
			for (Object subpage: subpages) {
				List<ViewItem> subpageViewItems = (List<ViewItem>) subpage;
				if (null != subpageViewItems &&
					round < subpageViewItems.size()) {
					pageViewItems.add(subpageViewItems.get(round));
					itemsAdded = true;
				}
			}
			++round;
		} while (itemsAdded);
		
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
