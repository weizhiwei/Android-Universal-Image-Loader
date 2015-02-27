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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.wzw.ic.mvc.HeaderViewHolder;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.ViewNodeRoot;

public class StreamViewNode2 extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBSTREAMS;
    protected final Object[] subpages;

	public StreamViewNode2(ViewItem gallery) {
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
        subpages = new Object[SUBSTREAMS.length];
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

        boolean needToDoLoad = true;
        if (!reload) {
            for (Object subpage: Arrays.asList(subpages)) {
                if (null != subpage && !((List<ViewItem>) subpage).isEmpty()) {
                    needToDoLoad = false;
                    break;
                }
            }
        }

        if (needToDoLoad) {
            final CountDownLatch latch = new CountDownLatch(SUBSTREAMS.length);
            for (int i = 0; i < SUBSTREAMS.length; ++i) {
                final int index = i;
                new Thread(new Runnable() {

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
        }

		List<List<ViewItem>> picViewItems = new ArrayList<List<ViewItem>> ();
		final List<ViewItem> albumViewItems = new ArrayList<ViewItem> ();
		for (Object subpage: Arrays.asList(subpages)) {
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
                for (int i = 0; i < n; ++i) {
                    subpageViewItems.remove(0); // pop out all the used items
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
        String authorName = (viewItem.getAuthor() == null ? null : viewItem.getAuthor().getLabel());
        if (!TextUtils.isEmpty(authorName)) {
            caption += String.format(
                    "<b>%s</b> posted %d picture%s", authorName, headers.get(position), headers.get(position) > 1 ? "s" : "");
        }
		if (null != viewItem.getPostedDate()) {
			if (!TextUtils.isEmpty(caption)) {
				caption += "<br/>";
			}
			caption += DateUtils.getRelativeTimeSpanString(
							viewItem.getPostedDate().getTime(), (new Date()).getTime(),
							DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
		}
		if (!TextUtils.isEmpty(caption)) {
			((StreamHeaderViewHolder)holder).textView.setText(new SpannableString(Html.fromHtml(caption)));
		}
        ((StreamHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
        if (null != viewItem.getAuthor()) {
            if (!TextUtils.isEmpty(viewItem.getAuthor().getImageUrl())) {
                ((StreamHeaderViewHolder)holder).imageView.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(viewItem.getAuthor().getImageUrl(),
                        ((StreamHeaderViewHolder)holder).imageView,
                        new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.ic_stub)
                                .showImageForEmptyUri(R.drawable.ic_empty)
                                .showImageOnFail(R.drawable.ic_error)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .displayer(new RoundedBitmapDisplayer(((StreamHeaderViewHolder)holder).imageView.getLayoutParams().width/2))
                                .build());
            }
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
}
