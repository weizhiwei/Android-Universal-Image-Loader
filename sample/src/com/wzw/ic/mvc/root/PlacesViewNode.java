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
import com.wzw.ic.mvc.flickr.FlickrViewNodeSearch;
import com.wzw.ic.mvc.panoramio.PanoramioViewNodeSightSeeing;

import org.jsoup.nodes.Document;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlacesViewNode extends PanoramioViewNodeSightSeeing {

    static final Pattern COORDS_PATTERN = Pattern.compile(".*lt=([0-9.-]+)[^0-9.-]+ln=([0-9.-]+).*");

	public PlacesViewNode() {
		super();
    }

    @Override
    protected List<ViewItem> extractViewItemsFromPage(Document page) {
        List<ViewItem> viewItems = super.extractViewItemsFromPage(page);
        for (ViewItem viewItem: viewItems) {
            viewItem.setViewType(ViewItem.VIEW_TYPE_GRID);
            FlickrViewNodeSearch searchNode = new FlickrViewNodeSearch(viewItem.getLabel());
            Matcher m = COORDS_PATTERN.matcher(viewItem.getNodeUrl());
            if (m.matches()) {
                searchNode.getSearchParameters().setLatitude(m.group(1));
                searchNode.getSearchParameters().setLongitude(m.group(2));
            }
            viewItem.setViewNode(searchNode);
        }
        return viewItems;
    }

	@Override
	public int getHeaderViewResId(int header, int itemViewType /* card type */) {
		return R.layout.header;
	}
	
	@Override
	public HeaderViewHolder createHolderFromHeaderView(View headerView) {
		return new StreamHeaderViewHolder(headerView);
	}
	
	@Override
	public void updateHeaderView(View headerView, HeaderViewHolder holder, int position) {
		int n = 0;
		for (int i = 0; i < position; ++i) {
			n += headers.get(i);
		}
		ViewItem viewItem = viewItems.get(n);
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
                ImageLoader.getInstance().displayImage(viewItem.getAuthor().getImageUrl(),
                        ((StreamHeaderViewHolder)holder).imageView,
                        new DisplayImageOptions.Builder()
                                .showImageOnLoading(R.drawable.ic_stub)
                                .showImageForEmptyUri(R.drawable.ic_empty)
                                .showImageOnFail(R.drawable.ic_error)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .considerExifParams(true)
                                .displayer(new RoundedBitmapDisplayer(((StreamHeaderViewHolder)holder).imageView.getLayoutParams().width/2))
                                .build());
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
	
//	public void onFooterClicked(int footer, ViewItemActivityStarter starter) {
//		int n = 0;
//		for (int i = 0; i < footer; ++i) {
//			n += headers.get(i);
//		}
//		ViewItem viewItem = viewItems.get(n);
//		if (!TextUtils.isEmpty(viewItem.getOrigin())) {
//			ViewItem originViewItem = RootViewNode.getInstance().findGalleryViewItem(viewItem.getOrigin());
//			if (null != originViewItem) {
//				starter.startViewItemActivity(originViewItem.getViewNode(),
//						((ViewNodeRoot)originViewItem.getViewNode()).getStream());
//			}
//		}
//	}
}
