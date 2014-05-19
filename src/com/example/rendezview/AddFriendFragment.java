package com.example.rendezview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class AddFriendFragment extends Fragment{

	private WeakReference<TryToRetrieveUsersList> asyncTaskWeakRef;
	
	private View addFriendView;
	
	private AutoCompleteTextView mACTextView;
	private ArrayAdapter mAdapter;
	private int positionFromAdapter;
	
	private Button mAddFriendButton;
	private Button mLocateFriendButton;
	private Button mFriendsListButton;
	
	// TODO - this list will be populated by the async thred in background from server responses
	private List<UserInfo> usersList = new ArrayList<UserInfo>();		
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);					
		setRetainInstance(true);		
		if (usersList.size() == 0) {
			int userId = MainActivity.getUserInfo().getUserId();
    		if (userId != -1)
    			startNewAsyncTask(userId);	
		}
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
		
		mAddFriendButton = (Button) addFriendView.findViewById(R.id.add_friend_button1);
		mLocateFriendButton = (Button) addFriendView.findViewById(R.id.add_friend_button2);
		mFriendsListButton = (Button) addFriendView.findViewById(R.id.add_friend_button3);
		
		// Check what user has typed and do something depending on the text he entered
		mAddFriendButton.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	String friendName = mACTextView.getEditableText().toString();
		    	
		    	UserInfo ui = null;
		    	if ((ui = UserInfo.containsUser(usersList, friendName)) == null) {
		    		Toast.makeText(getActivity(), " Please enter complete name!", Toast.LENGTH_SHORT).show();
		    		mLocateFriendButton.setEnabled(false);
			        mFriendsListButton.setEnabled(false);
		    	} else {
		    		List<UserInfo> friendsList = MainActivity.getFriendsList();
			    	// Check if the user is already friend with this user
		    		if (UserInfo.containsUser(friendsList, friendName) != null) {
			    		Toast.makeText(getActivity(), friendName.toString() + " is already your friend!", Toast.LENGTH_SHORT).show();
			    		mLocateFriendButton.setEnabled(true);
				        mFriendsListButton.setEnabled(true);
			    	} else {			    					    				    					    	
			    		// Add friend to the friends list from MainActivity in order to make it
			    		// accesible for any fragment			    		
	    				MainActivity.addToFriendsList(ui);			    			
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

    public void hideKeyboard(View view) {
		 InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		 in.hideSoftInputFromWindow(mACTextView.getWindowToken(), 0);
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
	
    // AsyncTask methods.    
    private void startNewAsyncTask(int userId) {
    	TryToRetrieveUsersList asyncTask = new TryToRetrieveUsersList(this);
		this.asyncTaskWeakRef = new WeakReference<TryToRetrieveUsersList>(asyncTask);
		asyncTask.execute(userId);		
	}
	
	private boolean isAsyncTaskPendingOrRunning() {
	    return this.asyncTaskWeakRef != null &&
			    this.asyncTaskWeakRef.get() != null &&
			    !this.asyncTaskWeakRef.get().getStatus().equals(Status.FINISHED);
	}	
			   
    private void setAdapterForACTV() {
		//populateUsersList();
		mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, UserInfo.getUsersNamesAsString(usersList));		
		mACTextView.setAdapter(mAdapter);
	}
    
    private void populateUsersList() {
		usersList.clear();
//		usersList.add(new UserInfo("Bican Daniel", new LatLng(0,0),  0, 0));
//		usersList.add(new UserInfo("Badarcea Mihai", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Bivol Calin", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Bolovan Dinu", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Georgescu Bianca", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Bican Andreea", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Manea Valentina", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Neagu Djuvara", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Tiberiu Berariu", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Mihai Stan", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Gino Iorgulescu", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Cristiana Enescu", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Oliver Twist", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Mama lui Stefan cel Mare", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Dimitrie Cantemir", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Dean Carnazes", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Veronica Micle", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Mihai Eminsecu", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Stanislav Milkovici", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Ioan Cuza", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Maria Plopeanu", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Goe Patitul", new LatLng(0,0), (long) 0, 0));
//		usersList.add(new UserInfo("Elena Udrea", new LatLng(0,0), (long) 0, 0));
	}
    
    // Class that does the background work
	private class TryToRetrieveUsersList extends AsyncTask<Integer, Void, String> {

		private String TAG = "AddFriendFragment.TryToRetrieveUsersList";
		
        private WeakReference<AddFriendFragment> fragmentWeakRef;
                 
        private TryToRetrieveUsersList (AddFriendFragment fragment) {
            this.fragmentWeakRef = new WeakReference<AddFriendFragment>(fragment);
        }

        @Override
        protected String doInBackground(Integer... id) {
            //TODO: usersList has to be updated here from the server
        	Log.d(TAG, "Se executa doInBackground!");
        	            
	    	int userId = id[0].intValue();
	    	String messageForServer = "6" + " " + userId + "\n";
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
            	Toast.makeText(getActivity(), "Cannot retrieve the list of users from server", Toast.LENGTH_LONG).show();
            } else if (this.fragmentWeakRef.get() != null) {
            	//TODO: treat the result
            	String[] resultParts  = result.split(" ");
            	
            	if (Integer.valueOf(resultParts[0]) == 6) {
	            	int numberOfUsers = Integer.valueOf(resultParts[1]);
	            	if (numberOfUsers == 0) {
	            		Toast.makeText(getActivity(), "Sorry but there is no other user online", Toast.LENGTH_LONG).show();
	            	} else {
	            		usersList.clear();
		            	for (int i = 0; i < numberOfUsers; i++) {
		            		String userName = resultParts[3 + i*3] + " " + resultParts[4 + i*3];
		            		Integer userId = Integer.valueOf(resultParts[2 + i*3]);
		            		usersList.add(new UserInfo(userName, new LatLng(0,0), userId, 0));		            		
		            	}
		            	
	            		if (!mLocateFriendButton.isEnabled()) {
		            		setAdapterForACTV();		            		
		            	}
	            	}
            	}
            }
        }
        
        
    }	
}
