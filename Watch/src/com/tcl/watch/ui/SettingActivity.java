package com.tcl.watch.ui;

import java.util.List;

import com.tcl.watch.ConfigData;
import com.tcl.watch.R;
import com.tcl.watch.data.UserSetting;
import com.tcl.watch.logic.DataService;

import android.R.integer;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingActivity extends BaseActivity implements OnClickListener {

	private final static String TAG = SettingActivity.class.getName();
	// setting button
	private Button movemodeButton, saveintervalButton, uploadintervalButton;
	private Button speedalarmButton, heightalarmButton, batteryalarmButton,
			heartratealarmButton;
	// setting value
	private static Button movemodevalueButton;
	private static Button saveintervalvalueButton;
	private static Button uploadintervalvalueButton;
	private static Button speedalarmvalueButton, heightalarmvalueButton,
			batteryalarmvalueButton, heartratealarmvalueButton;

	// setting index
	public enum Index {
		MODE, SAVE_INTERVAL, UPLOAD_INTERVAL, SPEED_ALARM, HEIGHT_ALARM, BATT_ALARM, HEARTRATE_ALARM
	}

	private SeekBar seekBar;
	private TextView description;
	private LayoutInflater myLayoutInflater;
	private View seekbarView;
	int sportWhich, saveInterval, uploadInterval, heartrateAlarm;
	int speedAlarm = 0, heightAlarm = 0, batteryAlarm = 0;
	Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		mContext = this;
		movemodeButton = (Button) findViewById(R.id.movemodeButton);
		saveintervalButton = (Button) findViewById(R.id.saveintervalButton);
		uploadintervalButton = (Button) findViewById(R.id.uploadintervalButton);
		speedalarmButton = (Button) findViewById(R.id.speedalarmButton);
		heightalarmButton = (Button) findViewById(R.id.heightalarmButton);
		batteryalarmButton = (Button) findViewById(R.id.batteryalarmButton);
		heartratealarmButton = (Button) findViewById(R.id.heartratealarmButton);

		movemodevalueButton = (Button) findViewById(R.id.modevalue);
		saveintervalvalueButton = (Button) findViewById(R.id.saveintervalvalue);
		uploadintervalvalueButton = (Button) findViewById(R.id.uploadintervalvalue);
		speedalarmvalueButton = (Button) findViewById(R.id.speedalarmvalue);
		heightalarmvalueButton = (Button) findViewById(R.id.heightalarmvalue);
		batteryalarmvalueButton = (Button) findViewById(R.id.batteryalarmvalue);
		heartratealarmvalueButton = (Button) findViewById(R.id.heartratealarmvalue);
		getSettingValue();
		
		myLayoutInflater = LayoutInflater.from(SettingActivity.this);
		seekbarView = myLayoutInflater.inflate(R.layout.activity_seekbar, null);
		seekBar = (SeekBar) seekbarView.findViewById(R.id.seekBar);
		description = (TextView) seekbarView.findViewById(R.id.description);
		movemodeButton.setOnClickListener(this);
		saveintervalButton.setOnClickListener(this);
		uploadintervalButton.setOnClickListener(this);
		speedalarmButton.setOnClickListener(this);
		heightalarmButton.setOnClickListener(this);
		batteryalarmButton.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			// 拖动条停止拖动的时候调用
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			// 拖动条开始拖动的时候调用
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			// 拖动条进度改变的时候调用
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				heartrateAlarm = progress + 50;
				description.setText("Heart Rate：" + (heartrateAlarm) + "bpm");
			}
		});
		heartratealarmButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.movemodeButton:
			sportWhich = UserSetting.getSportMode();
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("运动模式设置")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(ConfigData.sportModeStrings,
							sportWhich, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (ConfigData.isDebug) {
										Log.d(TAG, "chaoyue which=" + which);
									}
									sportWhich = which;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									UserSetting.putSportMode(sportWhich);
									if (isServiceRunning(mContext,
											DataService.TAG)) {
										Intent intent = new Intent(mContext,
												DataService.class);
										mContext.startService(intent);
									}
									updateSettingValue(
											Index.MODE,
											ConfigData.sportModeStrings[sportWhich]);
								}

							}).setNegativeButton("取消", null).show();
			break;

		case R.id.saveintervalButton:
			saveInterval = UserSetting.getDatasaveInterval();
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("保存时间设置")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(ConfigData.saveIntervalStrings,
							saveInterval,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									saveInterval = which;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									UserSetting
											.putDatasaveInterval(saveInterval);
									if (isServiceRunning(mContext,
											DataService.TAG)) {
										if (ConfigData.isDebug) {
											Log.d(TAG,
													"chaoyue service has started");
										}
										Intent intent = new Intent(mContext,
												DataService.class);
										mContext.startService(intent);
									}
									updateSettingValue(
											Index.SAVE_INTERVAL,
											ConfigData.saveIntervalStrings[saveInterval]);
								}
							}).setNegativeButton("取消", null).show();
			break;

		case R.id.uploadintervalButton:
			uploadInterval = UserSetting.getUploadsaveInterval();
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("上传时间设置")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(ConfigData.uploadInterval,
							uploadInterval,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									uploadInterval = which;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									UserSetting
											.putUploadsaveInterval(uploadInterval);
									if (isServiceRunning(mContext,
											DataService.TAG)) {
										Intent intent = new Intent(mContext,
												DataService.class);
										mContext.startService(intent);
									}
									updateSettingValue(
											Index.UPLOAD_INTERVAL,
											ConfigData.uploadInterval[uploadInterval]);
								}
							}).setNegativeButton("取消", null).show();
			break;

		case R.id.speedalarmButton:
			speedAlarm = UserSetting.getSpeedAlarm();
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("速度报警设置")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(ConfigData.speedAlarmStrings,
							speedAlarm, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									speedAlarm = which;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									UserSetting.putSpeedAlarm(speedAlarm);
									if (isServiceRunning(mContext,
											DataService.TAG)) {
										Intent intent = new Intent(mContext,
												DataService.class);
										mContext.startService(intent);
									}
									updateSettingValue(
											Index.SPEED_ALARM,
											ConfigData.speedAlarmStrings[speedAlarm]);
								}
							}).setNegativeButton("取消", null).show();
			break;

		case R.id.heightalarmButton:
			heightAlarm = UserSetting.getHeightAlarm();
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("高度报警设置")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(ConfigData.heightAlarmStrings,
							heightAlarm, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									heightAlarm = which;
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									UserSetting.putHeightAlarm(heightAlarm);
									if (isServiceRunning(mContext,
											DataService.TAG)) {
										Intent intent = new Intent(mContext,
												DataService.class);
										mContext.startService(intent);
									}
									updateSettingValue(
											Index.HEIGHT_ALARM,
											ConfigData.heightAlarmStrings[heightAlarm]);
								}
							}).setNegativeButton("取消", null).show();
			break;

		case R.id.batteryalarmButton:
			batteryAlarm = UserSetting.getBatteryAlarm();
			int[] contract2 = { 1, 2 };
			for (int i = 0; i < contract2.length; i++) {
				if ((batteryAlarm & contract2[i]) == 0) {
					ConfigData.defSel[i] = false;
				} else {
					ConfigData.defSel[i] = true;
				}
			}
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("电量提醒设置")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setMultiChoiceItems(ConfigData.powerAreas,
							ConfigData.defSel,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton, boolean isChecked) {
									if (isChecked) {
										batteryAlarm |= (1 << whichButton);
									} else {
										batteryAlarm &= ~(1 << whichButton);
									}

									if (ConfigData.isDebug) {
										Log.d(TAG, "lillian whichButton="
												+ whichButton);
										Log.d(TAG, "lillian isChecked="
												+ isChecked);
										Log.d(TAG, "lillian batteryAlarm="
												+ batteryAlarm);
									}
								}
							})
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									UserSetting.putBatteryAlarm(batteryAlarm);
									if (isServiceRunning(mContext,
											DataService.TAG)) {
										Intent intent = new Intent(mContext,
												DataService.class);
										mContext.startService(intent);
									}
									
									if (batteryAlarm > 2) {
										updateSettingValue(Index.BATT_ALARM,
												(ConfigData.powerAreas[0] + ", " + ConfigData.powerAreas[1]));
									} else {
										updateSettingValue(Index.BATT_ALARM,
												ConfigData.powerAreas[batteryAlarm >> 1]);
									}
								}
							}).setNegativeButton("取消", null).show();
			break;

		case R.id.heartratealarmButton:
			ViewGroup parent = (ViewGroup) seekbarView.getParent();
			if (parent != null) {
				parent.removeView(seekbarView);
			}
			seekBar.setProgress(UserSetting.getHeartrateAlarm() - 50);
			new AlertDialog.Builder(SettingActivity.this)
					.setTitle("心率报警设置")
					.setView(seekbarView)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									UserSetting
											.putHeartrateAlarm(heartrateAlarm);
									if (isServiceRunning(mContext,
											DataService.TAG)) {
										Intent intent = new Intent(mContext,
												DataService.class);
										mContext.startService(intent);
									}
									updateSettingValue(Index.HEARTRATE_ALARM,
											("" + UserSetting
													.getHeartrateAlarm()));
								}
							}).setNegativeButton("取消", null).show();
			break;

		default:
			break;
		}

	}

	public static void getSettingValue() {
		if (UserSetting.getSportMode() > 1) {
			movemodevalueButton.setText(ConfigData.sportModeStrings[0]);
		} else {
			movemodevalueButton.setText(ConfigData.sportModeStrings[UserSetting.getSportMode()]);
		}
		
		if (UserSetting.getDatasaveInterval() > 2) {
			saveintervalvalueButton.setText(ConfigData.saveIntervalStrings[0]);
		} else {
			saveintervalvalueButton.setText(ConfigData.saveIntervalStrings[UserSetting.getDatasaveInterval()]);
		}
		
		if (UserSetting.getUploadsaveInterval() > 1) {
			uploadintervalvalueButton.setText(ConfigData.uploadInterval[0]);
		} else {
			uploadintervalvalueButton.setText(ConfigData.uploadInterval[UserSetting.getUploadsaveInterval()]);
		}
		
		if (UserSetting.getSpeedAlarm() > 4) {
			speedalarmvalueButton.setText(ConfigData.speedAlarmStrings[0]);
		} else {
			speedalarmvalueButton.setText(ConfigData.speedAlarmStrings[UserSetting.getSpeedAlarm()]);
		}
		
		if (UserSetting.getHeightAlarm() > 5) {
			heightalarmvalueButton.setText(ConfigData.heightAlarmStrings[0]);
		} else {
			heightalarmvalueButton.setText(ConfigData.heightAlarmStrings[UserSetting.getHeightAlarm()]);
		}
		
		if (UserSetting.getBatteryAlarm() > 2) {
			batteryalarmvalueButton.setText(ConfigData.powerAreas[0] + ", " + ConfigData.powerAreas[1]);
		} else {
			batteryalarmvalueButton.setText(ConfigData.powerAreas[UserSetting.getBatteryAlarm() >> 1]);
		}
		
		heartratealarmvalueButton.setText("" + UserSetting.getHeartrateAlarm());
	}
	
	public static void updateSettingValue(Index index, String valueString) {
		switch (index) {
		case MODE:
			movemodevalueButton.setText(valueString);
			break;

		case SAVE_INTERVAL:
			saveintervalvalueButton.setText(valueString);
			break;

		case UPLOAD_INTERVAL:
			uploadintervalvalueButton.setText(valueString);
			break;

		case SPEED_ALARM:
			speedalarmvalueButton.setText(valueString);
			break;

		case HEIGHT_ALARM:
			heightalarmvalueButton.setText(valueString);
			break;

		case BATT_ALARM:
			Log.d(TAG, "lillian BATT_ALARM" + valueString);
			batteryalarmvalueButton.setText(valueString);

			break;

		case HEARTRATE_ALARM:
			heartratealarmvalueButton.setText(valueString);
			break;

		default:
			break;
		}
	}

	public static boolean isServiceRunning(Context context, String serviceName) {
		ActivityManager manager = (ActivityManager) context
				.getSystemService(SettingActivity.ACTIVITY_SERVICE);
		List<RunningServiceInfo> infos = manager.getRunningServices(100);
		for (RunningServiceInfo info : infos) {
			if (ConfigData.isDebug) {
				Log.d(TAG,
						"chaoyue service name=" + info.service.getClassName());
			}
			if (info.service.getClassName().equals(serviceName)) {
				return true;
			}

		}
		return false;

	}
}
