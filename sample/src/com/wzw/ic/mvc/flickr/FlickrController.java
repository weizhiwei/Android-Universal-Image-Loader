package com.wzw.ic.mvc.flickr;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImageListActivity;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class FlickrController extends BaseController {

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
		
		// user
		if (itemUrl.contains("people")) {
			intent = new Intent(parentActivity, ImageListActivity.class);
			String peopleId = itemUrl.substring(itemUrl.lastIndexOf("/people/") + 8, itemUrl.length() - 1);
			newNode = new FlickrViewNodePeople(peopleId);
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
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, this);
		parentActivity.startActivity(intent);
	}

}
