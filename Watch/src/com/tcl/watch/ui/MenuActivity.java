package com.tcl.watch.ui;

import com.tcl.watch.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends BaseActivity {
	
	private Button settingButton, histdataButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		
		//设置按钮
		settingButton = (Button)findViewById(R.id.setting);
		//历史数据按钮
		histdataButton = (Button)findViewById(R.id.histdata);
		
		settingButton.setOnClickListener(new OnClickListener() {
			//监听设置按钮
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MenuActivity.this, SettingActivity.class);
				MenuActivity.this.startActivity(intent);
			}
		});
		
		histdataButton.setOnClickListener(new OnClickListener() {
			//监听历史数据按钮
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MenuActivity.this, ShowHistoricDataActivity.class);
				MenuActivity.this.startActivity(intent);
			}
		});
	}
}
