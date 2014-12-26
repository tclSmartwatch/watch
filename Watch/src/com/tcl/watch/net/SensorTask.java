package com.tcl.watch.net;

import com.tcl.watch.ConfigData;

public class SensorTask extends BaseTask {

	@Override
	String getUrl() {
		// TODO Auto-generated method stub
		return ConfigData.SENSOR_URL;
	}

	@Override
	void doSuccess(String result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void doFailure(Throwable t, String strMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void doCancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void updateUI(long count, long current) {
		// TODO Auto-generated method stub
		
	}

}
