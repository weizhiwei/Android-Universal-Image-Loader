package com.wzw.ic.mvc.moko;

import android.app.Activity;
import android.content.Intent;

import com.nostra13.example.universalimageloader.Constants.Extra;
import com.nostra13.example.universalimageloader.ImageGridActivity;
import com.nostra13.example.universalimageloader.ImagePagerActivity;
import com.wzw.ic.mvc.BaseController;
import com.wzw.ic.mvc.ViewItem;
import com.wzw.ic.mvc.ViewNode;

public class MokoController extends BaseController {
	
	public static String MOKO_ICON = "http://www.vitbbs.cn/uploads/allimg/c101125/12ZEK63410-14106.gif";
	
	@Override
	public void startItemView(Activity parentActivity, ViewNode node, int position) {
		Intent intent = null;
		ViewNode newNode = null;
		
		String nodeUrl = node.getSourceUrl();
		ViewItem viewItem = node.getViewItems().get(position);
		String itemUrl = viewItem.getNodeUrl();
		if (nodeUrl.equals("http://www.moko.cc/")) {
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
		
		intent.putExtra(Extra.MODEL, newNode);
		intent.putExtra(Extra.CONTROLLER, this);
		intent.putExtra(Extra.VIEW_ITEM, viewItem);

		parentActivity.startActivity(intent);
	}
	
}
