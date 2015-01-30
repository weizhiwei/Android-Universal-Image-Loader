package com.wzw.ic.mvc.root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.HeaderViewHolder;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeRoot;

public class StreamViewNode2 extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBSTREAMS;

	public StreamViewNode2(ViewItem gallery) {
		super("stream");
		List<ViewItem> galleryViewItems = gallery.getViewNode().getViewItems();
		SUBSTREAMS = new ViewNode[galleryViewItems.size()];
		for (int i = 0; i < galleryViewItems.size(); ++i) {
			SUBSTREAMS[i] = ((ViewNodeRoot)galleryViewItems.get(i).getViewNode()).getStream().getViewNode();
		}
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
		
		List<Object> subpageList = Arrays.asList(subpages);
		
		List<List<ViewItem>> picViewItems = new ArrayList<List<ViewItem>> ();
		final List<ViewItem> albumViewItems = new ArrayList<ViewItem> ();
		for (Object subpage: subpageList) {
			if (null != subpage) {
				List<ViewItem> subpageViewItems = (List<ViewItem>) subpage;
				int n = Math.min(subpageViewItems.size(), 4);
				for (int i = 0; i < n; ++i) {
					ViewItem viewItem = subpageViewItems.get(i);
					if (viewItem.getViewType() == ViewItem.VIEW_TYPE_IMAGE_PAGER) {
						List<ViewItem> dummy = new ArrayList<ViewItem> (1);
						dummy.add(viewItem);
						picViewItems.add(dummy);
					} else {
						albumViewItems.add(viewItem);
					}
				}
			}
		}
		
		// unfold albums
		final Object[] subpages2 = new Object[albumViewItems.size()];
		final CountDownLatch latch2 = new CountDownLatch(albumViewItems.size());
		for (int i = 0; i < albumViewItems.size(); ++i) {
			final int index = i;
			new Thread(new Runnable () {

				@Override
				public void run() {
					ViewNode node = albumViewItems.get(index).getViewNode();
					List<ViewItem> page = node.reload();
					subpages2[index] = page;
					latch2.countDown();
				}
				
			}).run();
		}
		
		try {
			latch2.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Object> subpageList2 = Arrays.asList(subpages2);
        for (Object subpage2: subpageList2) {
			if (null != subpage2) {
				picViewItems.add((List<ViewItem>) subpage2);
			}
		}

//        Collections.shuffle(picViewItems);

		List<ViewItem> pageViewItems = new ArrayList<ViewItem> ();
		List<Integer> pageHeaders = new ArrayList<Integer> ();
        for (List<ViewItem> subpageViewItems: picViewItems) {
			pageViewItems.addAll(subpageViewItems);
			pageHeaders.add(subpageViewItems.size());
		}
		
		if (null != pageViewItems && pageViewItems.size() > 0) {
			pageNo = newPageNo;
			if (reload) {
				viewItems.clear();
				headers.clear();
			}
			viewItems.addAll(pageViewItems);
			headers.addAll(pageHeaders);
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
	
	@Override
	public int getHeaderViewResId(int header, int itemViewType /* card type */) {
		return R.layout.header;
	}
	
	@Override
	public HeaderViewHolder createHolderFromHeaderView(View headerView) {
		return new StreamHeaderViewHolder(headerView);
	}
	
	@Override
	public void updateHeaderView(View headerView, HeaderViewHolder holder, int position) {
		int n = 0;
		for (int i = 0; i < position; ++i) {
			n += headers.get(i);
		}
		ViewItem viewItem = viewItems.get(n);
		String caption = "";
		ViewItem originViewItem = null;
		if (!TextUtils.isEmpty(viewItem.getOrigin())) {
			originViewItem = RootViewNode.getInstance().findGalleryViewItem(viewItem.getOrigin());
			caption += String.format("<b>%s</b>", viewItem.getOrigin());
		}
		if (null != viewItem.getPostedDate()) {
			if (!TextUtils.isEmpty(caption)) {
				caption += "<br/>";
			}
			caption += ("last updated: "
					+ DateUtils.getRelativeTimeSpanString(
							viewItem.getPostedDate().getTime(), (new Date()).getTime(),
							DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
		}
		if (!TextUtils.isEmpty(caption)) {
			((StreamHeaderViewHolder)holder).textView.setText(new SpannableString(Html.fromHtml(caption)));
		}
		if (null != originViewItem) {
			((StreamHeaderViewHolder)holder).imageView.setVisibility(View.VISIBLE);
        	((StreamHeaderViewHolder)holder).imageView.setImageResource(originViewItem.getViewItemImageResId());
        } else {
        	((StreamHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
        }
	}
	
	private static class StreamHeaderViewHolder extends HeaderViewHolder {
		public TextView textView;
        public ImageView imageView;

        public StreamHeaderViewHolder(View convertView) {
			super(convertView);
			textView = (TextView)convertView.findViewById(R.id.text);
	        imageView = (ImageView)convertView.findViewById(R.id.image);
		}
    }
	
	public void onHeaderClicked(int header, ViewItemActivityStarter starter) {
		int n = 0;
		for (int i = 0; i < header; ++i) {
			n += headers.get(i);
		}
		ViewItem viewItem = viewItems.get(n);
		if (!TextUtils.isEmpty(viewItem.getOrigin())) {
			ViewItem originViewItem = RootViewNode.getInstance().findGalleryViewItem(viewItem.getOrigin());
			if (null != originViewItem) {
				starter.startViewItemActivity(RootViewNode.getInstance().getGalleryViewItem().getViewNode(),
						originViewItem);
			}
		}
	}
	
	public void onFooterClicked(int footer, ViewItemActivityStarter starter) {
		int n = 0;
		for (int i = 0; i < footer; ++i) {
			n += headers.get(i);
		}
		ViewItem viewItem = viewItems.get(n);
		if (!TextUtils.isEmpty(viewItem.getOrigin())) {
			ViewItem originViewItem = RootViewNode.getInstance().findGalleryViewItem(viewItem.getOrigin());
			if (null != originViewItem) {
				starter.startViewItemActivity(originViewItem.getViewNode(),
						((ViewNodeRoot)originViewItem.getViewNode()).getStream());
			}
		}
	}
}
