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

public class LocateFriendListAdapter extends ArrayAdapter<UserInfo> {	
	
	private List<UserInfo> items;
	private int layoutResourceId;
	private Context context;
	
	public LocateFriendListAdapter(Context context, int layoutResourceId, List<UserInfo> items) {
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
		friendInfoHolder.friendInfo.setPosition(position);
		friendInfoHolder.locateUnlocateButton = (Button) row.findViewById(R.id.friendInfo_locate_id);
		friendInfoHolder.deleteItem = (Button) row.findViewById(R.id.friendInfo_remove_item);
		
		if (friendInfoHolder.friendInfo.getLocated() == 0)
			friendInfoHolder.locateUnlocateButton.setText("Locate");
		else 
			friendInfoHolder.locateUnlocateButton.setText("Unlocate");
		
		friendInfoHolder.friendInfo.setButton(friendInfoHolder.locateUnlocateButton);		
		
		friendInfoHolder.locateUnlocateButton.setTag(friendInfoHolder.friendInfo);		
		friendInfoHolder.deleteItem.setTag(friendInfoHolder.friendInfo);
		
		friendInfoHolder.deleteItem.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {                
            	UserInfo ui = (UserInfo) view.getTag();
            	
            	List<UserInfo> friendsList = MainActivity.getFriendsList();
                UserInfo.removeFriend(friendsList, ui.getUserName());
                                 
                notifyDataSetChanged();
            }
        });
		
		friendInfoHolder.friendName = (TextView)row.findViewById(R.id.friendInfo_name_id);			
		
		row.setTag(friendInfoHolder);			
		
		friendInfoHolder.friendName.setText(friendInfoHolder.friendInfo.getUserName());			
				
		return row;
	}
	
	public static class FriendInfoHolder {
		UserInfo friendInfo;
		TextView friendName;		
		Button locateUnlocateButton;
		Button deleteItem;
	}	
}
