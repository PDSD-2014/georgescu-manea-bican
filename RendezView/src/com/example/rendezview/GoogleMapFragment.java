package com.example.rendezview;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private int UPDATE_LOCATION_TIMEOUT = 15 * 1000;
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
        //if (mMap == null) {
        	mMap = ((MapView) inflatedView.findViewById(R.id.map)).getMap();                       
        //}
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
    
    private void setUpMap() {    	    	    	                      
    	Location userLocation = getUserLocation();
    	
    	LatLng userLatLng = null;
    	
    	if (userLocation != null) {
    		userLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
    	} else {
    		userLatLng = defaultLatLng;
    	}
    	
    	setMarkerOnMap(userLocation.getLatitude(), userLocation.getLongitude());       
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
}