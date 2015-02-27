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

public class StreamViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBSTREAMS;

	public StreamViewNode(ViewItem gallery) {
		super("stream");
		List<ViewItem> galleryViewItems = gallery.getViewNode().getViewItems();
        int substreamCount = 0;
        for (int i = 0; i < galleryViewItems.size(); ++i) {
            substreamCount += ((ViewNodeRoot)galleryViewItems.get(i).getViewNode()).getStream().size();
        }
		SUBSTREAMS = new ViewNode[substreamCount];
        int k = 0;
		for (int i = 0; i < galleryViewItems.size(); ++i) {
            List<ViewItem> substreams = ((ViewNodeRoot)galleryViewItems.get(i).getViewNode()).getStream();
            for (ViewItem substream: substreams) {
                SUBSTREAMS[k++] = substream.getViewNode();
            }
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
		Collections.sort(subpageList, new Comparator<Object>() {
			@Override
			public int compare(Object lhs, Object rhs) {
				List<ViewItem> l = (List<ViewItem>) lhs,
								r = (List<ViewItem>) rhs;
				if (null != l && !l.isEmpty() &&
					null != r && !r.isEmpty() &&
					null != l.get(0).getPostedDate() &&
					null != r.get(0).getPostedDate()) {
					return r.get(0).getPostedDate().compareTo(
							l.get(0).getPostedDate());
				}
				return 0;
			}
		});
		
		List<ViewItem> pageViewItems = new ArrayList<ViewItem> ();
		List<Integer> pageHeaders = new ArrayList<Integer> ();
		for (Object subpage: subpageList) {
			if (null != subpage) {
				List<ViewItem> subpageViewItems = (List<ViewItem>) subpage;
				int n = Math.min(subpageViewItems.size(), 8);
				for (int i = 0; i < n; ++i) {
					pageViewItems.add(subpageViewItems.get(i));
				}
				if (n > 0) {
					pageHeaders.add(n);
				}
			}
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
		return false;
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
	
//	public void onFooterClicked(int footer, ViewItemActivityStarter starter) {
//		int n = 0;
//		for (int i = 0; i < footer; ++i) {
//			n += headers.get(i);
//		}
//		ViewItem viewItem = viewItems.get(n);
//		if (!TextUtils.isEmpty(viewItem.getOrigin())) {
//			ViewItem originViewItem = RootViewNode.getInstance().findGalleryViewItem(viewItem.getOrigin());
//			if (null != originViewItem) {
//				starter.startViewItemActivity(originViewItem.getViewNode(),
//						((ViewNodeRoot)originViewItem.getViewNode()).getStream());
//			}
//		}
//	}
//
//	@Override
//	public void onViewItemClicked(ViewItem viewItem, ViewItemActivityStarter starter) {
//		if (!TextUtils.isEmpty(viewItem.getOrigin())) {
//			ViewItem originViewItem = RootViewNode.getInstance().findGalleryViewItem(viewItem.getOrigin());
//			if (null != originViewItem) {
//				ViewNode streamNode = ((ViewNodeRoot)originViewItem.getViewNode()).getStream().getViewNode();
//				starter.startViewItemActivity(streamNode, viewItem);
//			}
//		}
//	}
}
