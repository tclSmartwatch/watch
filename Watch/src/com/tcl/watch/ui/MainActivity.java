package com.tcl.watch.ui;

import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.http.AjaxParams;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.tcl.watch.ConfigData;
import com.tcl.watch.FileReadWrite;
import com.tcl.watch.R;
import com.tcl.watch.bean.GPSBean;
import com.tcl.watch.data.Settings;
import com.tcl.watch.logic.DataService;
import com.tcl.watch.net.GPSTask;
import com.tcl.watch.net.SensorTask;

public class MainActivity extends BaseActivity {

	private static final String TAG = "MainActivity";
	private Context mContext;

	private Button menuButton, switcherButton, exitButton;
	private static final int START = 1;
	private static final int STOP = 2;
	public static int IN_CHINA = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		Settings.init(this);
		SDKInitializer.initialize(this.getApplication());
		// 判别本机的语言环境
		String language = mContext.getResources().getConfiguration().locale
				.getLanguage();
		if (language.equals("zh")) {
			IN_CHINA = 1;
		} else {
			IN_CHINA = 0;
		}

		menuButton = (Button) findViewById(R.id.menu);
		switcherButton = (Button) findViewById(R.id.switcher);
		exitButton = (Button) findViewById(R.id.exit);

		menuButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MenuActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});

		switcherButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent2 = new Intent(mContext, ShowActivity.class);
				mContext.startActivity(intent2);

			}
		});

		exitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				exictAll();
			}
		});

		// new SensorTask().post(new AjaxParams());
		// 查找超出时间界限的数据
		FinalDb finalDb = FinalDb.create(mContext);
		finalDb.deleteByWhere(GPSBean.class,
				"DATE(dates) = DATE('now','-1 months','localtime')");

	}

	// 当前日期

	public static String getNowTime(String dateformat) {
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);// 可以方便地修改日期格式
		String currentDate = dateFormat.format(now);
		return currentDate;
	}

	// 当前星期
	public static int getWeek() {

		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

	}

	/**
	 * 字符串的日期格式的计算
	 */
	public static int daysBetween(String oldDate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(oldDate));
		long oldTime = cal.getTimeInMillis();
		cal.setTime(new java.util.Date());
		long newTime = cal.getTimeInMillis();
		long between_days = (newTime - oldTime) / (1000 * 60 * 60 * 24);
		return Integer.parseInt(String.valueOf(between_days));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { // 按下的如果是BACK，同时没有重复
			saveLog();
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void saveLog() {
		if (GPSTask.sentLog.length() != 0) {

			String dir = null;
			if (dir == null) {
				dir = FileReadWrite.isCacheFileIsExit(mContext,
						ConfigData.SENT_DIR);
			}
			final String CRASH_NAME = "yyyyMMddkkmmss";
			CharSequence timestamp = DateFormat.format(CRASH_NAME,
					System.currentTimeMillis());
			String filename = dir + "/" + timestamp;
			try {
				FileReadWrite.saveFile(new File(filename),
						GPSTask.sentLog.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
