package com.example.rendezview;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapFragment extends Fragment implements android.location.LocationListener {
	
    private MapView mMapView;
    private GoogleMap mMap;
    private LocationManager mLocationManager;      
   
    private String PROVIDER_GPS = "gps";
    private String PROVIDER_NETWORK = "network";
    private String PROVIDER_PASSIVE = "passive";
    
    // 30 s location update interval
    private int UPDATE_LOCATION_TIMEOUT = 60 * 1000;
    // 0 meters location update
    private int UPDATE_LOCATION_DISTANCE = 0;
    
    private LatLng defaultLatLng = new LatLng(44.438929, 26.104165);
    
    // This should be equal to the name of the user that is logged on this phone
    private String markerTitle = "Bican Daniel";
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.google_map_layout, container, false);    	    	    
    		
        mMapView = (MapView) inflatedView.findViewById(R.id.map);        
        mMapView.onCreate(savedInstanceState);
        
        MapsInitializer.initialize(getActivity());              
        
        setUpMapIfNeeded(inflatedView);
        setUpMap();              
        
        return inflatedView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = getBestProviderForLocation();
        mLocationManager.requestLocationUpdates(provider, UPDATE_LOCATION_TIMEOUT, UPDATE_LOCATION_DISTANCE, this);
    }

    private void setUpMapIfNeeded(View inflatedView) {
        mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();                       
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
    
    private void setUpMap() {    	    	    	                      
    	Location userLocation = getUserLocation();
    	
    	LatLng userLatLng = null;
    	
    	if (userLocation != null) {
    		userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
    		setMarkerOnMap(userLatLng.latitude, userLatLng.longitude);    		
        	sendLocationToServer(userLatLng.latitude, userLatLng.longitude);
    	}    	    
    }   
    
    private void setMarkerOnMap(double latitude, double longitude) {
    	LatLng userLatLng = new LatLng(latitude, longitude);
    	
    	MarkerOptions userMarkerOptions = new MarkerOptions();
    	userMarkerOptions.position(userLatLng);
    	userMarkerOptions.title(markerTitle);
    	
    	Marker userMarker = mMap.addMarker(userMarkerOptions);
    	userMarker.showInfoWindow();
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15));      
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

	@Override
	public void onLocationChanged(Location location) {		
		mMap.clear();		
		setMarkerOnMap(location.getLatitude(), location.getLongitude());
		sendLocationToServer(location.getLatitude(), location.getLongitude());
		// TODO - retrieve from local list the coordinates of friends 
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
	    	
	    	String messageForServer = "1" + " " + userId + " " + userIdLatitudeAndLongitude[2] + " " + userIdLatitudeAndLongitude[2] + "\n";
	    	
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
}