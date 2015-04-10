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
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class PlacesViewNode extends ViewNode {

    static final Pattern COORDS_PATTERN = Pattern.compile(".*lt=([0-9.-]+)[^0-9.-]+ln=([0-9.-]+).*");

    static final int MODE_COVER = 0;
    static final int MODE_PLACES = 1;
    static final int MODE_SIGHTS = 2;
    static final int CHUNK_SIZES[] = {2, 1, 3};

    protected int pageNo;
    protected final ViewNode[] SUBSTREAMS;
    protected final Object[] subpages;
    protected final int mode;

	public PlacesViewNode(ViewNode[] viewNodes, int mode) {
        super("explorer");
        SUBSTREAMS = viewNodes;
        subpages = new Object[SUBSTREAMS.length];
        this.mode = mode;
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

        final List<ViewItem> albumViewItems = new ArrayList<ViewItem> ();
        for (int subpageIndex = 0; subpageIndex < SUBSTREAMS.length; ++subpageIndex) {
            if (null != subpages[subpageIndex]) {
                List<ViewItem> subpageViewItems = (List<ViewItem>) subpages[subpageIndex];

                int n = Math.min(subpageViewItems.size(), CHUNK_SIZES[mode]);
                for (int i = 0; i < n; ++i) {
                    int idx = (new Random()).nextInt(subpageViewItems.size());
                    ViewItem viewItem = subpageViewItems.remove(idx); // pop out all the used items
                    albumViewItems.add(viewItem);
                }
            }
        }

        List<ViewItem> resultViewItems = null;
        final List<Integer> albumHeaders = new ArrayList<Integer>();
        List<ViewItem> headerViewItems = null;

        if (MODE_PLACES == mode) {
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
                    int n = Math.min(subpage1.size(), 2 + (new Random()).nextInt(3));
                    for (int j = 0; j < n; ++j) {
                        int idx = (new Random()).nextInt(subpage1.size());
                        ViewItem viewItem = subpage1.remove(idx);
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
        final CountDownLatch latch2 = new CountDownLatch(resultViewItems.size() + MODE_COVER == mode ? 1 : 0);

        final String[] titleStr = new String[1];
        if (MODE_COVER == mode) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LonelyPlanetViewNodeBreadCrumbs nodeBreadCrumbs = new LonelyPlanetViewNodeBreadCrumbs(String.format(SUBSTREAMS[0].getSourceUrl(), 1));
                    List<ViewItem> breadCrumbs = nodeBreadCrumbs.reload();
                    if (null != breadCrumbs && !breadCrumbs.isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("<small>");
                        for (int i = 0; i < breadCrumbs.size() - 1; ++i) {
                            sb.append(breadCrumbs.get(i).getLabel());
                            sb.append(" / ");
                        }
                        sb.append("</small><br />");
                        sb.append(breadCrumbs.get(breadCrumbs.size() - 1).getLabel());
                        titleStr[0] = sb.toString();
                    }

                    latch2.countDown();
                }

            }).run();
        }

        for (int i = 0; i < resultViewItems.size(); ++i) {
            final int index = i;
            final List<ViewItem> finalResultViewItems = resultViewItems;
            new Thread(new Runnable () {

                @Override
                public void run() {

                    ViewItem viewItem = finalResultViewItems.get(index);

                    viewItem.setViewType(ViewItem.VIEW_TYPE_GRID);
                    FlickrViewNodeSearch searchNode = new FlickrViewNodeSearch(removeDiacritic(viewItem.getLabel()));
//                    Matcher m = COORDS_PATTERN.matcher(viewItem.getNodeUrl());
//                    if (m.matches()) {
//                        searchNode.getSearchParameters().setLatitude(m.group(1));
//                        searchNode.getSearchParameters().setLongitude(m.group(2));
//                    };
                    searchNode.setPerPage(MODE_COVER == mode ? 150 : 30);

                    List<ViewItem> page = null;
                    final int TRY_QUALIFIED_QUERY = 1;
                    final int TRY_UNQUALIFIED_QUERY = 0;
                    for (int i = 0; i < TRY_QUALIFIED_QUERY && (null == page || page.isEmpty()); ++i) {
                        page = searchNode.reload();
                    }
                    if (null == page || page.isEmpty()) {
                        String qualifiedQuery = viewItem.getLabel();
                        int index = qualifiedQuery.lastIndexOf(',');
                        if (index > 0) {
                            String unqualifiedQuery = qualifiedQuery.substring(0, index);
                            searchNode.getSearchParameters().setText(removeDiacritic(unqualifiedQuery));
                            for (int i = 0; i < TRY_UNQUALIFIED_QUERY && (null == page || page.isEmpty()); ++i) {
                                page = searchNode.reload();
                            }
                        }
                    }

                    if (null != page && !page.isEmpty()) {
                        ViewItem picViewItem = null;
                        Collections.shuffle(page);
                        for (ViewItem vi: page) {
                            if (vi.getStory().length() > 350) {
                                picViewItem = vi;
                                break;
                            }
                        }
                        if (picViewItem == null) {
                            picViewItem = page.get(0);
                        }
                        viewItem.setStory(picViewItem.getStory());
                        viewItem.setImageUrl(picViewItem.getImageUrl());
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

        // clean up failed items
        List<ViewItem> resultViewItems2 = new ArrayList<ViewItem>(resultViewItems.size());
        List<Integer> albumHeaders2 = new ArrayList<Integer>(albumHeaders.size());
        List<ViewItem> headerViewItems2 = new ArrayList<ViewItem>(headerViewItems.size());
        int idxInGroup, countInGroup, idxInterGroup;

        idxInGroup = 0;
        countInGroup = 0;
        idxInterGroup = 0;
        for (ViewItem viewItem: resultViewItems) {
            if (!TextUtils.isEmpty(viewItem.getImageUrl())) {
                resultViewItems2.add(viewItem);
                ++countInGroup;
            }
            if (++idxInGroup == albumHeaders.get(idxInterGroup)) {
                if (countInGroup > 0) {
                    albumHeaders2.add(countInGroup);
                    headerViewItems2.add(headerViewItems.get(idxInterGroup));

                    // we only need one pic for MODE_COVER
                    if (MODE_COVER == mode) {
                        if (!TextUtils.isEmpty(titleStr[0])) {
                            ViewItem headerViewItem = new ViewItem(null, null, null, 0, null);
                            headerViewItem.setLabel(titleStr[0]);
                            headerViewItems2.set(0, headerViewItem);
                        }
                        break;
                    }
                }
                idxInGroup = 0;
                countInGroup = 0;
                ++idxInterGroup;
            }
        }

        if (null != resultViewItems2 && resultViewItems2.size() > 0) {
            pageNo = newPageNo;
            if (reload) {
                viewItems.clear();
                headers.clear();
                headerItems.clear();
            }
            viewItems.addAll(resultViewItems2);
            headers.addAll(albumHeaders2);
            headerItems.addAll(headerViewItems2);
        }

        return albumViewItems;
    }

    @Override
    public List<ViewItem> loadOneMorePage() {
        return doLoad(false);
    }

    @Override
    public boolean supportPaging() {
        return MODE_COVER != mode;
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
     * Mirror of the unicode table from 00c0 to 024f without diacritics.
     */
    //Latin 1 - Latin Extended-B
    private static final String tab00c0 = "aaaaaaaceeeeiiii" +
            "DNOOOOO\u00d7\u00d8UUUUYI\u00df" +
            "aaaaaaaceeeeiiii" +
            "\u00f0nooooo\u00f7\u00f8uuuuy\u00fey" +
            "aaaaaaccccccccdd" +
            "ddeeeeeeeeeegggg" +
            "gggghhhhiiiiiiii" +
            "iijjjjkkklllllll" +
            "lllnnnnnnnnnoooo" +
            "oooorrrrrrssssss" +
            "ssttttttuuuuuuuu" +
            "uuuuwwyyyzzzzzzf" +
            "bbbbbboccddddoee" +
            "effgyhltikklawnn" +
            "ooooopprsseltttt" +
            "uuuuyyzz3ee3255t" +
            "plll!dddjjjnnnaa" +
            "iioouuuuuuuuuuea" +
            "aaaaaggggkkoooo3" +
            "3jdddgghpnnaaaao" +
            "oaaaaeeeeiiiiooo" +
            "orrrruuuusstt33h" +
            "hnd88zzaaeeooooo" +
            "oooyybnbjbpacclt" +
            "sz??buaeejjqrrryy";

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
            if (one >= '\u00c0' && one <= '\u024f') {
                one = tab00c0.charAt((int) one - '\u00c0');
            }
            vysl[i] = one;
        }
        return new String(vysl);
    }
}
