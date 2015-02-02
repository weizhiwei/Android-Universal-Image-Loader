package com.wzw.ic.mvc.flickr;

import com.wzw.ic.mvc.ViewItem;

import java.util.ArrayList;
import java.util.List;

public class FlickrViewNodePeopleFeeds extends FlickrViewNodePeoplePhotos {

    private static final int COMPACT_TIME_INTERVAL_IN_MILLISECONDS = 1000*3600; // 1 hr

    public FlickrViewNodePeopleFeeds(String sourceUrl) {
        super(sourceUrl);
    }

    @Override
    public List<ViewItem> reload() {
        return compactViewItems(super.reload());
    }

    @Override
    public List<ViewItem> loadOneMorePage() {
        return compactViewItems(super.loadOneMorePage());
    }

    private List<ViewItem> compactViewItems(List<ViewItem> results) {
        if (null == results || results.isEmpty()) {
            return results;
        }

        List<ViewItem> compacted = new ArrayList<ViewItem>();
        int runHead = 0;
        for (int i = 0; i < results.size(); ++i) {
            ViewItem viewItem = results.get(i);
            ViewItem head = results.get(runHead);

            if (viewItem.getPostedDate().getTime()
                - head.getPostedDate().getTime() >
                COMPACT_TIME_INTERVAL_IN_MILLISECONDS) {
                List<ViewItem> viewItems = new ArrayList<ViewItem>(i - runHead);
                for (int j = runHead; j < i; ++j) {
                    viewItems.add(results.get(j));
                }
                ViewItem album = new ViewItem(null, null, null, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodePeopleFeed(null, viewItems));
                album.setPostedDate(head.getPostedDate());
                compacted.add(album);
                runHead = i;
            }
        }
        // close it
        List<ViewItem> viewItems = new ArrayList<ViewItem>(results.size() - runHead);
        for (int j = runHead; j < results.size(); ++j) {
            viewItems.add(results.get(j));
        }
        ViewItem album = new ViewItem(null, null, null, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodePeopleFeed(null, viewItems));
        album.setPostedDate(results.get(runHead).getPostedDate());
        compacted.add(album);

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
