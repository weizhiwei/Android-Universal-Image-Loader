package com.wzw.ic.mvc.nationalgeographic;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class NGController extends BaseController {
	
	public static String NG_ICON = "http://a266.phobos.apple.com/us/r1000/087/Purple2/v4/6d/20/4b/6d204b68-468b-22c8-a831-8d26535edbbb/mzl.keeyqfwh.png";
	
	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int position) {
		Intent intent = null;
		ViewNode newNode = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(position);
		String itemUrl = viewItem.getNodeUrl();
		
		if (itemUrl.equals("photoOfTheDay")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new NGViewNodePhotoOfTheDay();
		}
		if (node instanceof NGViewNodePhotoOfTheDay) {
			intent = new Intent(parentActivity, ImagePagerActivity.class);
			newNode = node;
			intent.putExtra(Extra.IMAGE_POSITION, position);
		}
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, this);
		intent.putExtra(Extra.VIEW_ITEM, viewItem);

		parentActivity.startActivity(intent);
	}
	
}
