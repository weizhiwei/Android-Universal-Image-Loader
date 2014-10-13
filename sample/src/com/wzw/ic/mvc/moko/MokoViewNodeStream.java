package com.wzw.ic.mvc.moko;

public class MokoViewNodeStream extends MokoViewNodeChannel {

	public MokoViewNodeStream() {
		super(URL_PREFIX + "/moko/post/%d.html");
	}
}
