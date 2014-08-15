package com.nostra13.example.universalimageloader;

public class EntryActivity extends ImageListActivity {
	
	@Override
	public void onBackPressed() {
		imageLoader.stop();
//		IcDatabase.getInstance().close();
		super.onBackPressed();
	}
}
