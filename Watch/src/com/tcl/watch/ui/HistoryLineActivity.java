package com.tcl.watch.ui;

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tcl.watch.R;
import com.tcl.watch.bean.SensorBean;

@SuppressLint("NewApi")
public class HistoryLineActivity extends BaseActivity {
	private GoogleMap mMap;
	Context mContext;
	private String start;
	private String stop;
	private ArrayList<SensorBean> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_line);
		Button button = (Button) findViewById(R.id.b_detail);
		Bundle bundle = getIntent().getExtras();
		start = bundle.getString("start");
		stop = bundle.getString("stop");
		FinalDb finalDb = FinalDb.create(mContext);
		list = (ArrayList<SensorBean>) finalDb.findAllByWhere(SensorBean.class,
				"dates >= '" + start + "' and dates <= '" + stop
						+ "' order by dates desc limit 100");
		setUpMapIfNeeded();
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HistoryLineActivity.this,
						ShowHistoricDataActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("start", start);
				bundle.putString("stop", stop);
				intent.putExtras(bundle);
				HistoryLineActivity.this.startActivity(intent);

			}
		});
	}

	@Override
	public void onResume() {
		setUpMapIfNeeded();
		super.onResume();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
			mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// 地图描点
				PolylineOptions lineOptions = new PolylineOptions();
				double lat = 0.0f;
				double lng = 0.0f;
				double lastLat = 0.0f;
				double lastLng = 0.0f;
				for (int i = 0; i < list.size(); i++) {
					lat = list.get(i).getLatituede();
					lng = list.get(i).getLongitude();
					if (lat != lastLat || lng != lastLng
							&& (lat != 0.0f && lng != 0.0f)) {
						LatLng latLng = new LatLng(lat, lng);
						lineOptions.add(latLng);

					}
					lastLat = lat;
					lastLng = lng;
				}
				if (lineOptions.getPoints().size() > 0) {
					lineOptions.width(3);
					lineOptions.color(Color.BLUE);

					mMap.addPolyline(lineOptions);
					// 定位到第0点经纬度
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							lineOptions.getPoints().get(0), 18));

				}
			}
		}
	}

}
