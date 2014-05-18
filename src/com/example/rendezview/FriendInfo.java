package com.example.rendezview;

import android.widget.Button;

public class FriendInfo {
	String name;
	// 1 if friend is located; 0 otherwise
	int located;
	Button button = null;
	
	public  FriendInfo(String friendName, int friendLocated) {
		name = friendName;
		located = friendLocated;
	}
	
	public void setButton(Button b) {
		button = b;
	}	
	
	public void setName(String friendName) {
		name = friendName;
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
