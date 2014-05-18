package com.example.rendezview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LocateFriendListAdapter extends ArrayAdapter<FriendInfo> {	
	
	private List<FriendInfo> items;
	private int layoutResourceId;
	private Context context;
	
	public LocateFriendListAdapter(Context context, int layoutResourceId, List<FriendInfo> items) {
		super(context, layoutResourceId, items);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.items = items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		FriendInfoHolder friendInfoHolder = null;
		
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		row = inflater.inflate(layoutResourceId, parent, false);
		
		friendInfoHolder = new FriendInfoHolder();
		friendInfoHolder.friendInfo = items.get(position);
		friendInfoHolder.locateUnlocateButton = (Button) row.findViewById(R.id.friendInfo_locate_id);
		friendInfoHolder.friendInfo.setButton(friendInfoHolder.locateUnlocateButton);
		friendInfoHolder.locateUnlocateButton.setTag(friendInfoHolder.friendInfo);
		
		friendInfoHolder.friendName = (TextView)row.findViewById(R.id.friendInfo_name_id);			
		
		row.setTag(friendInfoHolder);			
		
		friendInfoHolder.friendName.setText(friendInfoHolder.friendInfo.getName());			
				
		return row;
	}
	
	public static class FriendInfoHolder {
		FriendInfo friendInfo;
		TextView friendName;		
		Button locateUnlocateButton;
	}	
}
