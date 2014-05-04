package com.example.rendezview;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

public class UserInfo {
	private String userName;
	private LatLng userLatLng;
	private Long userId;
	
	public UserInfo(String name, LatLng latLng, Long id) {
		this.userName = name;
		this.userLatLng = latLng;
		this.userId = id;
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
	
	public static ArrayList<String> getUsersNames(List<UserInfo> usersList) {
		ArrayList<String> names = new ArrayList<String>();
		
		for (int i = 0; i < usersList.size(); i++) {
			names.add(usersList.get(i).getUserName());
		}
		
		return names;
	}
}
