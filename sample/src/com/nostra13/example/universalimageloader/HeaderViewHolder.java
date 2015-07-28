package com.nostra13.example.universalimageloader;

import com.nostra13.example.universalimageloader.R;

import android.view.View;

public class HeaderViewHolder {
	public View header;
	public View divider;
	public View footer;
	
	public HeaderViewHolder(View convertView) {
		header = convertView.findViewById(R.id.header);
        divider = convertView.findViewById(R.id.divider);
        footer = convertView.findViewById(R.id.footer);
	}
}
