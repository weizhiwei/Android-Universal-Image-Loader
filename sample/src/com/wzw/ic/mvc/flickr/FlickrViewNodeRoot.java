package com.wzw.ic.mvc.flickr;

import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodeRoot extends FlickrViewNode {

	public FlickrViewNodeRoot() {
		super("https://www.flickr.com");
		ViewItem viewItemInterestingness = new ViewItem("Interestingness", "interestingness", FLICKR_ICON, ViewItem.VIEW_TYPE_GRID, new FlickrViewNodeInterestingness());
		viewItemInterestingness.setInitialZoomLevel(2);
		viewItems.add(viewItemInterestingness);
		viewItems.add(new ViewItem("Commons", "commons", FLICKR_ICON, ViewItem.VIEW_TYPE_LIST, new FlickrViewNodeCommons()));
		viewItems.add(new ViewItem("Galleries", "https://www.flickr.com/photos/66956608@N06/galleries/", FLICKR_ICON, ViewItem.VIEW_TYPE_LIST, new FlickrViewNodePeopleGalleries("66956608@N06")));
		viewItems.add(new ViewItem("weizhiwei", "https://www.flickr.com/people/67764677@N07/", "http://farm7.staticflickr.com/6178/buddyicons/67764677@N07.jpg", ViewItem.VIEW_TYPE_LIST, new FlickrViewNodePeoplePhotosets("67764677@N07")));
		viewItems.add(new ViewItem("Vision Ke", "https://www.flickr.com/people/70058109@N06/", "http://farm7.staticflickr.com/6218/buddyicons/70058109@N06.jpg", ViewItem.VIEW_TYPE_LIST, new FlickrViewNodePeoplePhotosets("70058109@N06")));
		viewItems.add(new ViewItem("Alex WJ", "https://www.flickr.com/people/85310965@N08/", "http://farm8.staticflickr.com/7443/buddyicons/85310965@N08.jpg", ViewItem.VIEW_TYPE_LIST, new FlickrViewNodePeoplePhotosets("85310965@N08")));
	}
}
