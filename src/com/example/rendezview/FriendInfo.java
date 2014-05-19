package com.example.rendezview;

import java.util.ArrayList;
import java.util.List;

import android.widget.Button;

public class FriendInfo {
	private String name;
	private Long id;
	// 1 if friend is located; 0 otherwise
	private int located;
	Button button = null;
	
	public  FriendInfo(String friendName, Long friendId, int friendLocated) {
		name = friendName;
		id = friendId;
		located = friendLocated;
	}
	
	public void setButton(Button b) {
		button = b;
	}	
	
	public void setName(String friendName) {
		name = friendName;
	}
	
	public void setFriendId(long friendId) {
		id = friendId;
	}
	
	public Long getFriendId(){
		return id;
	}
	
	public String getName() {
		return name;
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

}
