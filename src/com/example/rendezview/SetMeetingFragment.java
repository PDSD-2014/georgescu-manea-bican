package com.example.rendezview;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class SetMeetingFragment extends Fragment {
	
	private WeakReference<MyAsyncTask> asyncTaskWeakRef;
	
	private View setMeetingView;
	
	private AutoCompleteTextView mACTextView;
	private ArrayAdapter mAdapter;
	private AutoCompleteTextView mACTextViewLocation;
	private PlacesAutoCompleteAdapter mAdapterLocation;
	
	private Button mAddAttendeeButton;
	private Button mSendInvitationButton;
	private Button mSeeMeetingButton;
	private Button mCancelMeetingButton;
	int mCurCheckPosition = 0;
	//coordinates of meeting location
	// they will be set when choosing from location autocomplete list
	private LatLng location = null;
	
	private List<UserInfo> friendsList = new ArrayList<UserInfo>();
	private List<String> meetingList = new ArrayList<String>();
	// meeting attendees' ids to be sent to server
	private List<Integer> attendeesIds = new ArrayList<Integer>();
	private List<UserInfo> friendsInMeetingList = new ArrayList<UserInfo>();
	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	
	private static final String API_KEY = "YOUR_API_KEY";
	
	private ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;
	
	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?sensor=false&key=" + API_KEY);
	        sb.append("&components=country:uk");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));
	
	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());
	
	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }
	
	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
	
	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<String>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }
	
	    return resultList;
	}
	
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;

	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }

	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return resultList.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = autocomplete(constraint.toString());

	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}

	
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
	
	public void hideKeyboard(View view, AutoCompleteTextView mACTextView) {
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
		mACTextViewLocation = (AutoCompleteTextView) setMeetingView.findViewById(R.id.autoCompleteTextView2);
		mACTextView.setThreshold(1);
		mACTextViewLocation.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1));

		
		
		// Remove keyboard after user selects an element		
		mACTextView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				hideKeyboard(arg1, mACTextView);
			}
		});

		mACTextView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});		
		
		// Remove keyboard after user selects an element		
		mACTextViewLocation.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			        String str = (String) adapterView.getItemAtPosition(position);
			        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();

					hideKeyboard(view, mACTextViewLocation);
				}
		});
		
		mACTextViewLocation.setOnTouchListener(new OnTouchListener() {
			
			@Override
			
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
		    	UserInfo friend = null;
		    	
		    	for (UserInfo fr : friendsList){
		    		if (fr.getUserName().equals(friendName.toString())){
		    			friend = fr;
		    			break;
		    		} 
		    	}
		    	
		    	if (friendName.toString().isEmpty()) {
		    		Toast.makeText(getActivity(), "You must enter a name!", Toast.LENGTH_SHORT).show();
		    	} else if (UserInfo.containsUser(friendsList, friendName.toString()) == null) {
		    		Toast.makeText(getActivity(), friendName.toString() + " is not in your friend list!", Toast.LENGTH_SHORT).show();
		    	} else {
			    	if (meetingList.contains(friendName.toString())) {
			    		Toast.makeText(getActivity(), friendName.toString() + " is already marked to join the meeting!", Toast.LENGTH_SHORT).show();
			    	} else {	
			    		meetingList.add(friendName.toString());
			    		friendsInMeetingList.add(friend);
			    		mACTextView.setText("");
			    		
			    		
			    		String allAttendees = "Attendees: ";
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
				
				if (mACTextViewLocation.getText().toString().isEmpty()) {
					Toast.makeText(getActivity(), "You must specify a location!", Toast.LENGTH_SHORT).show();
				} else if (location == null){
					Toast.makeText(getActivity(), "Not a valid location!", Toast.LENGTH_SHORT).show();
				} else {
					// populate attendesIds list -> for server message needed
					for (UserInfo friend : friendsInMeetingList) {
						attendeesIds.add(friend.getUserId());
					}
					//you cannot add friends to the meeting anymore, but you can see or cancel the meeting
					mAddAttendeeButton.setEnabled(false);
					mSeeMeetingButton.setEnabled(true);
					mCancelMeetingButton.setEnabled(true);
				}
				
			}
		});
		
		/**
		 * When SeeMeeting is pressed move to Map Fragment to see how meeting goes
		 */
		mSeeMeetingButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {					
				getActivity().getActionBar().setSelectedNavigationItem(0);
				// mark located attribute for all attendees
				// in order to start locating them on map
				for (UserInfo friend : friendsInMeetingList) {
					friend.locate();
				}
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
				attendeesIds.clear();
				friendsInMeetingList.clear();
				mAddAttendeeButton.setEnabled(true);
				mSendInvitationButton.setEnabled(false);
				mSeeMeetingButton.setEnabled(false);
				mCancelMeetingButton.setEnabled(false);
				
				//remove friends and location from map
				for (UserInfo friend : friendsInMeetingList) {
					friend.locate();
				}
				
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
    	hideKeyboard(setMeetingView, mACTextView);
    	hideKeyboard(setMeetingView, mACTextViewLocation);
    	super.onDestroyView();    	        
    }
    
    @Override
    public void onDestroy() {
    	hideKeyboard(setMeetingView, mACTextView);
    	hideKeyboard(setMeetingView, mACTextViewLocation);
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
			
        	return null;
        }

        protected void onProgressUpdate(Integer progress) {            
        	Toast.makeText(getActivity(), Integer.toString(progress), Toast.LENGTH_SHORT).show();
        }
        
        @Override
        protected void onPostExecute(Void response) {
            super.onPostExecute(response);
            if (this.fragmentWeakRef.get() != null) {
            	//TODO          
            	
            }
        }
        
        
    }
}
