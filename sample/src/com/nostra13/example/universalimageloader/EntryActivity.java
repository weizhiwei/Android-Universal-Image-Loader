package com.nostra13.example.universalimageloader;

public class EntryActivity extends ImageGridActivity {
	
	@Override
	public void onBackPressed() {
		imageLoader.stop();
		super.onBackPressed();
	}
}
