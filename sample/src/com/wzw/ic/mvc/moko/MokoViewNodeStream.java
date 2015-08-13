package com.wzw.ic.mvc.moko;

import com.wzw.ic.mvc.ViewNode;

public class MokoViewNodeStream extends MokoViewNodeChannel {

	public MokoViewNodeStream(ViewNode parent) {
		super(parent, URL_PREFIX + "/postChannel.action?curPage=%d");
	}
}
