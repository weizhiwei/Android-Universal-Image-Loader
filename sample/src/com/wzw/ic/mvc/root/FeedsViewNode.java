package com.wzw.ic.mvc.root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.graphics.Bitmap;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.example.universalimageloader.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wzw.ic.mvc.HeaderViewHolder;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodePeoplePhotos;
import com.wzw.ic.mvc.moko.MokoViewNodeUser;

public class FeedsViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBFEEDS = new ViewNode[] {
		new MokoViewNodeUser(String.format("http://www.moko.cc/post/%s/new/", "davei1314") + "%d.html"),
		new MokoViewNodeUser(String.format("http://www.moko.cc/post/%s/new/", "zhangqunyun") + "%d.html"),
		new FlickrViewNodePeoplePhotos("67764677@N07"),
		new FlickrViewNodePeoplePhotos("70058109@N06"),
		new FlickrViewNodePeoplePhotos("85310965@N08"),
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
		
		List<Object> subpageList = Arrays.asList(subpages);
		Collections.sort(subpageList, new Comparator<Object>() {
			@Override
			public int compare(Object lhs, Object rhs) {
				List<ViewItem> l = (List<ViewItem>) lhs,
								r = (List<ViewItem>) rhs;
				if (null != l && !l.isEmpty() &&
					null != r && !r.isEmpty()) {
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
				int n = Math.min(subpageViewItems.size(), 9);
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
        return new FeedsHeaderViewHolder(headerView);
	}
	
	@Override
	public void updateHeaderView(View headerView, final HeaderViewHolder holder, int position) {
		int n = 0;
		for (int i = 0; i < position; ++i) {
			n += headers.get(i);
		}
		ViewItem viewItem = viewItems.get(n);
		
		String caption = "";
		String authorName = (viewItem.getAuthor() == null ? null : viewItem.getAuthor().getLabel());
		if (!TextUtils.isEmpty(authorName)) {
			caption += String.format("<b>%s</b>", authorName);
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
			((FeedsHeaderViewHolder)holder).textView.setText(new SpannableString(Html.fromHtml(caption)));
		}
		
		((FeedsHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
        if (null != viewItem.getAuthor()) {
        	if (!TextUtils.isEmpty(viewItem.getAuthor().getImageUrl())) {
        		((FeedsHeaderViewHolder)holder).imageView.setVisibility(View.VISIBLE);
        		ImageLoader.getInstance().displayImage(viewItem.getAuthor().getImageUrl(),
        				((FeedsHeaderViewHolder)holder).imageView,
                                new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.ic_stub)
                                .showImageForEmptyUri(R.drawable.ic_empty)
                                .showImageOnFail(R.drawable.ic_error)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .displayer(new RoundedBitmapDisplayer(((FeedsHeaderViewHolder)holder).imageView.getLayoutParams().width/2))
                                .build());
			}
        }
	}
	
	private static class FeedsHeaderViewHolder extends HeaderViewHolder {
		public TextView textView;
        public ImageView imageView;

        public FeedsHeaderViewHolder(View convertView) {
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
		
        if (null != viewItem.getAuthor()) {
        	starter.startViewItemActivity(null, viewItem.getAuthor());
        }
	}
	
	public void onFooterClicked(int footer, ViewItemActivityStarter starter) {
		int n = 0;
		for (int i = 0; i < footer; ++i) {
			n += headers.get(i);
		}
		ViewItem viewItem = viewItems.get(n);
		
        if (null != viewItem.getAuthor()) {
        	for (ViewNode pplNode: SUBFEEDS) {
				if (pplNode.getSourceUrl().equals(viewItem.getAuthor().getViewNode().getSourceUrl())) {
					starter.startViewItemActivity(null,
							new ViewItem(null, null, null, ViewItem.VIEW_TYPE_GRID, pplNode));
					break;
				}
			}
        }
    }
	
	@Override
	public void onViewItemClicked(ViewItem viewItem, ViewItemActivityStarter starter) {
		if (null != viewItem.getAuthor()) {
			for (ViewNode pplNode: SUBFEEDS) {
				if (pplNode.getSourceUrl().equals(viewItem.getAuthor().getViewNode().getSourceUrl())) {
					starter.startViewItemActivity(pplNode, viewItem);
					break;
				}
			}
        }
	}
}
