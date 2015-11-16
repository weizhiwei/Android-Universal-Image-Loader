package com.wzw.ic.mvc.root;

import android.os.AsyncTask;

import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.moko.MokoViewNodeFollowing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class StreamViewNode extends ViewNode {

    protected ViewNode source;
	protected int pageNo;
	protected List<ViewNode> subfeeds;
    protected List<List<ViewNode>> subpages;

	public StreamViewNode(ViewNode source) {
		super(null);
        this.source = source;
	}

    @Override
	public boolean supportReloading() {
		return false;
	}

	@Override
    public void load(final boolean reload, final LoadListener loadListener) {
        new AsyncTask<Void, Void, Void> () {
            @Override
            protected Void doInBackground(Void... params) {
                int newPageNo = reload ? 0 : pageNo + 1;

                if (reload || null == subfeeds || subfeeds.isEmpty()) {
                    final CountDownLatch latch = new CountDownLatch(1);
                    source.load(reload, new LoadListener() { // TODO: load all not just one page
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
                loadListener.onLoadDone(StreamViewNode.this);
            }
        }.execute();
	}

	@Override
	public boolean supportPaging() {
		return true;
	}
}
