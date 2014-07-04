package com.wzw.ic.controller;

import java.io.Serializable;

import android.app.Activity;

import com.wzw.ic.model.ViewNode;

public abstract class BaseController implements Serializable {
	public abstract void startItemView(
			Activity parentActivity, ViewNode node, int index);
}
