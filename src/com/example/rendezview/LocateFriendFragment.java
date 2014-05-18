package com.example.rendezview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class LocateFriendFragment extends Fragment {
	
	Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View locateFriendView = inflater.inflate(R.layout.locate_friend_layout, container, false);			
		
		ListView friendsListItem = (ListView) locateFriendView.findViewById(R.id.listView1);

		ArrayList<FriendInfo> namesList = UserInfo.getUsersNamesAsFriendInfo(MainActivity.getFriendsList());				
		
		LocateFriendListAdapter adapter = new LocateFriendListAdapter(getActivity(), R.layout.locate_friend_list_item, namesList);
//		final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, namesList);
		friendsListItem.setAdapter(adapter);
		
		return locateFriendView;
	}	
}