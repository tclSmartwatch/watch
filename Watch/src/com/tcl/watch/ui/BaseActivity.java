package com.tcl.watch.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BaseActivity extends Activity {
	private static final String EXICT_ALL="exict_all";
	protected BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction(EXICT_ALL);
		this.registerReceiver(this.broadcastReceiver, filter);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(this.broadcastReceiver);
	}
	
	protected void exictAll() {
		 Intent intent = new Intent();
         intent.setAction(EXICT_ALL);
         this.sendBroadcast(intent);
         super.finish();
	}

}
