package com.tcl.watch.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalDb;

import com.baidu.android.bbalbs.common.a.a;
import com.tcl.watch.ConfigData;
import com.tcl.watch.bean.GPSBean;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class GPSTask {
	private static final String TAG = GPSTask.class.getName();
	Context mContext;
	GPSBean mGpsBean;
	Socket socket = null;
	BufferedWriter writer = null;
	BufferedReader reader = null;
	String buffer = "";
	private static final String HOST = "10.128.210.117";
	private static final int PORT = 31272;
	public static StringBuffer sentLog = new StringBuffer();

	public GPSTask(Context context, GPSBean gpsBean) {
		this.mContext = context;
		this.mGpsBean = gpsBean;
		connect();
	}

	public void connect() {

		AsyncTask<GPSBean, Void, Void> read = new AsyncTask<GPSBean, Void, Void>() {

			@Override
			protected Void doInBackground(GPSBean... arg0) {
				GPSBean gpsBean = arg0[0];
				String gps = null;
				String date = gpsBean.getDate();
				StringBuffer dateBuffer = new StringBuffer();
				dateBuffer.append(date.substring(2, 3))
						.append(date.substring(5, 6))
						.append(date.substring(8, 9))
						.append(date.substring(11, 12))
						.append(date.substring(14, 15))
						.append(date.substring(17, 18));
				double lat = gpsBean.getLatituede();
				double lon = gpsBean.getLongitude();
				double altitude = gpsBean.getAltitude();
				String NS = (lat > 0) ? "N" : "S";
				String EW = (lon > 0) ? "E" : "W";
				gps = "imei:123456789098765,help me!," + dateBuffer.toString()
						+ ",97355551234,F,-1,A," + lat + "," + NS + "," + lon
						+ "," + EW + "," + altitude;
				if (ConfigData.isDebug) {
					Log.d(TAG, "chaoyue gps=" + gps);
				}
				try {
					socket = new Socket(HOST, PORT);
					if (socket.isClosed() == false
							&& socket.isConnected() == true) {
						if (ConfigData.isDebug) {
							Log.d(TAG, "chaoyue socket is conncecting");
						}
					} else {
						socket.connect(new InetSocketAddress(HOST, PORT));
					}
					writer = new BufferedWriter(new OutputStreamWriter(
							socket.getOutputStream()));
					reader = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					writer.write(gps);
					writer.flush();
					// 读取发来服务器信息
					String line = null;
					while ((line = reader.readLine()) != null) {
						buffer = buffer + line;
					}

				} catch (UnknownHostException e1) {
					buffer = e1.toString();
				} catch (IOException e1) {
					buffer = e1.toString();
				} finally {
					if (buffer.equals("")) {// 这个判断条件是不靠谱，目前数据发送成功失败服务器都没有发送数据
						gpsBean.setUpload(1);
						FinalDb finalDb = FinalDb.create(mContext);
						finalDb.update(gpsBean);
						sentLog.append(gpsBean.toString() + ",success\n");
					} else {
						sentLog.append(gpsBean.toString() + "," + buffer + "\n");
					}
				}

				return null;
			}

		};
		read.execute(mGpsBean);

	}

}
