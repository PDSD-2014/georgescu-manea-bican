package com.example.rendezview;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAB_KEY_INDEX = "tab_key";	
	
	public ActionBar actionbar;
	
	public static Fragment mAddFriendFragment;
	public static Fragment mLocateFriendFragment;
	public static Fragment mSetMeetingFragment;	
	public static Fragment mGoogleMapFragment;	
	
	public static UserInfo userInfo = new UserInfo();
	
	public static List<UserInfo> mFriendsList = new ArrayList<UserInfo>();
	
	// Used for storing friends list set on addFriendFragment
	public static synchronized void addToFriendsList(UserInfo friendInfo) {
		mFriendsList.add(friendInfo);
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

		// add the tabs to the action bar
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
		
		Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(loginIntent, 0);
	}	

	protected void onActivityResult(int requestCode, int resultCode,
             Intent data) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	        	Toast.makeText(this, "Welcome " + userInfo.getUserName(), Toast.LENGTH_SHORT).show();
	        }
	    }
	}

	public void locateUnlocateButtonHandler(View v) {
		UserInfo friendInfo = (UserInfo)v.getTag();
		
//		if (friendInfo.button != null) {
//			if (friendInfo.getLocated() == 0) {
//				friendInfo.button.setText("Unlocate");
//				friendInfo.locate();
//			} else {
//				friendInfo.button.setText("Locate");
//				friendInfo.unlocate();
//			}
//		} else {
//			Toast.makeText(this, "Welcome " + userInfo.getUserName(), Toast.LENGTH_SHORT).show();
//		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*switch (item.getItemId()) {
		case R.id.menuitem_search:
			Toast.makeText(this, getString(R.string.ui_menu_search),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_send:
			Toast.makeText(this, getString(R.string.ui_menu_send),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_add:
			Toast.makeText(this, getString(R.string.ui_menu_add),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_share:
			Toast.makeText(this, getString(R.string.ui_menu_share),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_feedback:
			Toast.makeText(this, getString(R.string.ui_menu_feedback),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_about:
			Toast.makeText(this, getString(R.string.ui_menu_about),
					Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menuitem_quit:
			Toast.makeText(this, getString(R.string.ui_menu_quit),
					Toast.LENGTH_SHORT).show();
			finish(); // close the activity
			return true;
		}
*/		return false;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
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