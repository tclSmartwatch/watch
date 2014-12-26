package com.tcl.watch;

public class ConfigData {
	public static boolean isDebug = true;
	// 设置数据
	public static String[] sportModeStrings = new String[] { "步行模式", "骑车模式" };
	public static String[] saveIntervalStrings = new String[] { "10秒", "20秒",
			"30秒" };
	public static String[] uploadInterval = new String[] { "10分钟", "30分钟" };

	// 速度报警
	public static String[] speedAlarmStrings = new String[] { "20公里/时",
			"25公里/时", "30公里/时", "35公里/时", "40公里/时" };
	public static float[] SPEEDS = { 20.0f, 25.0f, 30.0f, 35.0f, 40.0f };
	public static boolean[] speedAlarmdefSel = { false, false, false, false,
			false };

	// 高度报警
	public static String[] heightAlarmStrings = new String[] { "0.5千米", "1千米",
			"1.5千米", "2千米", "2.5千米", "3千米" };
	public static float[] HEIGHTS = { 0.5f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f };
	public static boolean[] heightAlarmdefSel = { false, false, false, false,
			false, false };

	// 电量
	public static String[] powerAreas = new String[] { "20%", "10%" };
	public static boolean[] defSel = { false, false };
	// 是否打开服务
	public static int open = 0;

	public static final String SENSOR_URL = "http://10.128.210.147:8080/track/Track";
}
