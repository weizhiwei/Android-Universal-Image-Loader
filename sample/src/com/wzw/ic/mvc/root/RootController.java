package com.wzw.ic.mvc.root;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.Constants.Extra;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;
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
			
			if (itemUrl.contains("moko")) {
				intent = new Intent(parentActivity, ImageGridActivity.class);
				newNode = new MokoViewNodeRoot(itemUrl);
				newController = new MokoController();
			}
		}
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, newController);
		parentActivity.startActivity(intent);
	}
}
