package com.wzw.ic.mvc.root;

import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
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
import com.wzw.ic.mvc.flickr.FlickrViewNodeSearch;
import com.wzw.ic.mvc.panoramio.PanoramioViewNodeSightSeeing;

import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlacesViewNode extends ViewNode {

    static final Pattern COORDS_PATTERN = Pattern.compile(".*lt=([0-9.-]+)[^0-9.-]+ln=([0-9.-]+).*");

    protected int pageNo;
    protected final ViewNode[] SUBSTREAMS;
    protected final Object[] subpages;
    protected final Object[] subheaders;

	public PlacesViewNode(ViewNode[] viewNodes) {
        super("places");
        SUBSTREAMS = viewNodes;
        subpages = new Object[SUBSTREAMS.length];
        subheaders = new Object[SUBSTREAMS.length];
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
                        subheaders[index] = node.getHeaders(); // TODO: assume no node.loadOneMorePage() here
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

        final List<Integer> albumHeaders = new ArrayList<Integer>();
        final List<ViewItem> albumViewItems = new ArrayList<ViewItem> ();
        for (int subpageIndex = 0; subpageIndex < subpages.length; ++subpageIndex) {
            if (null != subpages[subpageIndex]) {
                List<ViewItem> subpageViewItems = (List<ViewItem>) subpages[subpageIndex];
                List<Integer> subpageHeaders = (List<Integer>) subheaders[subpageIndex];

                if (null != subpageHeaders && !subpageHeaders.isEmpty()) {
                    int offset = 0;
                    int m = Math.min(subpageHeaders.size(), 3);
                    for (int j = 0; j < m; ++j) {
                        int header = subpageHeaders.get(j);
                        int n = Math.min(header, 4 + (new Random()).nextInt(6));
                        for (int i = 0; i < n; ++i) {
                            ViewItem viewItem = subpageViewItems.get(offset + (new Random()).nextInt(header));
                            albumViewItems.add(viewItem);
                        }
                        albumHeaders.add(n);

                        offset += header;
                    }
                    for (int i = 0; i < m; ++i) {
                        subpageHeaders.remove(0); // pop out all the used items
                    }
                    for (int i = 0; i < offset; ++i) {
                        subpageViewItems.remove(0);
                    }
                } else {
                    int n = Math.min(subpageViewItems.size(), 9);
                    for (int i = 0; i < n; ++i) {
                        ViewItem viewItem = subpageViewItems.get(i);
                        albumViewItems.add(viewItem);
                    }
                    for (int i = 0; i < n; ++i) {
                        subpageViewItems.remove(0); // pop out all the used items
                    }
                }

                for (ViewItem viewItem: albumViewItems) {
                    viewItem.setViewType(ViewItem.VIEW_TYPE_GRID);
                    FlickrViewNodeSearch searchNode = new FlickrViewNodeSearch(viewItem.getLabel());
                    Matcher m = COORDS_PATTERN.matcher(viewItem.getNodeUrl());
                    if (m.matches()) {
                        searchNode.getSearchParameters().setLatitude(m.group(1));
                        searchNode.getSearchParameters().setLongitude(m.group(2));
                    }
                    searchNode.setPerPage(1); // for later search
                    viewItem.setViewNode(searchNode);
                }
            }
        }

        // search for a thumbnail..
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

        for (int i = 0; i < albumViewItems.size(); ++i) {
            List<ViewItem> subpage2 = (List<ViewItem>) subpages2[i];
            if (null != subpage2 && !subpage2.isEmpty()) {
                albumViewItems.get(i).setImageUrl(subpage2.get(0).getImageUrl());
                ((FlickrViewNodeSearch) albumViewItems.get(i).getViewNode()).setPerPage(30);
            }
        }

        if (null != albumViewItems && albumViewItems.size() > 0) {
            pageNo = newPageNo;
            if (reload) {
                viewItems.clear();
                headers.clear();
            }
            viewItems.addAll(albumViewItems);
            headers.addAll(albumHeaders);
        }

        return albumViewItems;
    }

    @Override
    public List<ViewItem> loadOneMorePage() {
        return doLoad(false);
    }

    @Override
    public boolean supportPaging() {
        return false;
    }
}
