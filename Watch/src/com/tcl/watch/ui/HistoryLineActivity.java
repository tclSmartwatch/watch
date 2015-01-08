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
import android.widget.RelativeLayout;
import com.tcl.watch.R;
import com.tcl.watch.bean.SensorBean;

public class HistoryLineActivity extends BaseActivity {
	Context mContext;
	private String start;
	private String stop;
	private ArrayList<SensorBean> list;
	private MyMap myMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext=this;
		setContentView(R.layout.activity_history_line);
		Button button = (Button) findViewById(R.id.b_detail);
		RelativeLayout rlLayout=(RelativeLayout) findViewById(R.id.rl_history_map);
		myMap = new MyMap(mContext, rlLayout);
		Bundle bundle = getIntent().getExtras();
		start = bundle.getString("start");
		stop = bundle.getString("stop");
		FinalDb finalDb = FinalDb.create(mContext);
		list = (ArrayList<SensorBean>) finalDb.findAllByWhere(SensorBean.class,
				"dates >= '" + start + "' and dates <= '" + stop
						+ "' order by dates desc limit 100");
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
		super.onResume();
		myMap.onResume();
	}
	
	@Override
	protected void onPause() {
		myMap.onPause();
		super.onPause();
	}
	

	
}
