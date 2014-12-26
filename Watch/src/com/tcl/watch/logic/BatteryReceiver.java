package com.tcl.watch.logic;

import com.tcl.watch.data.UserSetting;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

public class BatteryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		// 获取当前电量
		int current = bundle.getInt("level");
		// 获取总电量　
		int total = bundle.getInt("scale");
		int battery = UserSetting.getBatteryAlarm();
		if ((battery & 1) != 0 && (current * 1.0 / total == 0.2)
				|| (battery & 2) != 0 && (current * 1.0 / total == 0.1)) {
			Vibrate(context, 1000);
		}

	}

	public static void Vibrate(final Context context, long milliseconds) {
		Vibrator vib = (Vibrator) context
				.getSystemService(Service.VIBRATOR_SERVICE);
		vib.vibrate(milliseconds);
	}
}
