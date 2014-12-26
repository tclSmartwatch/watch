package com.tcl.watch.data;


public class UserSetting {

	private final static String SPORT_MODE="sport_mode";
	private final static String DATASAVE_INTERVAL="datasaveInterval";
	private final static String DATAUPLOAD_INTERVAL="datauploadInterval";
	private final static String SPEED_ALARM="speedAlarm";
	private final static String HEIGHT_ALARM="heightAlarm";
	private final static String BATTERY_ALARM="batteryAlarm";
	private final static String HEARTRATE_ALARM="heartrateAlarm";
	
	//运动模式
	public static int getSportMode() {
		return Settings.getInt(SPORT_MODE, 0);
	}

	public static void putSportMode(int mode) {
		Settings.putInt(SPORT_MODE, mode);
	}
	
	//保存时间
	public static int getDatasaveInterval() {
		return Settings.getInt(DATASAVE_INTERVAL, 0);
	}
	
	public static void putDatasaveInterval(int interval) {
		Settings.putInt(DATASAVE_INTERVAL, interval);
	}
	
	//上传时间
	public static int getUploadsaveInterval() {
		return Settings.getInt(DATAUPLOAD_INTERVAL, 0);
	}
	
	public static void putUploadsaveInterval(int interval) {
		Settings.putInt(DATAUPLOAD_INTERVAL, interval);
	}
	
	//速度报警
	public static int getSpeedAlarm() {
		return Settings.getInt(SPEED_ALARM, 0);
	}
	
	public static void putSpeedAlarm(int speed) {
		Settings.putInt(SPEED_ALARM, speed);
	}
	
	//高度报警
	public static int getHeightAlarm() {
		return Settings.getInt(HEIGHT_ALARM, 0);
	}
	
	public static void putHeightAlarm(int height) {
		Settings.putInt(HEIGHT_ALARM, height);
	}
	
	//电量提醒
	public static int getBatteryAlarm() {
		return Settings.getInt(BATTERY_ALARM, 0);
	}
	
	public static void putBatteryAlarm(int value) {
		Settings.putInt(BATTERY_ALARM, value);
	}
	
	//心率报警
	public static int getHeartrateAlarm() {
		return Settings.getInt(HEARTRATE_ALARM, 0);
	}
	
	public static void putHeartrateAlarm(int heartrate) {
		Settings.putInt(HEARTRATE_ALARM, heartrate);
	}
}