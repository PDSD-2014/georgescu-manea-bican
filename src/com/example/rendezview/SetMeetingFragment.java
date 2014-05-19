package com.example.rendezview;


//TODO   -  mechanism to always be listening for meeting requests

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

public class SetMeetingFragment extends Fragment {
	
	private WeakReference<MyAsyncTask> asyncTaskWeakRef;
	
	private View setMeetingView;
	
	private AutoCompleteTextView mACTextView;
	private ArrayAdapter mAdapter;
	
	private Button mAddAttendeeButton;
	private Button mSendInvitationButton;
	private Button mSeeMeetingButton;
	private Button mCancelMeetingButton;
	int mCurCheckPosition = 0;
	
	// TODO - this list will be populated by the async thread in background from server responses
	private List<UserInfo> friendsList = new ArrayList<UserInfo>();
	private List<String> meetingList = new ArrayList<String>();
	// meeting attendees' ids to be sent to server
	private List<Integer> meetingListIds = new ArrayList<Integer>();
	
	/*private void populateFriendList() {
		friendList.clear();
		usersList.add(new UserInfo("Bican Daniel", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Badarcea Mihai", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Bivol Calin", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Bolovan Dinu", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Georgescu Bianca", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Bican Andreea", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Manea Valentina", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Neagu Djuvara", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Tiberiu Berariu", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Mihai Stan", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Gino Iorgulescu", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Cristiana Enescu", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Oliver Twist", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Mama lui Stefan cel Mare", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Dimitrie Cantemir", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Dean Carnazes", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Veronica Micle", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Mihai Eminsecu", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Stanislav Milkovici", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Ioan Cuza", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Maria Plopeanu", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Goe Patitul", new LatLng(0,0), (long) 0));
		usersList.add(new UserInfo("Elena Udrea", new LatLng(0,0), (long) 0));
	}*/
	
	
	@SuppressWarnings("rawtypes")
	private void setAdapterForACTV() {
		//populateUsersList();
		mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, UserInfo.getUsersNamesAsString(MainActivity.getFriendsList()));
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
		friendsList = MainActivity.getFriendsList();
		setMeetingView = inflater.inflate(R.layout.set_meeting_layout, container, false);
		setMeetingView.setBackgroundColor(Color.BLACK);
		mACTextView = (AutoCompleteTextView) setMeetingView.findViewById(R.id.autoCompleteTextView1);		
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
			
			//TODO
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});			
		
		setAdapterForACTV();
		
		mAddAttendeeButton = (Button) setMeetingView.findViewById(R.id.button1);
		mSendInvitationButton = (Button) setMeetingView.findViewById(R.id.button2);
		mSeeMeetingButton = (Button) setMeetingView.findViewById(R.id.button3);
		mCancelMeetingButton = (Button) setMeetingView.findViewById(R.id.button4);
		
		// Check what user has typed and do something depending on the text he entered
		mAddAttendeeButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	Editable friendName = mACTextView.getEditableText();
		    	int friendId = -1;
		    	
		    	for (UserInfo friend : friendsList){
		    		if (friend.getUserName().equals(friendName.toString())){
		    			friendId = friend.getUserId();
		    			break;
		    		} 
		    	}
		    	
		    	if (UserInfo.containsUser(friendsList, friendName.toString()) != null) {
		    		Toast.makeText(getActivity(), " Please enter complete name!", Toast.LENGTH_SHORT).show();
		    	} else {
			    	if (meetingList.contains(friendName.toString())) {
			    		Toast.makeText(getActivity(), friendName.toString() + " is already marked to join the meeting!", Toast.LENGTH_SHORT).show();
			    		//mLocateFriendButton.setEnabled(true);
				        //mFriendsListButton.setEnabled(true);
			    	} else {	
			    		// TODO - Alternative for getting id: get id from local sql
			    		meetingList.add(friendName.toString());
			    		meetingListIds.add(friendId);
			    		mACTextView.setText("");
			    		
			    		
			    		String allAttendees = "";
			    		for (int i = 0; i < meetingList.size(); i++){
			    			if (i == 0)
			    				allAttendees = allAttendees + meetingList.get(i);
			    			else 
			    				allAttendees = allAttendees +", " + meetingList.get(i);
			    		}
			    		Toast.makeText(getActivity(), allAttendees, Toast.LENGTH_SHORT).show();
			    		mSendInvitationButton.setEnabled(true);
			    	}
		    	}	
		    	
		    }
		});
		
		/**
		 * If SendInvitation button is pressed send request to server for invitations to attendees
		 * and enbale See and Cancel Meeting Buttons
		 */
		mSendInvitationButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				//TODO  Send Invitation through server
				//TODO Wait for acceptance
				//TODO progress bar till all accept
				
				//you cannot add friends to teh meeting anymore, but you can see or cancel the meeting
				mAddAttendeeButton.setEnabled(false);
				mSeeMeetingButton.setEnabled(true);
				mCancelMeetingButton.setEnabled(true);
				
			}
		});
		
		/**
		 * When SeeMeeting is pressed move to Map Fragment to see how meeting goes
		 */
		mSeeMeetingButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {					
				getActivity().getActionBar().setSelectedNavigationItem(0);
				
				//TODO  We should have a place to put information like
				// estimated arriving time for each attendee
				
				
			}
		});
		
		mCancelMeetingButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {				
				//TODO  Send Invitation
				
				//Get buttons to their initial state
				meetingList.clear();
				meetingListIds.clear();
				mAddAttendeeButton.setEnabled(true);
				mSendInvitationButton.setEnabled(false);
				mSeeMeetingButton.setEnabled(false);
				mCancelMeetingButton.setEnabled(false);
				
			}
		});
		
		return setMeetingView;
    }
	
	@Override
    public void onResume() {
        super.onResume();    
    }

    @Override
    public void onPause() {
        super.onPause();        
    }
    
    
    //TODO - save SetMeeting fragment's state to restore after moving to MapFragment
    // currently not saving buttons enable states
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
    }    

    @Override
    public void onDestroyView() {
    	hideKeyboard(setMeetingView);
    	super.onDestroyView();    	        
    }
    
    @Override
    public void onDestroy() {
    	hideKeyboard(setMeetingView);
    	super.onDestroy();    	        
    }
	
    // Class that does the background work
	private class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<SetMeetingFragment> fragmentWeakRef;

        private MyAsyncTask(SetMeetingFragment fragment) {
        	this.fragmentWeakRef = new WeakReference<SetMeetingFragment>(fragment);
		}

		@Override
        protected Void doInBackground(Void... params) {
            //TODO: usersList has to be updated here from the server
            //populateUsersList();
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
            	/*if (!mLocateFriendButton.isEnabled()) {
            		setAdapterForACTV();
            		Toast.makeText(getActivity(), "Finished executing!", Toast.LENGTH_SHORT).show();
            	} else {
            		Toast.makeText(getActivity(), "Waiting!", Toast.LENGTH_SHORT).show();
            	}*/
            }
        }
        
        
    }
}
