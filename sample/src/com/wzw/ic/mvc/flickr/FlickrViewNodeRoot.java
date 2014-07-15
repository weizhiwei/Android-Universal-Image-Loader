package com.wzw.ic.mvc.flickr;

import com.wzw.ic.mvc.ViewItem;

public class FlickrViewNodeRoot extends FlickrViewNode {

	public FlickrViewNodeRoot() {
		super("https://www.flickr.com");
		viewItems.add(new ViewItem("Interestingness", "interestingness", FlickrController.FLICKR_ICON, 0));
		viewItems.add(new ViewItem("Commons", "commons", FlickrController.FLICKR_ICON, 0));
		viewItems.add(new ViewItem("Galleries", "https://www.flickr.com/photos/66956608@N06/galleries/", FlickrController.FLICKR_ICON, 0));
		viewItems.add(new ViewItem("weizhiwei", "https://www.flickr.com/people/67764677@N07/", "http://farm7.staticflickr.com/6178/buddyicons/67764677@N07.jpg", 0));
		viewItems.add(new ViewItem("Vision Ke", "https://www.flickr.com/people/70058109@N06/", "http://farm7.staticflickr.com/6218/buddyicons/70058109@N06.jpg", 0));
		viewItems.add(new ViewItem("Alex WJ", "https://www.flickr.com/people/85310965@N08/", "http://farm8.staticflickr.com/7443/buddyicons/85310965@N08.jpg", 0));
	}
}
