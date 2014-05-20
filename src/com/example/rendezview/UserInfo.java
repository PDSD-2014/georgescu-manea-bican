package com.example.rendezview;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class UserInfo {
	private String userName = null;
	private LatLng userLatLng = null;
	private int userId = -1;
	private int located = 0;
	public Button locateFriendButton = null;
	private int position;
	
	public UserInfo(String name, LatLng latLng, int id, int located) {
		this.userName = name;
		this.userLatLng = latLng;
		this.userId = id;
		this.located = located;
	}
	
	public void setPosition(int pos) {
		position = pos;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setButton(Button b) {
		locateFriendButton = b;
	}	
	
	public UserInfo() {
		
	}

	public void locate() {
		located = 1;
	}
	
	public void unlocate() {
		located = 0;
	}
	
	public int getLocated() {		
		return located;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public int getUserId() {			
		return userId;
	}
	
	public LatLng getUserLocation() {
		return userLatLng;
	}
	
	public void setUserName(String name) {
		this.userName = name;
	}
	
	public void setUserId(int id) {			
		this.userId = id;
	}
	
	public void setUserLocation(LatLng latLng) {
		this.userLatLng = latLng;
	}
	
	public static void removeFriend(List<UserInfo> usersList, String userName) {
		for (int i = 0; i < usersList.size(); i++) {
			String name = usersList.get(i).getUserName();
			
			if (userName.equals(name)) {
				usersList.remove(i);
				break;
			}
		}
	}
	
	public static UserInfo containsUser(List<UserInfo> usersList, String userName) {
		UserInfo ui = null;
		
		for (int i = 0; i < usersList.size(); i++) {
			String name = usersList.get(i).getUserName();
			
			if (userName.equals(name)) {
				ui = usersList.get(i);
				break;
			}
		}
		
		return ui;
	}
	
	public static UserInfo containsUser(List<UserInfo> usersList, int userId) {
		UserInfo ui = null;
		
		for (int i = 0; i < usersList.size(); i++) {
			int id = usersList.get(i).getUserId();
			
			if (id == userId) {
				ui = usersList.get(i);
				break;
			}
		}
		
		return ui;
	}
	
	public static ArrayList<String> getUsersNamesAsString(List<UserInfo> usersList) {
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i = 0; i < usersList.size(); i++) {
			names.add(usersList.get(i).getUserName());
		}
		
		return names;
	}
}
