package com.example.rendezview;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class AddFriendFragment extends Fragment{

	private WeakReference<MyAsyncTask> asyncTaskWeakRef;
	
	private View addFriendView;
	
	private AutoCompleteTextView mACTextView;
	private ArrayAdapter mAdapter;
	
	private Button mAddFriendButton;
	private Button mLocateFriendButton;
	private Button mFriendsListButton;
	
	// TODO - this list will be populated by the async thred in background from server responses
	private List<UserInfo> usersList = new ArrayList<UserInfo>();
	private List<UserInfo> friendsList = new ArrayList<UserInfo>();
	
	private void populateUsersList() {
		usersList.clear();
		usersList.add(new UserInfo("Bican Daniel", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Badarcea Mihai", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Bivol Calin", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Bolovan Dinu", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Georgescu Bianca", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Bican Andreea", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Manea Valentina", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Neagu Djuvara", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Tiberiu Berariu", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Mihai Stan", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Gino Iorgulescu", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Cristiana Enescu", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Oliver Twist", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Mama lui Stefan cel Mare", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Dimitrie Cantemir", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Dean Carnazes", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Veronica Micle", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Mihai Eminsecu", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Stanislav Milkovici", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Ioan Cuza", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Maria Plopeanu", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Goe Patitul", new LatLng(0,0), (long) 0, 0));
		usersList.add(new UserInfo("Elena Udrea", new LatLng(0,0), (long) 0, 0));
	}
	
	private void setAdapterForACTV() {
		//populateUsersList();
		mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, UserInfo.getUsersNamesAsString(usersList));
		mACTextView.setAdapter(mAdapter);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);					
		setRetainInstance(true);		
		startNewAsyncTask();			
	}
	 
	private void startNewAsyncTask() {
		MyAsyncTask asyncTask = new MyAsyncTask(this);
		this.asyncTaskWeakRef = new WeakReference<MyAsyncTask>(asyncTask);
		asyncTask.execute();		
	}
	
	private boolean isAsyncTaskPendingOrRunning() {
	    return this.asyncTaskWeakRef != null &&
			    this.asyncTaskWeakRef.get() != null &&
			    !this.asyncTaskWeakRef.get().getStatus().equals(Status.FINISHED);
	}	
	
	public void hideKeyboard(View view) {
		 InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		 in.hideSoftInputFromWindow(mACTextView.getWindowToken(), 0);
	}	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		addFriendView = inflater.inflate(R.layout.add_friend_layout, container, false);
		addFriendView.setBackgroundColor(Color.BLACK);
		mACTextView = (AutoCompleteTextView) addFriendView.findViewById(R.id.autoCompleteTextView1);		
		mACTextView.setThreshold(1);
		
		// Remove keyboard after user selects an element		
		mACTextView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				hideKeyboard(arg1);
			}
		});
		
		// Disable locate and friends list when searching a user in database
		mACTextView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mLocateFriendButton.setEnabled(false);
		        mFriendsListButton.setEnabled(false);
				return false;
			}
		});			
		
		setAdapterForACTV();
		
		mAddFriendButton = (Button) addFriendView.findViewById(R.id.button1);
		mLocateFriendButton = (Button) addFriendView.findViewById(R.id.button2);
		mFriendsListButton = (Button) addFriendView.findViewById(R.id.button3);
		
		// Check what user has typed and do something depending on the text he entered
		mAddFriendButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Editable friendName = mACTextView.getEditableText();
		    	
		    	if (!UserInfo.containsUser(usersList, friendName.toString())) {
		    		Toast.makeText(getActivity(), " Please enter complete name!", Toast.LENGTH_SHORT).show();
		    		mLocateFriendButton.setEnabled(false);
			        mFriendsListButton.setEnabled(false);
		    	} else {
			    	if (UserInfo.containsUser(friendsList, friendName.toString())) {
			    		Toast.makeText(getActivity(), friendName.toString() + " is already your friend!", Toast.LENGTH_SHORT).show();
			    		mLocateFriendButton.setEnabled(true);
				        mFriendsListButton.setEnabled(true);
			    	} else {			    					    	
			    		friendsList.add(new UserInfo(friendName.toString(), new LatLng(0,0), (long) 0, 0));			    		
			    		// Add friend to the friends list from MainActivity in order to make it
			    		// accesible for any fragment
			    		MainActivity.addToFriendsList(new UserInfo(friendName.toString(), new LatLng(0,0), (long) 0, 0));
			    		for (int i = 0; i < friendsList.size(); i++)
			    			Toast.makeText(getActivity(), friendsList.get(i).getUserName(), Toast.LENGTH_SHORT).show();
			    			
			    		mLocateFriendButton.setEnabled(true);
				        mFriendsListButton.setEnabled(true);
			    	}
		    	}		    			      
		    }
		});
		
		// If "Friends List" button is pressed then change to find friends tab
		mFriendsListButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				getActivity().getActionBar().setSelectedNavigationItem(2);
			}
		});
		
		//If "Locate Friend" button is pressed then change to map tab
		mLocateFriendButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				getActivity().getActionBar().setSelectedNavigationItem(0);
			}
		});
		
		return addFriendView;
    }
	
	@Override
    public void onResume() {
        super.onResume();    
    }

    @Override
    public void onPause() {
        super.onPause();        
    }

    @Override
    public void onDestroyView() {
    	hideKeyboard(addFriendView);
    	super.onDestroyView();    	        
    }
    
    @Override
    public void onDestroy() {
    	hideKeyboard(addFriendView);
    	super.onDestroy();    	        
    }
	
    // Class that does the background work
	private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<AddFriendFragment> fragmentWeakRef;
                 
        private MyAsyncTask (AddFriendFragment fragment) {
            this.fragmentWeakRef = new WeakReference<AddFriendFragment>(fragment);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //TODO: usersList has to be updated here from the server
            populateUsersList();
        	return null;
        }

        protected void onProgressUpdate(Integer progress) {            
        	Toast.makeText(getActivity(), Integer.toString(progress), Toast.LENGTH_SHORT).show();
        }
        
        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            if (this.fragmentWeakRef.get() != null) {
            	//TODO: treat the result            
            	if (!mLocateFriendButton.isEnabled()) {
            		setAdapterForACTV();
            		Toast.makeText(getActivity(), "Finished executing!", Toast.LENGTH_SHORT).show();
            	} else {
            		Toast.makeText(getActivity(), "Waiting!", Toast.LENGTH_SHORT).show();
            	}
            }
        }
        
        
    }	
}
