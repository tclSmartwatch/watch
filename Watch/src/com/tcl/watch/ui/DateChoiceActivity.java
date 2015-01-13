package com.tcl.watch.ui;

import java.util.ArrayList;

import net.tsz.afinal.FinalDb;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.android.gms.internal.bu;
import com.tcl.watch.R;
import com.tcl.watch.bean.ControlBean;

public class DateChoiceActivity extends BaseActivity {

	private ArrayList<ControlBean> controlBeanlist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_listview);
		ListView listView = (ListView) findViewById(R.id.lv_date);
		FinalDb finalDb = FinalDb.create(this);
		controlBeanlist = (ArrayList<ControlBean>) finalDb.findAllByWhere(
				ControlBean.class,
				"1=1 order by stopdate desc limit 50");
		DateChoiceAdapter dateChoiceAdapter = new DateChoiceAdapter(this,
				controlBeanlist);
		listView.setAdapter(dateChoiceAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ControlBean controlBean = controlBeanlist.get(position);
				Intent intent=new Intent(DateChoiceActivity.this,HistoryLineActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("start", controlBean.getStartDate());
				bundle.putString("stop", controlBean.getStopDate());
				intent.putExtras(bundle);
				DateChoiceActivity.this.startActivity(intent);
			}
		});

	}
}
