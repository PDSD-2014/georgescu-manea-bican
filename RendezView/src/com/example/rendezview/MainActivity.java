package com.example.rendezview;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private static final String TAB_KEY_INDEX = "tab_key";	
	
	public ActionBar actionbar;
	
	public static Fragment mAddFriendFragment;
	public static Fragment mLocateFriendFragment;
	public static Fragment mSetMeetingFragment;	
	public static Fragment mGoogleMapFragment;	
	
	public static List<UserInfo> mFriendsList = new ArrayList<UserInfo>();
	
	// Used for storing friends list set on addFriendFragment
	public static synchronized void addToFriendsList(UserInfo friendInfo) {
		mFriendsList.add(friendInfo);
	}
	
	public static synchronized List<UserInfo> getFriendsList() {
		return mFriendsList;
	}
		
	private void createActionBar() {
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
		
		/*getFragmentManager().beginTransaction().
		add(mAddFriendFragment, "add_friend_tag").
		add(mLocateFriendFragment, "locate_friend_tag").
		add(mSetMeetingFragment, "set_meeting_tag").
		add(mGoogleMapFragment, "google_map_tag").
		commit();*/

		// add the tabs to the action bar
		actionbar.addTab(mGoogleMapTab);
		actionbar.addTab(mAddFriendTab);
		actionbar.addTab(mLocateFriendTab);
		actionbar.addTab(mSetMeetingTab);			
	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);												
		
		createActionBar();
		
		// restore to navigation
		if (savedInstanceState != null) {
			/*Toast.makeText(getApplicationContext(),
					"tab is " + savedInstanceState.getInt(TAB_KEY_INDEX, 0),
					Toast.LENGTH_SHORT).show();*/

			actionbar.setSelectedNavigationItem(savedInstanceState.getInt(
					TAB_KEY_INDEX, 0));
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

	// onSaveInstanceState() is used to "remember" the current state when a
	// configuration change occurs such screen orientation change. This
	// is not meant for "long term persistence". We store the tab navigation

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//		Toast.makeText(
//				this,
//				"onSaveInstanceState: tab is"
//						+ getActionBar().getSelectedNavigationIndex(),
//				Toast.LENGTH_SHORT).show();
//		outState.putInt(TAB_KEY_INDEX, getActionBar()
//				.getSelectedNavigationIndex());

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