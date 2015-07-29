package com.wzw.ic.mvc.root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.example.universalimageloader.R;
import com.nostra13.example.universalimageloader.HeaderViewHolder;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class StreamViewNode extends ViewNode {

	protected int pageNo;
	protected final ViewNode[] SUBSTREAMS;
    protected final Object[] subpages;

	public StreamViewNode(ViewNode viewNode) {
		super("stream");
        SUBSTREAMS = new ViewNode[] { viewNode };
        subpages = new Object[SUBSTREAMS.length];
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

                boolean needToDoLoad = true;
                if (!reload) {
                    for (Object subpage : Arrays.asList(subpages)) {
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
                        ViewNode node = SUBSTREAMS[index];
                        node.load(context, reload, new LoadListener() {
                            @Override
                            public void onLoadDone(ViewNode model) {
                                subpages[index] = model.getViewItems();
                                latch.countDown();
                            }
                        });
                    }

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                List<ViewItem> pageViewItems = new ArrayList<ViewItem>();
                for (Object subpage : Arrays.asList(subpages)) {
                    if (null != subpage) {
                        List<ViewItem> subpageViewItems = (List<ViewItem>) subpage;
                        int n = Math.min(subpageViewItems.size(), 4);
                        for (int i = 0; i < n; ++i) {
                            ViewItem viewItem = subpageViewItems.get(i);
                            viewItem.setViewType(ViewItem.VIEW_TYPE_LIST_TILES);
                            pageViewItems.add(viewItem);
                        }
                        for (int i = 0; i < n; ++i) {
                            subpageViewItems.remove(0); // pop out all the used items
                        }
                    }
                }

                if (null != pageViewItems && pageViewItems.size() > 0) {
                    pageNo = newPageNo;
                    if (reload) {
                        viewItems.clear();
                    }
                    viewItems.addAll(pageViewItems);
                }

                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadListener.onLoadDone(StreamViewNode.this);
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
	public int getWrapperViewResId(int position) {
		return R.layout.header;
	}
	
	@Override
	public HeaderViewHolder createHolderFromHeaderView(View headerView) {
		return new StreamHeaderViewHolder(headerView);
	}
	
	@Override
	public void updateHeaderView(View headerView, HeaderViewHolder holder, int position) {
		ViewItem viewItem = viewItems.get(position);
		String caption = "";
        if (!TextUtils.isEmpty(viewItem.getLabel())) {
            caption += String.format("<b>%s</b>", viewItem.getLabel());
        }
        String authorName = (viewItem.getAuthor() == null ? null : viewItem.getAuthor().getLabel());
        if (!TextUtils.isEmpty(authorName)) {
            if (!TextUtils.isEmpty(caption)) {
				caption += "<br/>  ";
			}
            caption += String.format("by <i>%s</i>", authorName);
        }
//		if (null != viewItem.getPostedDate()) {
//			if (!TextUtils.isEmpty(caption)) {
//				caption += "<br/>  ";
//			}
//			caption += DateUtils.getRelativeTimeSpanString(
//							viewItem.getPostedDate().getTime(), (new Date()).getTime(),
//							DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
//		}
		if (!TextUtils.isEmpty(caption)) {
			((StreamHeaderViewHolder)holder).textView.setText(new SpannableString(Html.fromHtml(caption)));
		}
        ((StreamHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
        if (null != viewItem.getAuthor()) {
            if (!TextUtils.isEmpty(viewItem.getAuthor().getImageUrl())) {
                ((StreamHeaderViewHolder)holder).imageView.setVisibility(View.VISIBLE);
//                ImageLoader.getInstance().displayImage(viewItem.getAuthor().getImageUrl(),
//                        ((StreamHeaderViewHolder)holder).imageView,
//                        new DisplayImageOptions.Builder()
//                                .showImageOnLoading(R.drawable.ic_stub)
//                                .showImageForEmptyUri(R.drawable.ic_empty)
//                                .showImageOnFail(R.drawable.ic_error)
//                                .cacheInMemory(true)
//                                .cacheOnDisk(true)
//                                .considerExifParams(true)
//                                .displayer(new RoundedBitmapDisplayer(((StreamHeaderViewHolder)holder).imageView.getLayoutParams().width/2))
//                                .build());
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
}
