package com.wzw.ic.mvc.root;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImageListActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
import com.wzw.ic.mvc.flickr.FlickrController;
import com.wzw.ic.mvc.flickr.FlickrViewNodeRoot;
import com.wzw.ic.mvc.fotopedia.FotoController;
import com.wzw.ic.mvc.fotopedia.FotoViewNodeRoot;
import com.wzw.ic.mvc.hearts.HeartsController;
import com.wzw.ic.mvc.hearts.HeartsViewNodeRoot;
import com.wzw.ic.mvc.moko.MokoController;
import com.wzw.ic.mvc.moko.MokoViewNodeRoot;
import com.wzw.ic.mvc.nationalgeographic.NGController;
import com.wzw.ic.mvc.nationalgeographic.NGViewNodeRoot;

public class RootController extends BaseController {

	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int index) {
		Intent intent = null;
		ViewNode newNode = null;
		BaseController newController = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(index);
		String itemUrl = viewItem.getNodeUrl();

		if (itemUrl.equals("hearts")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new HeartsViewNodeRoot();
			newController = new HeartsController();
		} else if (itemUrl.equals("moko")) {
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
		} else if (itemUrl.equals("nationalgeographic")) {
			intent = new Intent(parentActivity, ImageListActivity.class);
			newNode = new NGViewNodeRoot();
			newController = new NGController();
		}
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, newController);
		intent.putExtra(Extra.VIEW_ITEM, viewItem);
		
		parentActivity.startActivity(intent);
	}
}
