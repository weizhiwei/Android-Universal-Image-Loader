package com.wzw.ic.mvc.flickr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.googlecode.flickrjandroid.photos.Extras;
import com.wzw.ic.mvc.ViewNode;

public abstract class FlickrViewNode extends ViewNode {
	
	public static String FLICKR_ICON = "https://farm4.staticflickr.com/3741/buddyicons/66956608@N06_r.jpg";
	static String FLICKR_API_KEY = "6076b3fca0851330568d880610c70267";
	static final Set<String> EXTRAS = new HashSet<String>(Arrays.asList(
		Extras.DESCRIPTION, Extras.OWNER_NAME
	));
	
	public FlickrViewNode(String sourceUrl) {
		super(sourceUrl);
	}
}
