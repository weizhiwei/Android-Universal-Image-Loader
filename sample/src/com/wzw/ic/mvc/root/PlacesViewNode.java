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
    protected final Object[] subheaderItems;

	public PlacesViewNode(ViewNode[] viewNodes) {
        super("places");
        SUBSTREAMS = viewNodes;
        subpages = new Object[SUBSTREAMS.length];
        subheaders = new Object[SUBSTREAMS.length];
        subheaderItems = new Object[SUBSTREAMS.length];
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
                        subheaderItems[index] = node.getHeaderItems();
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
        final List<ViewItem> headerViewItems = new ArrayList<ViewItem> ();

        boolean aggregateMode = false;

        for (int subpageIndex = 0; subpageIndex < subpages.length; ++subpageIndex) {
            if (null != subpages[subpageIndex]) {
                List<ViewItem> subpageViewItems = (List<ViewItem>) subpages[subpageIndex];
                List<Integer> subpageHeaders = (List<Integer>) subheaders[subpageIndex];
                List<ViewItem> subpageHeaderItems = (List<ViewItem>) subheaderItems[subpageIndex];

                if (aggregateMode &&
                    (null != subpageHeaders && !subpageHeaders.isEmpty() && null != subpageHeaderItems && subpageHeaderItems.size() == subpageHeaders.size())) {
                    int offset = 0;
                    int m = Math.min(subpageHeaders.size(), 1);
                    for (int j = 0; j < m; ++j) {
                        int header = subpageHeaders.get(j);
                        int n = Math.min(header, 4 + (new Random()).nextInt(6));
                        for (int i = 0; i < n; ++i) {
                            ViewItem viewItem = subpageViewItems.get(offset + (new Random()).nextInt(header));
                            albumViewItems.add(viewItem);
                        }
                        albumHeaders.add(n);
                        headerViewItems.add(subpageHeaderItems.get(j));

                        offset += header;
                    }
                    for (int i = 0; i < m; ++i) {
                        subpageHeaders.remove(0); // pop out all the used items
                        subpageHeaderItems.remove(0);
                    }
                    for (int i = 0; i < offset; ++i) {
                        subpageViewItems.remove(0);
                    }
                } else {
                    int n = Math.min(subpageViewItems.size(), 5);
                    for (int i = 0; i < n; ++i) {
                        int idx = (new Random()).nextInt(subpageViewItems.size());
                        ViewItem viewItem = subpageViewItems.get(idx);
                        albumViewItems.add(viewItem);
                        subpageViewItems.remove(idx); // pop out all the used items
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

        List<ViewItem> resultViewItems = aggregateMode ? albumViewItems : new ArrayList<ViewItem>();
        for (int i = 0; i < albumViewItems.size(); ++i) {
            List<ViewItem> subpage2 = (List<ViewItem>) subpages2[i];
            if (null != subpage2 && !subpage2.isEmpty()) {
                if (aggregateMode) {
                    albumViewItems.get(i).setImageUrl(subpage2.get(0).getImageUrl());
                } else {
                    resultViewItems.addAll(subpage2);
                    albumHeaders.add(subpage2.size());
                    albumViewItems.get(i).setImageUrl(subpage2.get(0).getImageUrl());
                    headerViewItems.add(albumViewItems.get(i));
                }
            }
        }

        if (null != resultViewItems && resultViewItems.size() > 0) {
            pageNo = newPageNo;
            if (reload) {
                viewItems.clear();
                headers.clear();
                headerItems.clear();
            }
            viewItems.addAll(resultViewItems);
            headers.addAll(albumHeaders);
            headerItems.addAll(headerViewItems);
        }

        return albumViewItems;
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
        HeaderViewHolder holder = new StreamHeaderViewHolder(headerView);
        holder.footer = null;
        return holder;
    }

    @Override
    public void updateHeaderView(View headerView, HeaderViewHolder holder, int position) {
        ViewItem headerItem = headerItems.get(position);
        String caption = "";
        if (!TextUtils.isEmpty(headerItem.getLabel())) {
            caption += String.format("<a>%s</a>", headerItem.getLabel());
        }
        if (!TextUtils.isEmpty(caption)) {
            ((StreamHeaderViewHolder)holder).textView.setText(new SpannableString(Html.fromHtml(caption)));
        }
        ((StreamHeaderViewHolder)holder).imageView.setVisibility(View.GONE);
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
}
