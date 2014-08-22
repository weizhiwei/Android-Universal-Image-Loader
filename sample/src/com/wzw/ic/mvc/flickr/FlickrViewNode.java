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
		Extras.DESCRIPTION
	));
	
	public FlickrViewNode(String sourceUrl) {
		super(sourceUrl);
	}

	@Override
	public boolean supportReloading() {
		return false;
	}

	@Override
	public void reload() {
		// throw new MethodNotSupportedException("RootViewNode does not support reloading");
	}

	@Override
	public boolean supportPaging() {
		return false;
	}

	@Override
	public void loadOneMorePage() {
		// throw new MethodNotSupportedException("RootViewNode does not support paging");
	}
}
