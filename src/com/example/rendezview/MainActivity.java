package com.example.rendezview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends Activity {

	private static final String TAB_KEY_INDEX = "tab_key";	
	
	public ActionBar actionbar;
	
	public static Fragment mAddFriendFragment;
	public static Fragment mLocateFriendFragment;
	public static Fragment mSetMeetingFragment;	
	public static Fragment mGoogleMapFragment;	
	private static Context sContext;
	
	public static UserInfo userInfo = new UserInfo();
	
	public static List<UserInfo> mFriendsList = new ArrayList<UserInfo>();
	
	public static DatabaseHandler db;
	
	// Used for storing friends list set on addFriendFragment
	public static synchronized void addToFriendsList(UserInfo friendInfo) {		
		for (int i = 0; i < mFriendsList.size(); i++) {
			if (mFriendsList.get(i).getUserName().equals(friendInfo.getUserName())) {
				mFriendsList.remove(i);
				break;
			}
		}
		mFriendsList.add(friendInfo);
	}
	
	public static synchronized void setFriendsList(List<UserInfo> friendList) {
		mFriendsList = friendList;
	}
	
	public static synchronized List<UserInfo> getFriendsList() {
		return mFriendsList;
	}
	
	public static synchronized void setUserInfo(UserInfo user) {
		userInfo = user; 
	}
	
	public static synchronized UserInfo getUserInfo() {
		return userInfo; 
	}
	
	public static synchronized Context getContext() {
		return sContext;
	}
	
	private boolean createActionBar() {		
		
		actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		ActionBar.Tab mAddFriendTab = actionbar.newTab().setText(getString(R.string.add_friend_tab_name));
		ActionBar.Tab mLocateFriendTab = actionbar.newTab().setText(getString(R.string.locate_friend_tab_name));
		ActionBar.Tab mSetMeetingTab = actionbar.newTab().setText(getString(R.string.set_meeting_tab_name));
		ActionBar.Tab mGoogleMapTab = actionbar.newTab().setText(getString(R.string.google_map_tab_name));
		
		mAddFriendFragment = new AddFriendFragment();
		mLocateFriendFragment = new LocateFriendFragment();
		mSetMeetingFragment = new SetMeetingFragment();		
		mGoogleMapFragment = new GoogleMapFragment();
		
		mAddFriendFragment.setRetainInstance(true);
		mLocateFriendFragment.setRetainInstance(true);
		mSetMeetingFragment.setRetainInstance(true);
		mGoogleMapFragment.setRetainInstance(true);
		
		mAddFriendTab.setTabListener(new MyTabsListener(mAddFriendFragment,
				getApplicationContext()));
		mLocateFriendTab.setTabListener(new MyTabsListener(mLocateFriendFragment,
				getApplicationContext()));
		mSetMeetingTab.setTabListener(new MyTabsListener(mSetMeetingFragment,
				getApplicationContext()));
		mGoogleMapTab.setTabListener(new MyTabsListener(mGoogleMapFragment,
				getApplicationContext()));			

		// Add the tabs to the action bar.
		actionbar.addTab(mGoogleMapTab);
		actionbar.addTab(mAddFriendTab);
		actionbar.addTab(mLocateFriendTab);
		actionbar.addTab(mSetMeetingTab);			
		
		return true;
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);				
		
		StartFragmentsTask startFragmentsTask = new StartFragmentsTask();	    		
		startFragmentsTask.execute(savedInstanceState);							
		
		/*SharedPreferences initialPref = getSharedPreferences("INITIAL", 0);
		boolean firstTimer = initialPref.getBoolean("INITIAL", false);
		
		if (!firstTimer) {
			db = new DatabaseHandler(this);
			
			UserInfo ui = new UserInfo("Test Name", new LatLng(44,45), 12, 0);
			db.addFriend(ui);
			
			SharedPreferences.Editor editorPref = initialPref.edit();
	        editorPref.putBoolean("INITIAL", true);
	        editorPref.commit();
		} else {
			db = new DatabaseHandler(this);
			List<UserInfo> friendssss = db.getAllFriends();
			if (friendssss.size() > 0)
				Toast.makeText(this, friendssss.get(0).getUserName(), Toast.LENGTH_LONG).show();
		}*/
		
		// Get friends from database
		DatabaseHandler db = new DatabaseHandler(this);
		List<UserInfo> friendList = db.getAllFriends();
		this.setFriendsList(friendList);		
		
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(loginIntent, 0);
        sContext = getApplicationContext();
	}	

	protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {		
	    if (requestCode == 0) {	    	
	        if (resultCode == 0) {	        	
//	        	Toast.makeText(this, "Welcome " + userInfo.getUserName(), Toast.LENGTH_SHORT).show();
	        }
	    }
	}

	public void locateUnlocateButtonHandler(View v) {
		UserInfo friendInfo = (UserInfo)v.getTag();
		
		if (friendInfo.locateFriendButton != null) {
			if (friendInfo.getLocated() == 0) {
				friendInfo.locate();
				friendInfo.locateFriendButton.setText("Unlocate");				
			} else {
				friendInfo.unlocate();
				friendInfo.locateFriendButton.setText("Locate");				
			}
		}
	}		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem_share:
			Toast.makeText(this, "Thank you for sharing this app!",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_feedback:
			Toast.makeText(this, "Thank you for your feedback!",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_about:
			Toast.makeText(this, "Application name: RendezView\nAtuhors: Bianca Georgescu, Valentina Manea, Daniel Bican\nVersion:1.0",
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_quit:			
			finish(); // close the activity
			return true;		
		}
		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
//		Toast.makeText(this, "MainActivity -> onStop", Toast.LENGTH_LONG).show();		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		Toast.makeText(this, "MainActivity -> onDestroy", Toast.LENGTH_LONG).show();
		// Save friends in database
		DatabaseHandler db = new DatabaseHandler(this);
		List<UserInfo> friendsList = this.getFriendsList();
		for (int i = 0; i < friendsList.size(); i++) {
			db.addFriend(friendsList.get(i));
		}
		
		// Logout user from server
		TryToLogoutUser tryToLogoutUser = new TryToLogoutUser();
		tryToLogoutUser.execute(userInfo.getUserId());
	}
	
	protected class StartFragmentsTask extends AsyncTask<Bundle, Void, Boolean> {
	    @Override
	    protected Boolean doInBackground(Bundle... savedInstanceState) {
	    	Boolean flag_to_return = createActionBar();				    		    	    			
	    	
	    	// restore to navigation
	    	if (savedInstanceState[0] != null) {			
	    		Toast.makeText(getApplicationContext(),
					"tab is " + savedInstanceState[0].getInt(TAB_KEY_INDEX, 0),
						Toast.LENGTH_SHORT).show();
		
				actionbar.setSelectedNavigationItem(savedInstanceState[0].getInt(TAB_KEY_INDEX, 0));
			}
			
			return flag_to_return;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			/*Toast.makeText(getApplicationContext(),
					"Fragments loaded!",
						Toast.LENGTH_SHORT).show();*/
		}
	}
	
	// Class that does the background work
		private class TryToLogoutUser extends AsyncTask<Integer, Void, String> {

			private String TAG = "MainActivity.TryToLogoutUser";				       
	                 	        
	        @Override
	        protected String doInBackground(Integer... id) {
	        	Log.d(TAG, "Se executa doInBackground!");
	        	            	        	
		    	int userId = id[0].intValue();
		    	String messageForServer = 3 + " " + userId + "\n";
		    	String serverResult = null;
		    		    	
		    	try {	    		
		    		Socket clientSocket = new Socket(InetAddress.getByName("projects.rosedu.org"), 9000);
					
		    		BufferedWriter messageSender = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
		    		BufferedReader responseReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
					
		    		messageSender.write(messageForServer);
		    		messageSender.flush();
		    		
					serverResult = responseReader.readLine();
					
					clientSocket.close();
				} catch (UnknownHostException e) {
					Log.e(TAG, "Eroare la contactare server! " + e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					Log.e(TAG, "Eroare la conectare cu socketul! " + e.getMessage());
					e.printStackTrace();
				}
	            
	        	return serverResult;
	        }
	        
	        @Override
	        protected void onPostExecute(String result) {
	            super.onPostExecute(result);                       
	            Log.d(TAG, "Se executa onPostExecute!");
	            
	            if (result == null) {
	            	Log.d(TAG, "Utilizatorul nu a fost delogat din sistem!");
	            	Toast.makeText(getApplicationContext(), "Cannot retrieve the users address from server", Toast.LENGTH_LONG).show();
	            } 
	        }
		} 
}

// TabListenr class for managing user interaction with the ActionBar tabs. The
// application context is passed in pass it in constructor, needed for the
// toast.
class MyTabsListener implements ActionBar.TabListener {
	public Fragment fragment;
	public Context context;

	public MyTabsListener(Fragment fragment, Context context) {
		this.fragment = fragment;
		this.context = context;

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		//Toast.makeText(context, "Reselected!", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		//Toast.makeText(context, "Selected!", Toast.LENGTH_SHORT).show();
		ft.replace(R.id.fragment_container, fragment);		
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		//Toast.makeText(context, "Unselected!", Toast.LENGTH_SHORT).show();
		ft.remove(fragment);
	}
}