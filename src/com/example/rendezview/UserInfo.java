package com.example.rendezview;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

public class UserInfo {
	private String userName;
	private LatLng userLatLng;
	private Long userId;
	private int located;
	
	public UserInfo(String name, LatLng latLng, Long id, int located) {
		this.userName = name;
		this.userLatLng = latLng;
		this.userId = id;
		this.located = located;
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
	
	public Long getUserId() {			
		return userId;
	}
	
	public LatLng getUserLocation() {
		return userLatLng;
	}
	
	public void setUserName(String name) {
		this.userName = name;
	}
	
	public void setUserId(Long id) {			
		this.userId = id;
	}
	
	public void setUserLocation(LatLng latLng) {
		this.userLatLng = latLng;
	}
	
	public static boolean containsUser(List<UserInfo> usersList, String userName) {
		boolean contains = false;
		
		for (int i = 0; i < usersList.size(); i++) {
			String name = usersList.get(i).getUserName();
			
			if (userName.equals(name)) {
				contains = true;
				break;
			}
		}
		
		return contains;
	}
	
	public static ArrayList<FriendInfo> getUsersNamesAsFriendInfo(List<UserInfo> usersList) {
		ArrayList<FriendInfo> names = new ArrayList<FriendInfo>();
		
		for (int i = 0; i < usersList.size(); i++) {
			names.add(new FriendInfo(usersList.get(i).getUserName(), usersList.get(i).getUserId(), 0));
		}
		
		return names;
	}
	
	public static ArrayList<String> getUsersNamesAsString(List<UserInfo> usersList) {
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i = 0; i < usersList.size(); i++) {
			names.add(usersList.get(i).getUserName());
		}
		
		return names;
	}
}
