package com.tcl.watch.ui;

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;

import com.tcl.watch.R;
import com.tcl.watch.bean.SensorBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

public class ShowHistoricDataActivity extends Activity {

	private Context mContext;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_show_historic_data);
		Bundle bundle = getIntent().getExtras();
		String start = bundle.getString("start");
		String stop = bundle.getString("stop");
		FinalDb finalDb = FinalDb.create(mContext);
		ArrayList<SensorBean> list = (ArrayList<SensorBean>) finalDb
				.findAllByWhere(SensorBean.class, "dates >= '" + start
						+ "' and dates <= '" + stop
						+ "' order by dates desc limit 100");
		SensorAdapter sensorAdapter = new SensorAdapter(mContext, list);

		mListView = (ListView) findViewById(R.id.historicData);
		mListView.setAdapter(sensorAdapter);
	}

}
