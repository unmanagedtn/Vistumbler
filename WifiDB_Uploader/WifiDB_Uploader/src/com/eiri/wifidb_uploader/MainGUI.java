package com.eiri.wifidb_uploader;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainGUI extends Activity implements OnClickListener {
	private static final String TAG = "WiFiDB_Demo";
	Switch ScanSwitch;
	TextView textStatus;
	Button buttonScan;
	static GoogleMap map;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Setup UI
		ScanSwitch = (Switch) findViewById(R.id.ScanSwitch);
		ScanSwitch.setOnClickListener(this);
		if (isScanServiceRunning()){
			ScanSwitch.setChecked(true);
		}else{
			ScanSwitch.setChecked(false);
		}
		
		android.app.FragmentManager fragmentManager = getFragmentManager();  
	     MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);  
	     map = mapFragment.getMap(); 

		//Setup GPS
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
		}else{
			showGPSDisabledAlertToUser();
		}			
	}
	
	private void showGPSDisabledAlertToUser(){
		Log.d(TAG, "showGPSDisabledAlertToUser");
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
		.setCancelable(false)
		.setPositiveButton("Goto Settings Page To Enable GPS",
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						Intent callGPSSettingIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(callGPSSettingIntent);
					}
				}
		);
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int id){
						dialog.cancel();
					}
				}
		);
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, 0, 0, "Settings");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, QuickPrefsActivity.class));
                return true;
        }
        return false;
    }
    
	@Override
	public void onClick(View src) {
		int id = src.getId();
		if(id == R.id.ScanSwitch) {
	    	Log.d(TAG, "ScanSwitch Pressed");
	      	ScanSwitch = (Switch) findViewById(R.id.ScanSwitch);
	      	if (ScanSwitch.isChecked()){
	      		Log.d(TAG, "Start Scan");
	      		startService(new Intent(this, ScanService.class));
	      		ScanSwitch.setChecked(true);
	      	} else {
	      		Log.d(TAG, "Stop Scan");
	      		stopService(new Intent(this, ScanService.class));
	      		ScanSwitch.setChecked(false);
	        }
		}
	}
	
	private boolean isScanServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (ScanService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	
	public static void UpdateMapLocation(LatLng CurrentLoc) {
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(CurrentLoc, 15));
	}
	
	public static void UpdateMapZoomLevel(Integer zoomlevel) {
    	map.animateCamera(CameraUpdateFactory.zoomTo(zoomlevel), 2000, null); 
	}	
}
