package com.wzw.ic.controller.moko;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImageListActivity;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.wzw.ic.controller.BaseController;
import com.wzw.ic.model.ViewItem;
import com.wzw.ic.model.ViewNode;
import com.wzw.ic.model.moko.MokoViewNodeChannel;
import com.wzw.ic.model.moko.MokoViewNodePost;
import com.wzw.ic.model.moko.MokoViewNodeRoot;

public class MokoController extends BaseController {
	
	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int position) {
		Intent intent = null;
		ViewNode newNode = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(position);
		String itemUrl = viewItem.getNodeUrl();
		if (nodeUrl.equals("/")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new MokoViewNodeRoot(itemUrl);
			
		} else if (nodeUrl.equals("http://www.moko.cc/")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new MokoViewNodeChannel(itemUrl);
			
		} else if (nodeUrl.contains("channels")) {
			intent = new Intent(parentActivity, ImageGridActivity.class);
			newNode = new MokoViewNodePost(itemUrl);
			
		} else if (nodeUrl.contains("post")) {
			intent = new Intent(parentActivity, ImagePagerActivity.class);
			newNode = node;
			intent.putExtra(Extra.IMAGE_POSITION, position);
		}
		
		intent.putExtra(Extra.IMAGES, newNode);
		intent.putExtra(Extra.CONTROLLER, this);
		parentActivity.startActivity(intent);
	}
	
}
