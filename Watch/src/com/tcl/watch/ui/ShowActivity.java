package com.tcl.watch.ui;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.tcl.watch.ConfigData;
import com.tcl.watch.R;
import com.tcl.watch.bean.SensorBean;
import com.tcl.watch.logic.DataService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ShowActivity extends BaseActivity implements OnClickListener,
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener {

	private TextView showTextView;
	private Button openButton;
	private GoogleMap mMap;
	private GoogleApiClient mGoogleApiClient;
	Context mContext;
	StringBuffer stringBuffer = new StringBuffer();

	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	
	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			SensorBean bean = (SensorBean) bundle.getSerializable("sensor");
			stringBuffer.setLength(0);

			stringBuffer.append(MainActivity.getNowTime("yyyy年MM月dd日"))
					.append(" ").append(MainActivity.getNowTime("hh : mm"))
					.append("\n").append(getLatituede(bean.getLatituede()))
					.append("	").append(getLongtitude(bean.getLongitude()))
					.append("\n").append("海拔：").append(bean.getAltitude())
					.append("米 ").append("方向：").append(bean.getBearing())
					.append(" ").append("速度：").append(bean.getSpeed())
					.append("km/h\n").append("角速度：").append(bean.getMsenv())
					.append("rad/s\n\n");
			showTextView.setText(stringBuffer.toString());
		}
	};

	public static String getLatituede(double mLatituede) {
		if (mLatituede == 0) {
			return "纬度：0°";
		} else {
			return ((mLatituede > 0) ? ("北纬" + mLatituede + "°") : ("南纬"
					+ mLatituede + "°"));
		}
	}

	public static String getLongtitude(double mLongtitude) {
		if (mLongtitude == 0) {
			return "经度：0°";
		} else {
			return ((mLongtitude > 0) ? ("东经" + mLongtitude + "°") : ("西经"
					+ mLongtitude + "°"));
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);
		mContext = this;
		showTextView = (TextView) findViewById(R.id.tv_show);
		openButton = (Button) findViewById(R.id.b_open);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DataService.ACTION_DATA);
		registerReceiver(receiver, intentFilter);

		if (SettingActivity.isServiceRunning(mContext,
				DataService.class.getName())) {
			ConfigData.open = 1;
		} else {
			ConfigData.open = 0;
		}
		if (ConfigData.open == 0) {
			openButton.setText(mContext.getResources()
					.getString(R.string.start));
		} else {
			openButton
					.setText(mContext.getResources().getString(R.string.stop));
		}

		openButton.setOnClickListener(this);
		showTextView.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.b_open:
			if (ConfigData.open == 0) {
				ConfigData.open = 1;
				openButton.setText(mContext.getResources().getString(
						R.string.stop));
				Intent intent = new Intent(mContext, DataService.class);
				startService(intent);		
			} else {
				ConfigData.open = 0;
				openButton.setText(mContext.getResources().getString(
						R.string.start));
				Intent intent = new Intent(mContext, DataService.class);
				stopService(intent);
			}
			break;

		case R.id.tv_show:
//			Intent intent = new Intent(ShowActivity.this, MapActivity.class);
//			startActivity(intent);
			break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpGoogleApiClientIfNeeded();
		mGoogleApiClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
			}
		}
	}

	private void setUpGoogleApiClientIfNeeded() {
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
	}

	/**
	 * Button to get current Location. This demonstrates how to get the current
	 * Location as required without needing to register a LocationListener.
	 */
	public void showMyLocation(View view) {
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			String msg = "Location = "
					+ LocationServices.FusedLocationApi
							.getLastLocation(mGoogleApiClient);
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location) {

	}

	/**
	 * Callback called when connected to GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, REQUEST, this); // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Toast.makeText(this, "connection result :" + result,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onMyLocationButtonClick() {
		Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
				.show();
		// Return false so that we don't consume the event and the default
		// behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}
}
