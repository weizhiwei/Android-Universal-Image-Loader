package com.wzw.ic.mvc.flickr;

import com.wzw.ic.mvc.ViewItem;

import java.util.ArrayList;
import java.util.List;

public class FlickrViewNodePeopleFeeds extends FlickrViewNodePeoplePhotos {

    private static final int COMPACT_TIME_INTERVAL_IN_MILLISECONDS = 1000*3600; // 1 hr
    private List<ViewItem> leftoverViewItems = new ArrayList<ViewItem>();

    public FlickrViewNodePeopleFeeds(String sourceUrl) {
        super(sourceUrl);
    }

    @Override
    public List<ViewItem> reload() {
        return compactViewItems(super.reload());
    }

    @Override
    public List<ViewItem> loadOneMorePage() {
        List<ViewItem> results = super.loadOneMorePage();
        if (null == results || results.isEmpty()) {
            return results;
        }
        List<ViewItem> all = new ArrayList<ViewItem>(leftoverViewItems.size() + results.size());
        all.addAll(leftoverViewItems);
        all.addAll(results);
        return compactViewItems(all);
    }

    private List<ViewItem> compactViewItems(List<ViewItem> results) {
        if (null == results || results.isEmpty()) {
            return results;
        }

        List<ViewItem> compacted = new ArrayList<ViewItem>();
        int runHead = 0;

        while (true) {
            for (int i = 0; i < results.size(); ++i) {
                ViewItem viewItem = results.get(i);
                ViewItem head = results.get(runHead);

                if ((head.getPostedDate().getTime() - viewItem.getPostedDate().getTime()) >
                        COMPACT_TIME_INTERVAL_IN_MILLISECONDS) {
                    List<ViewItem> viewItems = new ArrayList<ViewItem>(i - runHead);
                    for (int j = runHead; j < i; ++j) {
                        viewItems.add(results.get(j));
                    }
                    ViewItem album = new ViewItem(null, null, null, ViewItem.VIEW_TYPE_GRID,
                            new FlickrViewNodePeopleFeed(null, viewItems));
                    album.setPostedDate(head.getPostedDate());
                    compacted.add(album);
                    runHead = i;
                }
            }

            if (!compacted.isEmpty()) {
                break;
            }

            List<ViewItem> nextPage = super.loadOneMorePage();
            if (null == nextPage || nextPage.isEmpty()) {
                break;
            }

            results.addAll(nextPage);
        }

        leftoverViewItems.clear();
        for (int i = runHead; i < results.size(); ++i) {
            leftoverViewItems.add(results.get(i));
        }

        return compacted;
    }

    private static class FlickrViewNodePeopleFeed extends FlickrViewNode {

        public FlickrViewNodePeopleFeed(String sourceUrl, List<ViewItem> viewItems) {
            super(sourceUrl);
            this.viewItems = viewItems;
        }

        @Override
        public List<ViewItem> reload() {
            return this.viewItems;
        }
    }
}
