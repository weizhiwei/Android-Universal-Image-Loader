package com.wzw.ic.mvc.flickr;

import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNodeRoot;

public class FlickrViewNodeRoot extends FlickrViewNode implements ViewNodeRoot {

	private ViewItem stream;
	
	public FlickrViewNodeRoot() {
		super("https://www.flickr.com");
		ViewItem viewItemInterestingness = new ViewItem("Interestingness", "interestingness", FLICKR_ICON, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeInterestingness());
		viewItemInterestingness.setInitialZoomLevel(1);
		viewItems.add(viewItemInterestingness);
		ViewItem viewItemCommons = new ViewItem("Commons", "commons", FLICKR_ICON, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeCommons());
		viewItems.add(viewItemCommons);
		ViewItem viewItemGalleries = new ViewItem("Galleries", "https://www.flickr.com/photos/66956608@N06/galleries/", FLICKR_ICON, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodePeopleGalleries("66956608@N06"));
		viewItemGalleries.setInitialZoomLevel(2);
		viewItems.add(viewItemGalleries);
		ViewItem viewItemWzw = new ViewItem("weizhiwei", "https://www.flickr.com/people/67764677@N07/", "http://farm7.staticflickr.com/6178/buddyicons/67764677@N07.jpg", ViewItem.VIEW_TYPE_GRID, new FlickrViewNodePeoplePhotos("67764677@N07"));
		viewItemWzw.setInitialZoomLevel(2);
		viewItems.add(viewItemWzw);
		ViewItem viewItemVk = new ViewItem("Vision Ke", "https://www.flickr.com/people/70058109@N06/", "http://farm7.staticflickr.com/6218/buddyicons/70058109@N06.jpg", ViewItem.VIEW_TYPE_GRID, new FlickrViewNodePeoplePhotosets("70058109@N06"));
		viewItemVk.setInitialZoomLevel(2);
		viewItems.add(viewItemVk);
		ViewItem viewItemAwj = new ViewItem("Alex WJ", "https://www.flickr.com/people/85310965@N08/", "http://farm8.staticflickr.com/7443/buddyicons/85310965@N08.jpg", ViewItem.VIEW_TYPE_GRID, new FlickrViewNodePeoplePhotosets("85310965@N08"));
		viewItemAwj.setInitialZoomLevel(2);
		viewItems.add(viewItemAwj);

		stream = new ViewItem("", "", FLICKR_ICON, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeStream());
	}

	@Override
	public ViewItem getStream() {
		return stream;
	}
}
