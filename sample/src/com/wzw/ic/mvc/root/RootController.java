package com.wzw.ic.mvc.root;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageListActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrController;
import com.wzw.ic.mvc.flickr.FlickrViewNodeRoot;
import com.wzw.ic.mvc.fotopedia.FotoController;
import com.wzw.ic.mvc.fotopedia.FotoViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoController;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;

public class RootController extends BaseController {

	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int index) {
		Intent intent = null;
		ViewNode newNode = null;
		BaseController newController = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(index);
		String itemUrl = viewItem.getNodeUrl();
		if (nodeUrl.equals("/")) {
			
			if (itemUrl.equals("moko")) {
				intent = new Intent(parentActivity, ImageGridActivity.class);
				newNode = new MokoViewNodeRoot();
				newController = new MokoController();
			} else if (itemUrl.equals("flickr")) {
				intent = new Intent(parentActivity, ImageListActivity.class);
				newNode = new FlickrViewNodeRoot();
				newController = new FlickrController();
			} else if (itemUrl.equals("fotopedia")) {
				intent = new Intent(parentActivity, ImageListActivity.class);
				newNode = new FotoViewNodeRoot();
				newController = new FotoController();
			}
		}
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, newController);
		parentActivity.startActivity(intent);
	}
}
