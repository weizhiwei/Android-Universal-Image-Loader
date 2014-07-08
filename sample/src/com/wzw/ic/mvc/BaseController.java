package com.wzw.ic.mvc;

import android.app.Activity;

public abstract class BaseController extends IcObject {
	public abstract void startItemView(
			Activity parentActivity, ViewNode node, int index);
}
