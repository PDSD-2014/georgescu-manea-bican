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

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapFragment extends Fragment implements android.location.LocationListener {
	
    private String TAG = "GoogleMapFragment";
	
	private MapView mMapView;
    private GoogleMap mMap;
    private LocationManager mLocationManager;      
   
    private String PROVIDER_GPS = "gps";
    private String PROVIDER_NETWORK = "network";
    private String PROVIDER_PASSIVE = "passive";
    
    // 30 s location update interval
    private int UPDATE_LOCATION_TIMEOUT = 10 * 1000;
    // 0 meters location update
    private int UPDATE_LOCATION_DISTANCE = 0;             
    
    private List<Marker> markerList = new ArrayList<Marker>();
    
    private int widthPixels, heightPixels;
    
    private String WAITING_FOR_SERVER = "Waiting ...";          
    
    private float cameraZoom = 15;
    
    private int numberOfFriends = 0;
    
    private Location oldLocation;
    private List<UserInfo> oldFriendList = MainActivity.getFriendsList();
    
    private int firstTime = 0;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.google_map_layout, container, false);    	    	    
    	
        mMapView = (MapView) inflatedView.findViewById(R.id.map);        
        mMapView.onCreate(savedInstanceState);
        
        MapsInitializer.initialize(getActivity());              
        
        mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();
        
        // Show markers on map.
        clearMap();
        setUserOnMap();
        getFriendsLocations();
        setFriendsOnMap();
        setCamera();
        
        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
                
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = getBestProviderForLocation();
        mLocationManager.requestLocationUpdates(provider, UPDATE_LOCATION_TIMEOUT, UPDATE_LOCATION_DISTANCE, this);
        
        widthPixels = getActivity().getResources().getDisplayMetrics().widthPixels;
        heightPixels = getActivity().getResources().getDisplayMetrics().heightPixels;
    }  

    private String getBestProviderForLocation() {
    	List<String> providers = mLocationManager.getProviders(true);
    	
    	String provider = null;
    	
    	if (providers.contains(PROVIDER_GPS)) {
    		provider = PROVIDER_GPS;
    	} else if (providers.contains(PROVIDER_NETWORK)) {
    		provider = PROVIDER_NETWORK;
    	} else if (providers.contains(PROVIDER_PASSIVE)) {
    		provider = PROVIDER_PASSIVE;
    	}
    	
    	return provider;
    }
    
    private Location getUserLocation() {    	    	
    	Location location = null;
    	
    	String provider = getBestProviderForLocation();
    	
    	if (provider != null) {
    		location = mLocationManager.getLastKnownLocation(provider);
    	} 
    	    	
    	return location;
    }
    
    // Send the location of the user to the server
    private void sendLocationToServer(double latitude, double longitude) {
    	TryToSendLocation tryToSendLocation = new TryToSendLocation();
    	if (tryToSendLocation != null) {
    		double userId = MainActivity.getUserInfo().getUserId();
    		if (userId != -1)
    			tryToSendLocation.execute(userId, latitude, longitude);
    	}
    }
    
    private void getFriendsLocations() {
    	List<UserInfo> friendsList = MainActivity.getFriendsList();
    	
    	// Show all friends on the map
    	if (friendsList.size() > 0) {
    		for (UserInfo friend : friendsList) {
    			if (friend.getLocated() == 1) {
    				// Request its location from server
    				TryToRetrieveUserLocation tryToRetrieveUserLocation = new TryToRetrieveUserLocation();
    				tryToRetrieveUserLocation.execute(friend.getUserId());    				
    			}
    		}
    	}
    }
    
    private void setFriendsOnMap() {    	    	
    	List<UserInfo> friendsList = MainActivity.getFriendsList();
    	
    	// Show all friends on the map
    	if (friendsList.size() > 0) {
    		for (UserInfo friend : friendsList) {
    			if (friend.getLocated() == 1) {
    				setMarkerOnMap(friend.getUserLocation().latitude, friend.getUserLocation().longitude, friend.getUserName());
    			}
    		}
    	}
    }
    
    private void setCamera() {
    	CameraUpdate cu = null;
    	
    	if (markerList.size() > 0) {
	    	LatLngBounds.Builder builder = new LatLngBounds.Builder();	    	
	    	for (Marker marker : markerList) {
	    	    builder.include(marker.getPosition());
	    	}
	    	LatLngBounds bounds = builder.build();
	    	cu = CameraUpdateFactory.newLatLngBounds(bounds, widthPixels, heightPixels, 50);	    		    
    	}     	
    	
    	if (cu != null) {
    		mMap.moveCamera(cu);
    	}
//    	else {
//    		mMap.animateCamera(CameraUpdateFactory.zoomTo(cameraZoom));
//    	}
    }
    
    private void setUserOnMap() {    	    	    	                      
    	Location userLocation = getUserLocation();
    	
    	LatLng userLatLng = null;
    	
    	String uiName = MainActivity.getUserInfo().getUserName(); 
    	
    	if (uiName == null) {
    		oldLocation = userLocation;
    		uiName = WAITING_FOR_SERVER;    	
    	}
    	
    	if (userLocation != null) {
    		userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
    		setMarkerOnMap(userLatLng.latitude, userLatLng.longitude, uiName);    		
        	sendLocationToServer(userLatLng.latitude, userLatLng.longitude);
    	}    	    
    }          

    public String adaptUserNameToMarker(String userName) {
    	String[] nameParts = userName.split(" ");
    	String newName = "";
    	
    	for (int i = 0; i < nameParts.length; i++) {
    		if (i != nameParts.length-1) {
    			newName += nameParts[i] + "\n";
    		} else { 
    			newName += nameParts[i];
    		}
    	}
    	
    	return newName;
    }
    
    // Set marker on map and move the camera.
    private void setMarkerOnMap(double latitude, double longitude, String markerTitle) {
    	LatLng userLatLng = new LatLng(latitude, longitude);    	
    	
    	/*MarkerOptions userMarkerOptions = new MarkerOptions();
    	userMarkerOptions.position(userLatLng);
    	userMarkerOptions.title(markerTitle);    	
    	userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));*/
    	
    	MarkerOptions userMarkerOptions = createMarkerOptions(adaptUserNameToMarker(markerTitle), UserInfo.FRIEND, userLatLng);    
    	if (userMarkerOptions != null) { 
    		Marker userMarker = mMap.addMarker(userMarkerOptions);    		
    		markerList.add(userMarker);
    	} else {
    		Log.e(TAG, "Nu am putut pune marker ul pe harta!");
    	}
    }
    
    /*
     * Custom marker methods
     */
    public MarkerOptions createMarkerOptions(String markerTitle, int markerType, LatLng markerLatLng) {
    	MarkerOptions mo = null;
    	
    	if (getActivity() != null) {
	    	View marker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
			TextView numTxt = (TextView) marker.findViewById(R.id.marker_num_txt);
			numTxt.setText(markerTitle);		
	 
			mo = new MarkerOptions()
			.position(markerLatLng)		
			.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), marker)));
    	}
		
		return mo;
    }
    
    
    public static Bitmap createDrawableFromView(Context context, View view) {
		DisplayMetrics displayMetrics = new DisplayMetrics();		
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
 
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
 
		return bitmap;
	}
    
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
    	super.onDestroy();
    	mMapView.onDestroy();        
    }
    
    @Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}
    
    private void clearMap() {    	    	
    	cameraZoom = mMap.getCameraPosition().zoom; 
    	mMap.clear();
		markerList.clear();
    }

    public boolean hasFriendChangedLocation() {
    	boolean returnFlag = false;
    	
    	List<UserInfo> friendList = MainActivity.getFriendsList();
    	
    	UserInfo ui1, ui2; 
    	
    	if (friendList.size() == 0) {
    		returnFlag = false;
    	} else if (friendList.size() != oldFriendList.size()) { 
    		returnFlag = true;
    	} else {
	    	for (int i = 0; i < friendList.size(); i++) {
	    		if ((ui1 = UserInfo.containsUser(friendList, friendList.get(i).getUserId())) != null &&
	    			(ui2 = UserInfo.containsUser(oldFriendList, friendList.get(i).getUserId())) != null) {
	    			
	    			double lat1 = Math.round(ui1.getUserLocation().latitude *1000.000) / 1000.000;
	    			double lat2 = Math.round(oldLocation.getLatitude() *1000.000) / 1000.000;
	    			double lon1 = Math.round(ui1.getUserLocation().longitude *1000.000) / 1000.000;
	    			double lon2 = Math.round(ui2.getUserLocation().longitude *1000.000) / 1000.000;
	    			
	    			if ((lat1 != lat2) && (lon1 != lon2)) {
	    				returnFlag = true;
	    				firstTime = 0;
	    			}
	    		}
	    	}
    	}
     	
    	return returnFlag;
    }
    
	@Override
	public void onLocationChanged(Location location) {				
		getFriendsLocations();
		
		double lat1 = Math.round(location.getLatitude() *1000.000) / 1000.000;
		double lon1 = Math.round(oldLocation.getLatitude() *1000.000) / 1000.000;
		double lat2 = Math.round(location.getLongitude() *1000.000) / 1000.000;
		double lon2 = Math.round(oldLocation.getLongitude() *1000.000) / 1000.000;
		
		if (((lat1 != lon1) && (lat2 != lon2))
				|| hasFriendChangedLocation()
				|| firstTime < 5) {
			
			clearMap();
			
			String uiName = MainActivity.getUserInfo().getUserName(); 
			
			if (uiName == null)
	    		uiName = WAITING_FOR_SERVER;
					
			setMarkerOnMap(location.getLatitude(), location.getLongitude(), adaptUserNameToMarker(uiName));
			sendLocationToServer(location.getLatitude(), location.getLongitude());		
				        
	        setFriendsOnMap();
	        setCamera();
	        
	        oldLocation = location;
	        oldFriendList = MainActivity.getFriendsList();
	        
	        firstTime++;
		}
	}
																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																													
	@Override
	public void onProviderDisabled(String arg0) {
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		
	}
	
	// Background thread that checks validity of user and password
	protected class TryToSendLocation extends AsyncTask<Double, Void, Void> {
	
	    private final static String TAG = "GoogleMapFragment.TryToSendLocation";			   
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        Log.d(TAG, "Se executa onPreExecute!");	        
	    }
		
	    @SuppressWarnings("unchecked")
	    @Override
	    protected Void doInBackground(Double... userIdLatitudeAndLongitude) {
	    	Log.d(TAG, "Se executa doInBackground!");	        	    		        	       		                
	    
	    	int userId = userIdLatitudeAndLongitude[0].intValue();	    		    
	    	
	    	String messageForServer = "1" + " " + userId + " " + userIdLatitudeAndLongitude[1] + " " + userIdLatitudeAndLongitude[2] + "\n";
	    	
//	    	Log.d(TAG, "*****************************" + messageForServer);
	    	
	    	try {	    		
	    		Socket clientSocket = new Socket(InetAddress.getByName("projects.rosedu.org"), 9000);
				
	    		BufferedWriter messageSender = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
	    		BufferedReader responseReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
	    		messageSender.write(messageForServer);
	    		messageSender.flush();	    					
				
				clientSocket.close();
			} catch (UnknownHostException e) {
				Log.e(TAG, "Eroare la contactare server! " + e.getMessage());
				e.printStackTrace();
			} catch (IOException e) {
				Log.e(TAG, "Eroare la conectare cu socketul! " + e.getMessage());
				e.printStackTrace();
			}
	    	
			return null;	    		    
	    }
	
	    @Override
	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);	        
        	Log.d(TAG, "Se executa onPostExecute!");        	                	        	        		                
			return;	     		       	       		     
	    }	
	}
	
	// Class that does the background work
	private class TryToRetrieveUserLocation extends AsyncTask<Integer, Void, String> {

		private String TAG = "GoogleMapFragment.TryToRetrieveUserLocation";				       
                 	        
        @Override
        protected String doInBackground(Integer... id) {
            //TODO: usersList has to be updated here from the server
        	Log.d(TAG, "Se executa doInBackground!");
        	            	        	
	    	int userId = id[0].intValue();
	    	String messageForServer = 2 + " " + userId + "\n";
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
            	Log.d(TAG, "Nu am obtinut adresa utilizatorului de la server!");
            	Toast.makeText(getActivity(), "Cannot retrieve the users address from server", Toast.LENGTH_LONG).show();
            } else {
            	String[] resultParts  = result.split(" ");
            	
            	int messageType = Integer.valueOf(resultParts[0]); 
            	
            	if (messageType == 2) {
            		int friendId = Integer.valueOf(resultParts[1]);
            			            		
            		if (resultParts.length == 4) {	            			            				            				    			    
            			List<UserInfo> friendList = MainActivity.getFriendsList();
            			UserInfo ui = null;
            			if ((ui = UserInfo.containsUser(friendList, friendId)) != null) {
            				ui.setUserLocation(new LatLng(Double.parseDouble(resultParts[2]), Double.parseDouble(resultParts[3])));
            				MainActivity.addToFriendsList(ui);
            			}
            		}
            	}
            }
        }
	}               
}