package com.wzw.ic.mvc.root;

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
import com.wzw.ic.mvc.flickr.FlickrViewNodeSearch;
import com.wzw.ic.mvc.lonelyplanet.LonelyPlanetViewNodeBreadCrumbs;
import com.wzw.ic.mvc.lonelyplanet.LonelyPlanetViewNodePlaces;
import com.wzw.ic.mvc.lonelyplanet.LonelyPlanetViewNodeSights;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class PlacesViewNode extends ViewNode {

    static final Pattern COORDS_PATTERN = Pattern.compile(".*lt=([0-9.-]+)[^0-9.-]+ln=([0-9.-]+).*");

    protected int pageNo;
    protected final ViewNode[] SUBSTREAMS;
    protected final Object[] subpages;

	public PlacesViewNode(ViewNode[] viewNodes) {
        super("explorer");
        SUBSTREAMS = viewNodes;
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

        final boolean placesMode = SUBSTREAMS[0] instanceof LonelyPlanetViewNodePlaces;

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

        final List<ViewItem> albumViewItems = new ArrayList<ViewItem> ();
        for (int subpageIndex = 0; subpageIndex < SUBSTREAMS.length; ++subpageIndex) {
            if (null != subpages[subpageIndex]) {
                List<ViewItem> subpageViewItems = (List<ViewItem>) subpages[subpageIndex];
                int n = Math.min(subpageViewItems.size(), 5);
                for (int i = 0; i < n; ++i) {
                    int idx = (new Random()).nextInt(subpageViewItems.size());
                    ViewItem viewItem = subpageViewItems.get(idx);
                    albumViewItems.add(viewItem);
                    subpageViewItems.remove(idx); // pop out all the used items
                }
            }
        }

        List<ViewItem> resultViewItems = null;
        final List<Integer> albumHeaders = new ArrayList<Integer>();
        List<ViewItem> headerViewItems = null;

        if (placesMode) {
            final Object[] subpages1 = new Object[albumViewItems.size()];
            final CountDownLatch latch1 = new CountDownLatch(albumViewItems.size());
            for (int i = 0; i < albumViewItems.size(); ++i) {
                final int index = i;
                new Thread(new Runnable () {

                    @Override
                    public void run() {
                        ViewItem viewItem = albumViewItems.get(index);

                        LonelyPlanetViewNodeSights sightsNode = new LonelyPlanetViewNodeSights(viewItem.getNodeUrl() + "/sights.html?page=%d");
                        List<ViewItem> page = sightsNode.reload();
                        subpages1[index] = page;

                        latch1.countDown();
                    }

                }).run();
            }

            try {
                latch1.await();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            resultViewItems = new ArrayList<ViewItem>();
            headerViewItems = new ArrayList<ViewItem>();
            for (int i = 0; i < albumViewItems.size(); ++i) {
                List<ViewItem> subpage1 = (List<ViewItem>) subpages1[i];
                if (null != subpage1 && !subpage1.isEmpty()) {
                    int n = Math.min(subpage1.size(), 3 + (new Random()).nextInt(4));
                    for (int j = 0; j < n; ++j) {
//                        int idx = (new Random()).nextInt(subpage1.size());
                        int idx = j;
                        ViewItem viewItem = subpage1.get(idx);
                        resultViewItems.add(viewItem);
                    }
                    albumHeaders.add(n);
                    headerViewItems.add(albumViewItems.get(i));
                }
            }
        } else {
            resultViewItems = albumViewItems;
            for (int i = 0; i < albumViewItems.size(); ++i) {
                albumHeaders.add(1);
            }
            headerViewItems = albumViewItems;
        }

        // search for a thumbnail..
        final CountDownLatch latch2 = new CountDownLatch(resultViewItems.size());
        for (int i = 0; i < resultViewItems.size(); ++i) {
            final int index = i;
            final List<ViewItem> finalResultViewItems = resultViewItems;
            new Thread(new Runnable () {

                @Override
                public void run() {

                    ViewItem viewItem = finalResultViewItems.get(index);

                    String unqualifiedQuery = viewItem.getLabel();
                    String qualifiedQuery = viewItem.getLabel();
                    StringBuilder sb = new StringBuilder();

                    LonelyPlanetViewNodeBreadCrumbs nodeBreadCrumbs = new LonelyPlanetViewNodeBreadCrumbs(viewItem.getNodeUrl());
                    List<ViewItem> breadCrumbs = nodeBreadCrumbs.reload();
                    if (null != breadCrumbs && !breadCrumbs.isEmpty()) {
                        sb.append("<small>");
                        for (ViewItem vi : breadCrumbs) {
                            sb.append(vi.getLabel());
                            sb.append(" / ");
                        }
                        sb.append("</small><br />");

                        if (!breadCrumbs.isEmpty()) {
                            qualifiedQuery += ", ";
                            qualifiedQuery += breadCrumbs.get(breadCrumbs.size() - 1).getLabel();
                        }
                    }
                    sb.append(viewItem.getLabel());

                    viewItem.setLabel(placesMode ? qualifiedQuery : sb.toString());
                    viewItem.setViewType(ViewItem.VIEW_TYPE_GRID);
                    FlickrViewNodeSearch searchNode = new FlickrViewNodeSearch(removeDiacritic(qualifiedQuery));
//                    Matcher m = COORDS_PATTERN.matcher(viewItem.getNodeUrl());
//                    if (m.matches()) {
//                        searchNode.getSearchParameters().setLatitude(m.group(1));
//                        searchNode.getSearchParameters().setLongitude(m.group(2));
//                    };
                    searchNode.setPerPage(5);

                    List<ViewItem> page = null;
                    for (int i = 0; i < 3 && (null == page || page.isEmpty()); ++i) {
                        page = searchNode.reload();
                    }

                    if (null == page || page.isEmpty()) {
                        searchNode.getSearchParameters().setText(removeDiacritic(unqualifiedQuery));
                        for (int i = 0; i < 3 && (null == page || page.isEmpty()); ++i) {
                            page = searchNode.reload();
                        }
                    }

                    if (null != page && !page.isEmpty()) {
                        viewItem.setImageUrl(page.get((new Random()).nextInt(page.size())).getImageUrl());
                    }

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

    /**
     * Mirror of the unicode table from 00c0 to 017f without diacritics.
     */
    private static final String tab00c0 = "AAAAAAACEEEEIIII" +
            "DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
            "aaaaaaaceeeeiiii" +
            "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
            "AaAaAaCcCcCcCcDd" +
            "DdEeEeEeEeEeGgGg" +
            "GgGgHhHhIiIiIiIi" +
            "IiJjJjKkkLlLlLlL" +
            "lLlNnNnNnnNnOoOo" +
            "OoOoRrRrRrSsSsSs" +
            "SsTtTtTtUuUuUuUu" +
            "UuUuWwYyYZzZzZzF";

    /**
     * Returns string without diacritics - 7 bit approximation.
     *
     * @param source string to convert
     * @return corresponding string without diacritics
     */
    public static String removeDiacritic(String source) {
        char[] vysl = new char[source.length()];
        char one;
        for (int i = 0; i < source.length(); i++) {
            one = source.charAt(i);
            if (one >= '\u00c0' && one <= '\u017f') {
                one = tab00c0.charAt((int) one - '\u00c0');
            }
            vysl[i] = one;
        }
        return new String(vysl);
    }
}
