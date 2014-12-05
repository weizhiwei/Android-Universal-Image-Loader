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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wzw.ic.mvc.HeaderViewHolder;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeRoot;
import com.wzw.ic.mvc.ViewNode.ViewItemActivityStarter;
import com.wzw.ic.mvc.flickr.FlickrViewNodePeoplePhotos;
import com.wzw.ic.mvc.moko.MokoViewNodeUser;

public class FeedsViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBFEEDS = new ViewNode[] {
//		new MokoViewNodeStream(),
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
	public int getHeaderViewResId() {
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
		
        if (null != viewItem.getAuthor()) {
        	((FeedsHeaderViewHolder)holder).imageView.setVisibility(View.VISIBLE);
        	ImageLoader.getInstance().loadImage(viewItem.getAuthor().getImageUrl(), new ImageLoadingListener() {
					
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						((FeedsHeaderViewHolder)holder).imageView.setImageBitmap(loadedImage);
					}
					
					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub
						
					}
				});
        } else {
        	((FeedsHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
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
	
	@Override
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
	
	@Override
	public void onFooterClicked(int footer, ViewItemActivityStarter starter) {
		onHeaderClicked(footer, starter);
	}
	
	@Override
	public void onViewItemClicked(ViewItem viewItem, ViewItemActivityStarter starter) {
		if (null != viewItem.getAuthor()) {
        	starter.startViewItemActivity(viewItem.getAuthor().getViewNode(), viewItem);
        }
	}
}
