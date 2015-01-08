package com.tcl.watch.logic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tcl.watch.ConfigData;
import com.tcl.watch.bean.GPSBean;
import com.tcl.watch.bean.SensorBean;
import com.tcl.watch.data.UserSetting;
import com.tcl.watch.ui.MainActivity;

public class DataService extends Service {

	public final static String TAG = DataService.class.getName();
	public Timer madvertieseTimer;
	public TimerTask madvertieseTask;
	private LocationManager mLocationManager;
	private Context mContext;
	public final static String ACTION_DATA = "data";
	private final float NO = -0.0f;
	// private Criteria criteria;//自带gps先熟悉
	// private String provider;//自动gps先注销
	// Location mLocation;//自带gps先注销
	SensorManager mSensorManager;
	// 传感器需要心率，卡路里，温度，紫外线，重力，方向传感器

	private Sensor temperatureSensor;// 温度
	private Sensor gravitySensor;// 重力
	private Sensor gyroscoreSensor; // 陀螺仪

	GPSBean mGPSBean;
	SensorBean mSensorBean;

	private final static int SAVE_10 = 10 * 1000;
	private final static int SAVE_20 = 20 * 1000;
	private final static int SAVE_30 = 30 * 1000;

	FinalDb mFinalDb;

	// 百度地图
	// 百度地图
	LocationClient mBDLocClient;
	public MyBDLocationListenner myBDListener = new MyBDLocationListenner();
	private BDLocation mBDLocation;
	public final static String BDACTION = "baidu_location";

	// google 地图
	private GoogleApiClient mGoogleApiClient;
	private static LocationRequest REQUEST = null;
	private Location mGoogleLocation;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		mGPSBean = new GPSBean();
		mSensorBean = new SensorBean();

		mLocationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mFinalDb = FinalDb.create(mContext);
		// 自动gps先注销
		// criteria = new Criteria();
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
		// criteria.setAltitudeRequired(true);// 设置需要获取海拔方向数据
		// criteria.setBearingRequired(true);// 设置需要获取方位数据
		// criteria.setCostAllowed(true);// 设置允许产生资费
		// criteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
		//
		// provider = mLocationManager.getBestProvider(criteria, true);
		gravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
		temperatureSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
		gyroscoreSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		// 百度地图
		if (MainActivity.IN_CHINA == 1) {

			mBDLocClient = new LocationClient(mContext);
			mBDLocClient.registerLocationListener(myBDListener);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(1000);
			mBDLocClient.setLocOption(option);
			mBDLocClient.start();
		} else {
			REQUEST = LocationRequest.create().setInterval(5000) // 5 seconds
					.setFastestInterval(16) // 16ms = 60fps
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			setUpGoogleApiClientIfNeeded();
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		openGPSSetting();
		unregister();
		register();

	}

	/**
	 * 传感器监听
	 */
	private void register() {
		int mode = UserSetting.getSportMode();
		int sportInterval = 0;
		if (mode == 0) {
			sportInterval = SensorManager.SENSOR_DELAY_NORMAL;
		} else {
			sportInterval = SensorManager.SENSOR_DELAY_GAME;
		}
		mSensorManager.registerListener(mSensorEventListener, gravitySensor,
				sportInterval);
		mSensorManager.registerListener(mSensorEventListener, gyroscoreSensor,
				sportInterval);
		mSensorManager.registerListener(mSensorEventListener,
				temperatureSensor, sportInterval);
		// 自动gps先注销
		// 设置监听器，自动更新的最小时间为间隔N秒(这里的单位是微秒)或最小位移变化超过N米(这里的单位是米)
		// mLocationManager.requestLocationUpdates(provider, 3, 0.5f,
		// locationListener);
		mLocationManager.addGpsStatusListener(gpsStatusListener);
		stopTimer();
		// 保存数据
		int save = UserSetting.getDatasaveInterval();
		int interval = 0;
		switch (save) {
		case 0:
			interval = SAVE_10;
			break;
		case 1:
			interval = SAVE_20;
			break;
		case 2:
			interval = SAVE_30;
			break;
		}
		startTimer(interval, 0);
		// 方向传感
	}

	/**
	 * 
	 * 启动定时器
	 */
	public void startTimer(final int cycleTime, final int afterTime) {
		if (madvertieseTimer == null) {
			madvertieseTimer = new Timer();
		}

		if (madvertieseTask == null) {
			madvertieseTask = new TimerTask() {

				@Override
				public void run() {
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyy-MM-dd hh:mm:ss");
					String date = dateFormat.format(new Date());
					mGPSBean.setDate(date);
					mSensorBean.setDate(date);
					save();
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putSerializable("sensor", mGPSBean);
					intent.putExtras(bundle);
					intent.setAction(ACTION_DATA);
					mContext.sendBroadcast(intent);
				}

			};

		}

		madvertieseTimer.schedule(madvertieseTask, afterTime, cycleTime);
	}

	/**
	 * 保存数据到数据库
	 */
	private void save() {
		GPSBean saveGPSBean = new GPSBean();
		saveGPSBean.setAltitude(mGPSBean.getAltitude());
		saveGPSBean.setBearing(mGPSBean.getBearing());
		saveGPSBean.setDate(mGPSBean.getDate());
		saveGPSBean.setLatituede(mGPSBean.getLatituede());
		saveGPSBean.setLongitude(mGPSBean.getLongitude());
		saveGPSBean.setSpeed(mGPSBean.getSpeed());
		mFinalDb.save(saveGPSBean);
		SensorBean saveSensorBean = new SensorBean();
		saveSensorBean.setDate(mSensorBean.getDate());
		saveSensorBean.setCalorie(mSensorBean.getCalorie());
		saveSensorBean.setFit(mSensorBean.getFit());
		saveSensorBean.setGsenx(mSensorBean.getGsenx());
		saveSensorBean.setGseny(mSensorBean.getGseny());
		saveSensorBean.setGsenz(mSensorBean.getGsenz());
		saveSensorBean.setMsenv(mSensorBean.getMsenv());
		saveSensorBean.setTemperature(mSensorBean.getTemperature());
		saveSensorBean.setUvsen(mSensorBean.getUvsen());
	}

	/**
	 * 暂停定时器
	 */
	public void stopTimer() {
		if (madvertieseTimer != null) {
			madvertieseTimer.cancel();
			madvertieseTimer = null;
		}

		if (madvertieseTask != null) {
			madvertieseTask.cancel();
			madvertieseTask = null;
		}
	}

	private void openGPSSetting() {
		boolean gpsEnabled = mLocationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);

		if (ConfigData.isDebug) {
			Log.d(TAG, "chaoyue location=" + gpsEnabled);
		}
		if (gpsEnabled) {
			// 关闭GPS
		} else {
			toggleGPS();
		}

	}

	private void updateUIToNewLocation() {

		if (MainActivity.IN_CHINA == 1) {
			if (mBDLocation != null) {

				mGPSBean.setAltitude(mBDLocation.getAltitude());
				// mSensorBean.setBearing(mBDLocation.getBearing());
				mGPSBean.setLatituede(mBDLocation.getLatitude());
				mGPSBean.setLongitude(mBDLocation.getLongitude());
				mGPSBean.setSpeed(mBDLocation.getSpeed() * 3.6f);// 米每秒换成公里每小时
			}

		} else {
			if (mGoogleLocation != null) {

				mGPSBean.setAltitude(mGoogleLocation.getAltitude());
				mGPSBean.setBearing(mGoogleLocation.getBearing());
				mGPSBean.setLatituede(mGoogleLocation.getLatitude());
				mGPSBean.setLongitude(mGoogleLocation.getLongitude());
				mGPSBean.setSpeed(mGoogleLocation.getSpeed() * 3.6f);// 米每秒换成公里每小时
			}
		}
		// 速度提醒
		if (UserSetting.getSpeedAlarm() >= 0
				&& mGPSBean.getSpeed() > ConfigData.SPEEDS[UserSetting
						.getSpeedAlarm()]) {
			BatteryReceiver.Vibrate(mContext, 1000);
		}

		// 高度提醒
		if (UserSetting.getHeightAlarm() >= 0
				&& mGPSBean.getAltitude() > ConfigData.HEIGHTS[UserSetting
						.getHeightAlarm()]) {
			BatteryReceiver.Vibrate(mContext, 1000);
		}
	}

	// 自动gps先注销
	// 定义对位置变化的监听函数
	// LocationListener locationListener = new LocationListener() {
	//
	// @Override
	// public void onLocationChanged(Location location) {
	// mLocation = location;
	// updateUIToNewLocation();
	//
	// }
	//
	// @Override
	// public void onStatusChanged(String provider, int status, Bundle extras) {
	// if (ConfigData.isDebug) {
	// Log.d(TAG, "chaoyue gps status=" + status);
	//
	// switch (status) {
	// // GPS状态为可见时
	// case LocationProvider.AVAILABLE:
	// Log.d(TAG, "chaoyue 当前GPS状态为可见状态");
	// break;
	// // GPS状态为服务区外时
	// case LocationProvider.OUT_OF_SERVICE:
	// Log.d(TAG, "chaoyue 当前GPS状态为服务区外状态");
	// break;
	// // GPS状态为暂停服务时
	// case LocationProvider.TEMPORARILY_UNAVAILABLE:
	// Log.d(TAG, "chaoyue 当前GPS状态为暂停服务状态");
	// break;
	// }
	// }
	//
	// }
	//
	// @Override
	// public void onProviderEnabled(String provider) {
	// if (ConfigData.isDebug) {
	// Log.d(TAG, "chaoyue gps enable provider=" + provider);
	// }
	//
	// }
	//
	// @Override
	// public void onProviderDisabled(String provider) {
	// if (ConfigData.isDebug) {
	// Log.d(TAG, "chaoyue gps disable provider=" + provider);
	// }
	// }
	//
	// };

	/**
	 * 强制打开gps
	 */
	private void toggleGPS() {
		Intent gpsIntent = new Intent();
		gpsIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
		gpsIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(mContext, 0, gpsIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}

	private SensorEventListener mSensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:// 加速度
				break;
			case Sensor.TYPE_AMBIENT_TEMPERATURE:// 温度
				float temperature = event.values[SensorManager.DATA_X];
				mSensorBean.setTemperature(temperature);
				break;
			case Sensor.TYPE_GRAVITY:// 重心
				float x = event.values[SensorManager.DATA_X];
				float y = event.values[SensorManager.DATA_Y];
				float z = event.values[SensorManager.DATA_Z];
				mSensorBean.setGsenx(x);
				mSensorBean.setGseny(y);
				mSensorBean.setGsenz(z);
				break;
			case Sensor.TYPE_GYROSCOPE:// 陀螺仪
				float v = event.values[0];
				mSensorBean.setMsenv(v);
				break;
			case Sensor.TYPE_LIGHT:// 光

				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:// 线性加速度

				break;
			case Sensor.TYPE_MAGNETIC_FIELD:// 磁力传感器

				break;

			case Sensor.TYPE_PRESSURE:// 压力

				break;
			case Sensor.TYPE_RELATIVE_HUMIDITY: // 相对湿度传感器

				break;
			case Sensor.TYPE_PROXIMITY:// 近距离

				break;
			case Sensor.TYPE_ROTATION_VECTOR: // 翻转

				break;

			default:
				break;
			}

		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 百度 定位SDK监听函数
	 */
	public class MyBDLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			mBDLocation = location;
			updateUIToNewLocation();
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putDouble("latitude", mBDLocation.getLatitude());
			bundle.putDouble("longitude", mBDLocation.getLongitude());
			bundle.putFloat("radius", mBDLocation.getRadius());
			intent.putExtras(bundle);
			intent.setAction(BDACTION);
			mContext.sendBroadcast(intent);
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	private void setUpGoogleApiClientIfNeeded() {
		if (mGoogleApiClient == null) {
			mGoogleApiClient = new GoogleApiClient.Builder(mContext)
					.addApi(LocationServices.API)
					.addConnectionCallbacks(new ConnectionCallbacks() {

						@Override
						public void onConnectionSuspended(int arg0) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onConnected(Bundle arg0) {
							LocationServices.FusedLocationApi
									.requestLocationUpdates(mGoogleApiClient,
											REQUEST, new LocationListener() {

												@Override
												public void onLocationChanged(
														Location arg0) {
													mGoogleLocation = arg0;
													updateUIToNewLocation();

												}
											}); // LocationListener

						}
					})
					.addOnConnectionFailedListener(
							new OnConnectionFailedListener() {

								@Override
								public void onConnectionFailed(
										ConnectionResult arg0) {
									// TODO Auto-generated method stub

								}
							}).build();
		}
	}

	/**
	 * Gps状态监听
	 */
	private GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
			switch (event) {
			case GpsStatus.GPS_EVENT_FIRST_FIX: {
				// 第一次定位时间UTC gps可用
				if (ConfigData.isDebug) {
					Log.d(TAG, "chaoyue GPS_EVENT_FIRST_FIX");
				}
				int i = gpsStatus.getTimeToFirstFix();
				break;
			}

			case GpsStatus.GPS_EVENT_SATELLITE_STATUS: {// 周期的报告卫星状态
				// 得到所有收到的卫星的信息，包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
				Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();

				List<GpsSatellite> satelliteList = new ArrayList<GpsSatellite>();

				for (GpsSatellite satellite : satellites) {
					// 包括 卫星的高度角、方位角、信噪比、和伪随机号（及卫星编号）
					/*
					 * satellite.getElevation(); //卫星仰角 satellite.getAzimuth();
					 * //卫星方位角 satellite.getSnr(); //信噪比 satellite.getPrn();
					 * //伪随机数，可以认为他就是卫星的编号 satellite.hasAlmanac(); //卫星历书
					 * satellite.hasEphemeris(); satellite.usedInFix();
					 */
					satelliteList.add(satellite);
				}
				if (ConfigData.isDebug) {
					Log.d(TAG, "chaoyue GPS_EVENT_SATELLITE_STATUS");
				}
				break;
			}

			case GpsStatus.GPS_EVENT_STARTED: {
				if (ConfigData.isDebug) {
					Log.d(TAG, "chaoyue GPS_EVENT_STARTED");
				}
				break;
			}

			case GpsStatus.GPS_EVENT_STOPPED: {
				if (ConfigData.isDebug) {
					Log.d(TAG, "chaoyue GPS_EVENT_STOPPED");
				}
				break;
			}

			default:
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregister();
	}

	private void unregister() {
		// 自动gps先注销
		// mLocationManager.removeUpdates(locationListener);
		mLocationManager.removeGpsStatusListener(gpsStatusListener);
		stopTimer();
		mSensorManager.unregisterListener(mSensorEventListener);
		if (MainActivity.IN_CHINA == 1) {
			if (mBDLocClient != null) {
				mBDLocClient.stop();
			}
		} else {
			if (mGoogleApiClient != null) {
				mGoogleApiClient.disconnect();
			}
		}
	}

}
