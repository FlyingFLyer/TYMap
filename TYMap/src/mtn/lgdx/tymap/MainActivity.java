package mtn.lgdx.tymap;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;

public class MainActivity extends Activity implements LocationSource, AMapLocationListener, OnMarkerClickListener{
	
	private MapView mapView;
	private AMap aMap;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private UiSettings uiSettings;
	private ActionBar actionBar;
	
	private LatLng current_latLng;
	private LatLng old_latLng;
	private MarkerOptions markerOptions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		setContentView(R.layout.activity_main);
		actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowTitleEnabled(false);
		
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
//		// Associate searchable configuration with the SearchView
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconified(false);					// Do not iconify the widget; expand it by default
		searchView.setSubmitButtonEnabled(true);		//add a "submit" button
		searchView.onWindowFocusChanged(false);
		
		//return true;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		Intent intent;
		switch (id) {
		case R.id.action_search:
			//intent = new Intent(this.getApplicationContext(),SearchActivity.class);
			//startActivity(intent);
			break;
		case R.id.action_track:
			intent = new Intent(this.getApplicationContext(),TrackActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mapView.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mapView.onPause();
		deactivate();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mapView.onDestroy();
	}
	
	private void init(){
		if (aMap == null) {
			aMap = mapView.getMap();
			uiSettings = aMap.getUiSettings();
			setUpMap();
		}
	}
	
	private void setUpMap(){
		aMap.setLocationSource(this);
		//aMap.getUiSettings().setMyLocationButtonEnabled(true);
		uiSettings.setMyLocationButtonEnabled(true);	//display location button
		uiSettings.setCompassEnabled(true);				//display compass
		uiSettings.setScaleControlsEnabled(true);		//display scale control 
		uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_BUTTOM);	//
		aMap.setMyLocationEnabled(true);
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
		aMap.setOnMarkerClickListener(this);		//set Marker click listener
	}

	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 10*1000, 5, this);
		}
	}

	@SuppressWarnings("deprecation")
	public void deactivate() {
		// TODO Auto-generated method stub
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onLocationChanged(AMapLocation amapLocation) {
		// TODO Auto-generated method stub
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null
					&& amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);		// 
				current_latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
				markerOptions = new MarkerOptions();
				markerOptions.visible(true)
							.position(current_latLng)
							.title("地址"+amapLocation.getAddress())
							.snippet("经度"+current_latLng.longitude+"  纬度"+current_latLng.latitude)
							.draggable(false)
							.setFlat(true);
				aMap.addMarker(markerOptions);
				aMap.addPolyline(new PolylineOptions().add(old_latLng,current_latLng).color(Color.GREEN));
				old_latLng = current_latLng;
				
			} else {
				Log.e("AmapErr","Location ERR:" + amapLocation.getAMapException().getErrorCode());
			}
		}
	}

	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}




	
}
