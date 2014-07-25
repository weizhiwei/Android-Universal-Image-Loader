package com.wzw.ic.mvc.flickr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;

import com.googlecode.flickrjandroid.photos.Extras;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImageListActivity;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class FlickrController extends BaseController {
	
	public static String FLICKR_ICON = "https://farm4.staticflickr.com/3741/buddyicons/66956608@N06_r.jpg";
	static String FLICKR_API_KEY = "6076b3fca0851330568d880610c70267";
	static final Set<String> EXTRAS = new HashSet<String>(Arrays.asList(
		Extras.DESCRIPTION
	));
	
	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int index) {
		Intent intent = null;
		ViewNode newNode = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(index);
		String itemUrl = viewItem.getNodeUrl();
		
		// interestingness
		if (itemUrl.equals("interestingness")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new FlickrViewNodeInterestingness();
			
		}
		if (node instanceof FlickrViewNodeInterestingness) {
			intent = new Intent(parentActivity, ImagePagerActivity.class);
			newNode = node;
			intent.putExtra(Extra.IMAGE_POSITION, index);
		}
		
		// commons
		if (itemUrl.equals("commons")) {
			intent = new Intent(parentActivity, ImageListActivity.class);
			newNode = new FlickrViewNodeCommons();
		}
		
		// people photosets
		if (itemUrl.contains("people")) {
			intent = new Intent(parentActivity, ImageListActivity.class);
			String peopleId = itemUrl.substring(itemUrl.lastIndexOf("/people/") + 8, itemUrl.length() - 1);
			newNode = new FlickrViewNodePeoplePhotosets(peopleId);
		}
		
		// photoset
		if (itemUrl.contains("sets")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			String photosetId = itemUrl.substring(itemUrl.lastIndexOf("/sets/") + 6, itemUrl.length() - 1);
			newNode = new FlickrViewNodePhotoset(photosetId);
		}
		if (node instanceof FlickrViewNodePhotoset) {
			intent = new Intent(parentActivity, ImagePagerActivity.class);
			newNode = node;
			intent.putExtra(Extra.IMAGE_POSITION, index);
		}
		
		// people galleries
		if (itemUrl.endsWith("/galleries/")) {
			intent = new Intent(parentActivity, ImageListActivity.class);
			String peopleId = itemUrl.substring(itemUrl.lastIndexOf("/photos/") + 8, itemUrl.lastIndexOf("/galleries/"));
			newNode = new FlickrViewNodePeopleGalleries(peopleId);
		}
		
		// gallery
		if (itemUrl.contains("/galleries/") && !itemUrl.endsWith("/galleries/")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			String galleryId = itemUrl.substring(itemUrl.lastIndexOf("/galleries/") + 11, itemUrl.length() - 1);
			newNode = new FlickrViewNodeGallery(galleryId);
		}
		if (node instanceof FlickrViewNodeGallery) {
			intent = new Intent(parentActivity, ImagePagerActivity.class);
			newNode = node;
			intent.putExtra(Extra.IMAGE_POSITION, index);
		}
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, this);
		intent.putExtra(Extra.VIEW_ITEM, viewItem);

		parentActivity.startActivity(intent);
	}

}
