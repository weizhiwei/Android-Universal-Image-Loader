package com.wzw.ic.mvc.root;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.example.universalimageloader.R;
import com.wzw.ic.mvc.HeaderViewHolder;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrViewNodeStream;
import com.wzw.ic.mvc.moko.MokoViewNodeStream;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeStream;

public class StreamViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBSTREAMS = new ViewNode[] {
		new MokoViewNodeStream(),
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
				int n = Math.min(subpageViewItems.size(), 4);
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
		StreamHeaderViewHolder holder = new StreamHeaderViewHolder();
        holder.textView = (TextView)headerView.findViewById(R.id.text);
        holder.imageView = (ImageView)headerView.findViewById(R.id.image);
        return holder;
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
			caption += ("last updated on "
	        		+ DateFormat.getDateInstance().format(viewItem.getPostedDate()));
		}
		if (!TextUtils.isEmpty(caption)) {
			((StreamHeaderViewHolder)holder).textView.setText(new SpannableString(Html.fromHtml(caption)));
		}
		if (null != originViewItem) {
			holder.model = RootViewNode.getInstance().getGalleryViewItem().getViewNode();
        	holder.viewItem = originViewItem;
        	((StreamHeaderViewHolder)holder).imageView.setVisibility(View.VISIBLE);
        	((StreamHeaderViewHolder)holder).imageView.setImageResource(originViewItem.getViewItemImageResId());
        } else {
        	((StreamHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
        }
	}
	
	private static class StreamHeaderViewHolder extends HeaderViewHolder {
        public TextView textView;
        public ImageView imageView;
    }
}
