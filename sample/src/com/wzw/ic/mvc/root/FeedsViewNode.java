package com.wzw.ic.mvc.root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.Context;
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
import com.wzw.ic.mvc.moko.MokoViewNodeAuthor;

public class FeedsViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBFEEDS = new ViewNode[] {
		new MokoViewNodeAuthor(String.format("http://www.moko.cc/post/%s/new/", "davei1314") + "%d.html"),
		new MokoViewNodeAuthor(String.format("http://www.moko.cc/post/%s/new/", "zhangqunyun") + "%d.html"),
	};
    protected final Object[] subpages;

	public FeedsViewNode() {
		super("feeds");
        subpages = new Object[SUBFEEDS.length];
	}

    @Override
	public boolean supportReloading() {
		return true;
	}
	
	@Override
    public List<ViewItem> load(final Context context, final boolean reload, final LoadListener loadListener) {
        new Thread(new Runnable () {

            @Override
            public void run() {
                int newPageNo = reload ? 0 : pageNo + 1;

                int needToDoLoadCount = SUBFEEDS.length;
                if (!reload) {
                    needToDoLoadCount = 0;
                    for (Object subpage : Arrays.asList(subpages)) {
                        if (null == subpage || ((List<ViewItem>) subpage).isEmpty()) {
                            ++needToDoLoadCount;
                        }
                    }
                }

                if (needToDoLoadCount > 0) {

                    final CountDownLatch latch = new CountDownLatch(needToDoLoadCount);
                    for (int i = 0; i < SUBFEEDS.length; ++i) {
                        if (reload || null == subpages[i] || ((List<ViewItem>) subpages[i]).isEmpty()) {
                            ViewNode node = SUBFEEDS[i];
                            final int index = i;
                            node.load(context, reload, new LoadListener() {
                                @Override
                                public void onLoadDone(ViewNode model) {
                                    subpages[index] = model.getViewItems();
                                    latch.countDown();
                                }
                            });
                        }
                    }

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                final List<ViewItem> albumViewItems = new ArrayList<ViewItem>();
                while (true) {
                    int index = -1;
                    Date date = new Date(0);
                    for (int i = 0; i < subpages.length; ++i) {
                        List<ViewItem> subpageViewItems = (List<ViewItem>) subpages[i];
                        if (null != subpageViewItems && !subpageViewItems.isEmpty()) {
                            if (null != subpageViewItems.get(0).getPostedDate() &&
                                    subpageViewItems.get(0).getPostedDate().after(date)) {
                                date = subpageViewItems.get(0).getPostedDate();
                                index = i;
                            }
                        }
                    }

                    if (index == -1) { // nothing to add
                        break;
                    } else {
                        List<ViewItem> subpageViewItems = (List<ViewItem>) subpages[index];
                        albumViewItems.add(subpageViewItems.remove(0));
                        if (albumViewItems.size() > 5 ||
                                subpageViewItems.isEmpty()) {
                            // if we have exhausted any list, we need to stop to do a reload, in order to maintain the getPostedDate order
                            break;
                        }
                    }
                }

                List<ViewItem> pageViewItems = new ArrayList<ViewItem>();
                List<Integer> pageHeaders = new ArrayList<Integer>();
                for (int i = 0; i < albumViewItems.size(); ++i) {
                    final ViewItem viewItem = albumViewItems.get(i);
                    pageViewItems.add(viewItem);
                    if (viewItem.getViewType() == ViewItem.VIEW_TYPE_IMAGE_PAGER) {
                        pageHeaders.add(1);
                    } else {
                        pageViewItems.add(viewItem);
                        pageHeaders.add(2);
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

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadListener.onLoadDone(FeedsViewNode.this);
                    }
                });
            }
        }).start();
        return null;
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
			((FeedsHeaderViewHolder)holder).textView.setText(new SpannableString(Html.fromHtml(caption)));
		}
		
		((FeedsHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
        if (null != viewItem.getAuthor()) {
        	if (!TextUtils.isEmpty(viewItem.getAuthor().getImageUrl())) {
        		((FeedsHeaderViewHolder)holder).imageView.setVisibility(View.VISIBLE);
//        		ImageLoader.getInstance().displayImage(viewItem.getAuthor().getImageUrl(),
//        				((FeedsHeaderViewHolder)holder).imageView,
//                                new DisplayImageOptions.Builder()
//                                .showImageOnLoading(R.drawable.ic_stub)
//                                .showImageForEmptyUri(R.drawable.ic_empty)
//                                .showImageOnFail(R.drawable.ic_error)
//                                .cacheInMemory(true)
//                                .cacheOnDisk(true)
//                                .considerExifParams(true)
//                                .displayer(new RoundedBitmapDisplayer(((FeedsHeaderViewHolder)holder).imageView.getLayoutParams().width/2))
//                                .build());
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
