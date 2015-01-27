package com.tcl.watch.ui;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalDb;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tcl.watch.ConfigData;
import com.tcl.watch.R;
import com.tcl.watch.bean.ControlBean;
import com.tcl.watch.bean.GPSBean;
import com.tcl.watch.logic.DataService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class ShowActivity extends BaseActivity implements OnClickListener {
	private static final String TAG=ShowActivity.class.getName();
	private TextView showTextView;
	private Button openButton;
	Context mContext;
	StringBuffer stringBuffer = new StringBuffer();
	public static String strartDate;
	public static String stopDate;
	FinalDb mFinalDb;

	BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(DataService.ACTION_DATA)) {
				Bundle bundle = intent.getExtras();
				GPSBean bean = (GPSBean) bundle.getSerializable("gps");
				stringBuffer.setLength(0);

				stringBuffer.append(MainActivity.getNowTime("yyyy年MM月dd日"))
						.append(" ").append(MainActivity.getNowTime("hh : mm"))
						.append("\n").append(getLatituede(bean.getLatituede()))
						.append("	").append(getLongtitude(bean.getLongitude()))
						.append("\n").append("海拔：").append(bean.getAltitude())
						.append("米 ").append("方向：").append(bean.getBearing())
						.append(" ").append("速度：").append(bean.getSpeed())
						.append("km/h\n");
				showTextView.setText(stringBuffer.toString());
			}
		}
	};

	// private MyMap myMap;

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
		// setContentView(R.layout.activity_show);//手机界面
		setContentView(R.layout.activity_show_watch);// 手表节目
		mContext = this;
		showTextView = (TextView) findViewById(R.id.tv_show);
		openButton = (Button) findViewById(R.id.b_open);
		RelativeLayout mapParentLayout = (RelativeLayout) findViewById(R.id.rl_map);
		mFinalDb = FinalDb.create(mContext);
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
		// myMap = new MyMap(mContext, mapParentLayout);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		// myMap.onDestroy();
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
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				strartDate = dateFormat.format(new Date());
			} else {
				ConfigData.open = 0;
				openButton.setText(mContext.getResources().getString(
						R.string.start));
				Intent intent = new Intent(mContext, DataService.class);
				stopService(intent);
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd hh:mm:ss");
				stopDate = dateFormat.format(new Date());
				ControlBean controlBean = new ControlBean();
				controlBean.setStartDate(strartDate);
				controlBean.setStopDate(stopDate);
				mFinalDb.save(controlBean);
			}
			break;

		case R.id.tv_show:
			// Intent intent = new Intent(ShowActivity.this, MapActivity.class);
			// startActivity(intent);
			break;
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		// myMap.onResume();
		// setUpMapIfNeeded();
		// setUpGoogleApiClientIfNeeded();
		// mGoogleApiClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		// myMap.onPause();
		// if (mGoogleApiClient != null) {
		// mGoogleApiClient.disconnect();
		// }
	}

}
