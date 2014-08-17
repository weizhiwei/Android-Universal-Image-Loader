package com.wzw.ic.mvc.hearts;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class HeartsController extends BaseController {
	
	public static String HEARTS_ICON = "https://farm6.staticflickr.com/5562/14944448425_f371d552b8_o.png";
	
	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int position) {
		Intent intent = null;
		ViewNode newNode = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(position);
		String itemUrl = viewItem.getNodeUrl();

		if (node instanceof HeartsViewNodeRoot) {
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
