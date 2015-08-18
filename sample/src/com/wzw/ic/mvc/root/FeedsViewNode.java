package com.wzw.ic.mvc.root;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.os.AsyncTask;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;

import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeAuthor;
import com.wzw.ic.mvc.moko.MokoViewNodeFollowing;

public class FeedsViewNode extends ViewNode {

	protected int pageNo;
	protected List<ViewNode> subfeeds;
    protected List<List<ViewNode>> subpages;

	public FeedsViewNode(ViewNode parent) {
		super(parent);
	}

    @Override
	public boolean supportReloading() {
		return true;
	}

	@Override
    public void load(final boolean reload, final LoadListener loadListener) {
        new AsyncTask<Void, Void, Void> () {
            @Override
            protected Void doInBackground(Void... params) {
                int newPageNo = reload ? 0 : pageNo + 1;

                if (reload || null == subfeeds || subfeeds.isEmpty()) {
                    ViewNode following = new MokoViewNodeFollowing(FeedsViewNode.this);
                    final CountDownLatch latch = new CountDownLatch(1);
                    following.load(true, new LoadListener() { // TODO: load all not just one page
                        @Override
                        public void onLoadDone(ViewNode model) {
                            subfeeds = model.getChildren();
                            subpages = new ArrayList<List<ViewNode>>(subfeeds.size());
                            for (int i = 0; i < subfeeds.size(); ++i) {
                                subpages.add(new ArrayList<ViewNode>());
                            }
                            latch.countDown();
                        }
                    });

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                int needToDoLoadCount = subfeeds.size();
                if (!reload) {
                    needToDoLoadCount = 0;
                    for (List<ViewNode> subpage : subpages) {
                        if (subpage.isEmpty()) {
                            ++needToDoLoadCount;
                        }
                    }
                }

                if (needToDoLoadCount > 0) {

                    final CountDownLatch latch = new CountDownLatch(needToDoLoadCount);
                    for (int i = 0; i < subfeeds.size(); ++i) {
                        if (reload || subpages.get(i).isEmpty()) {
                            final int index = i;
                            subfeeds.get(i).load(reload, new LoadListener() {
                                @Override
                                public void onLoadDone(ViewNode model) {
                                    subpages.get(index).clear();
                                    subpages.get(index).addAll(model.getChildren());
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

                final List<ViewNode> albumViewItems = new ArrayList<ViewNode>();
                while (true) {
                    int index = -1;
                    Date date = new Date(0);
                    for (int i = 0; i < subpages.size(); ++i) {
                        List<ViewNode> subpage = subpages.get(i);
                        if (!subpage.isEmpty()) {
                            if (null != subpage.get(0).getPostedDate() &&
                                    subpage.get(0).getPostedDate().after(date)) {
                                date = subpage.get(0).getPostedDate();
                                index = i;
                            }
                        }
                    }

                    if (index == -1) { // nothing to add
                        break;
                    } else {
                        List<ViewNode> subpage = subpages.get(index);
                        ViewNode viewItem = subpage.remove(0);
                        viewItem.setViewType(VIEW_TYPE_LIST_TILES);
                        albumViewItems.add(viewItem);
                        if (albumViewItems.size() > 5 || subpage.isEmpty()) {
                            // if we have exhausted any list, we need to stop to do a reload, in order to maintain the getPostedDate order
                            break;
                        }
                    }
                }

                if (null != albumViewItems && albumViewItems.size() > 0) {
                    pageNo = newPageNo;
                    if (reload) {
                        children.clear();
                    }
                    children.addAll(albumViewItems);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                loadListener.onLoadDone(FeedsViewNode.this);
            }
        }.execute();
	}

	@Override
	public boolean supportPaging() {
		return true;
	}
	
	@Override
	public void updateWrapperView(View headerView, final WrapperViewHolder holder, int position) {
		ViewNode viewItem = children.get(position);
		
		String caption = "";
		String authorName = (viewItem.getAuthor() == null ? null : viewItem.getAuthor().getTitle());
		if (!TextUtils.isEmpty(authorName)) {
			caption += String.format(
                    "<b>%s</b> posted %d picture%s", authorName, 10, 10 > 1 ? "s" : "");
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
			holder.textView.setText(new SpannableString(Html.fromHtml(caption)));
		}
		
		holder.imageView.setVisibility(View.GONE);
        if (null != viewItem.getAuthor()) {
        	if (!TextUtils.isEmpty(viewItem.getAuthor().getImageUrl())) {
        		holder.imageView.setVisibility(View.VISIBLE);
			}
        }
	}
}
