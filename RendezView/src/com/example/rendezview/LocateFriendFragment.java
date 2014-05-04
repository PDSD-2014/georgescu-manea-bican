package com.example.rendezview;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LocateFriendFragment extends Fragment{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View locateFriendView = inflater.inflate(R.layout.locate_friend_layout, container, false);
		
		ListView friendsListItem = (ListView) locateFriendView.findViewById(R.id.listView1);
		
		ArrayList<String> namesList = UserInfo.getUsersNames(MainActivity.getFriendsList());
		
		final ArrayAdapter adapter = new ArrayAdapter(getActivity(),
		        android.R.layout.simple_list_item_1, namesList);
		friendsListItem.setAdapter(adapter);
		
		return locateFriendView;
	}	
}