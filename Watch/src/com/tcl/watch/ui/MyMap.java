package com.tcl.watch.ui;

import java.util.ArrayList;
import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.internal.ew;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.MapFragment;
import com.tcl.watch.ConfigData;
import com.tcl.watch.R;
import com.tcl.watch.bean.GPSBean;
import com.tcl.watch.logic.DataService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class MyMap {
	Context mContext;
	// 百度地图
	private LocationMode mCurrentMode;
	BitmapDescriptor mCurrentMarker;
	MapView mBDMapView;
	BaiduMap mBaiduMap;
	boolean isFirstLoc = true;// 是否首次定位
	private BroadcastReceiver BDBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(DataService.BDACTION)) {
				Bundle bundle = intent.getExtras();
				double lat = bundle.getDouble("latitude");
				double log = bundle.getDouble("longitude");
				float raidus = bundle.getFloat("radius");
				if (MainActivity.IN_CHINA == 1 && mBDMapView != null
						&& mBaiduMap != null) {
					// 增加点
					MyLocationData locData = new MyLocationData.Builder()
							.accuracy(raidus)
							// 此处设置开发者获取到的方向信息，顺时针0-360
							.direction(100).latitude(lat).longitude(log)
							.build();
					mBaiduMap.setMyLocationData(locData);
					if (isFirstLoc) {
						isFirstLoc = false;
						LatLng ll = new LatLng(lat, log);
						MapStatusUpdate u = MapStatusUpdateFactory
								.newLatLngZoom(ll,20);
						mBaiduMap.animateMapStatus(u);

					}
					mBaiduMap
							.setMyLocationConfigeration(new MyLocationConfiguration(
									mCurrentMode, true, null));
				}
			}

		}

	};
	// google map
	private GoogleMap mGMap;
	private MapFragment mapFragment;

	private RelativeLayout parentLayout;// 加载地图的父类

	public MyMap(Context context, RelativeLayout parentLayout) {
		this.mContext = context;
		this.parentLayout = parentLayout;

		create();
	}

	public void create() {
		if (MainActivity.IN_CHINA == 1) {
			mCurrentMode = LocationMode.NORMAL;
			mBDMapView = new MapView(mContext);
			parentLayout.addView(mBDMapView);
			mBaiduMap = mBDMapView.getMap();
			// 开启定位图层
			mBaiduMap.setMyLocationEnabled(true);
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(DataService.BDACTION);
			mContext.registerReceiver(BDBroadcastReceiver, intentFilter);
		} else {

			setUpMapIfNeeded();
		}
	}

	public void onPause() {
		if (MainActivity.IN_CHINA == 1) {
			if (mBDMapView != null) {
				mBDMapView.onPause();
			}
		} else {

		}
	}

	public void onResume() {
		if (MainActivity.IN_CHINA == 1) {
			if (mBDMapView != null) {
				mBDMapView.onResume();
			}

		} else {
			setUpMapIfNeeded();
		}
	}

	public void onDestroy() {
		// 退出时销毁定位
		// 关闭定位图层
		if (MainActivity.IN_CHINA == 1) {
			mBaiduMap.setMyLocationEnabled(false);
			if (mBDMapView != null) {
				mBDMapView.onDestroy();
				mBDMapView = null;
			}
			mContext.unregisterReceiver(BDBroadcastReceiver);
		}

	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mGMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mapFragment = new MapFragment();
			FragmentTransaction fragmentTransaction = ((Activity) mContext)
					.getFragmentManager().beginTransaction();
			fragmentTransaction.add(parentLayout.getId(), mapFragment);
			fragmentTransaction.commit();
			// mapFragment=((MapFragment)((Activity)mContext).getFragmentManager().findFragmentById(
			// R.id.map));
			// Check if we were successful in obtaining the map.
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mGMap = mapFragment.getMap();
					if (mGMap == null) {
						handler.postDelayed(this, 500);
					} else {
						getMap();
					}

				}
			}, 500);

			getMap();
		}
	}

	private void getMap() {
		if (mGMap != null) {
			mGMap.setMyLocationEnabled(true);
			mGMap.setOnMyLocationButtonClickListener(new OnMyLocationButtonClickListener() {

				@Override
				public boolean onMyLocationButtonClick() {
					// TODO Auto-generated method stub
					return false;
				}
			});
		}
	}

	public void setLine(ArrayList<GPSBean> list) {
		if (MainActivity.IN_CHINA == 1) {
			mBaiduMap.clear();
			List<LatLng> points = new ArrayList<LatLng>();
			double lat = 0.0f;
			double lng = 0.0f;
			double lastLat = 0.0f;
			double lastLng = 0.0f;
			for (GPSBean gpsBean : list) {
				lat = gpsBean.getLatituede();
				lng = gpsBean.getLongitude();
				if (lat != lastLat || lng != lastLng
						&& (lat != 0.0f && lng != 0.0f)) {
					points.add(new LatLng(lat, lng));
				}
				lastLat = lat;
				lastLng = lng;
			}
			if (points.size() > 1) {
				OverlayOptions ooPolyline = new com.baidu.mapapi.map.PolylineOptions()
						.width(10).color(0xAAFF0000).points(points);
				mBaiduMap.addOverlay(ooPolyline);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(points
						.get(0));
				mBaiduMap.animateMapStatus(u);
			}
		} else {
			mGMap.clear();
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
					com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(
							lat, lng);
					lineOptions.add(latLng);
				}
				lastLat = lat;
				lastLng = lng;
			}
			if (lineOptions.getPoints().size() > 1) {
				lineOptions.width(3);
				lineOptions.color(Color.BLUE);
				if (mGMap != null) {
					mGMap.addPolyline(lineOptions);
					// 定位到第0点经纬度
					mGMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
							lineOptions.getPoints().get(0), 18));
				}

			}
		}
	}

}
