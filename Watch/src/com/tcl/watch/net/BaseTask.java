package com.tcl.watch.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.HttpResponseException;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.json.JSONObject;

import com.tcl.watch.ConfigData;

import android.content.Context;
import android.util.Log;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import net.tsz.afinal.http.HttpHandler;

public abstract class BaseTask {

	private static final String TAG = BaseTask.class.getName();
	boolean mIsStop = false;
	Context mContext;
	private volatile boolean mIsCancel = false;

	private HttpHandler downloadHandler;

	public static final int NET_ERROR_CODE = -1;//

	public void get(AjaxParams params) {
		FinalHttp fh = getHttpAddHeader();
		getBack(fh, params);
	}

	public void get(SSLSocketFactory socketFactory, AjaxParams params) {
		FinalHttp fh = getHttpAddHeader();
		fh.configSSLSocketFactory(socketFactory);
		getBack(fh, params);
	}

	public void get(SocketFactory socketFactory, AjaxParams params) {
		FinalHttp fh = getHttpAddHeader();
		fh.configSSLSocketFactory(socketFactory);
		getBack(fh, params);
	}

	private void getBack(FinalHttp fh, AjaxParams params) {
		if (mIsCancel) {
			doCancel();
			return;
		}
		fh.get(getUrl(), params, new AjaxCallBack<String>() {

			@Override
			public void onLoading(long count, long current) {

			}

			@Override
			public void onSuccess(String t) {

				if (ConfigData.isDebug) {
					Log.d(TAG, "response url = " + getUrl());
					Log.d(TAG, "response = " + t);
				}
				t = transfer(t);
				doSuccess(t);
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (ConfigData.isDebug) {
					Log.d(TAG, "response url = " + getUrl());
					Log.d(TAG, "response fail= " + t + "str=" + strMsg);
				}

				doFailure(t, strMsg);
			}
		});
	}

	public void post(JSONObject object) {
		if (mIsCancel) {
			doCancel();
			return;
		}
		FinalHttp fh = getHttpAddHeader();
		try {
			fh.post(getUrl(), object, new AjaxCallBack<String>() {

				@Override
				public void onLoading(long count, long current) {
					updateUI(count, current);
				}

				@Override
				public void onSuccess(String t) {
					if (ConfigData.isDebug) {
						Log.d(TAG, "response url = " + getUrl());
						Log.d(TAG, "response = " + t);
					}
					t = transfer(t);
					doSuccess(t);
				}

				@Override
				public void onStart() {
				}

				@Override
				public void onFailure(Throwable t, String strMsg) {
					if (ConfigData.isDebug) {
						Log.d(TAG, "response url = " + getUrl());
						Log.d(TAG, "response fail= " + t + "------str="
								+ strMsg);
					}
					doFailure(t, strMsg);
				}
			});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void post(AjaxParams params) {
		if (mIsCancel) {
			doCancel();
			return;
		}
		FinalHttp fh = getHttpAddHeader();
		fh.post(getUrl(), params, new AjaxCallBack<String>() {

			@Override
			public void onLoading(long count, long current) {
				updateUI(count, current);
			}

			@Override
			public void onSuccess(String t) {
				if (ConfigData.isDebug) {
					Log.d(TAG, "response url = " + getUrl());
					Log.d(TAG, "response = " + t);
				}
				t = transfer(t);
				doSuccess(t);
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onFailure(Throwable t, String strMsg) {
				if (ConfigData.isDebug) {
					Log.d(TAG, "response url = " + getUrl());
					Log.d(TAG, "response fail= " + t + "------str=" + strMsg);
				}
				doFailure(t, strMsg);
			}
		});

	}

	/**
	 * 是否断点续传
	 */
	public void download(boolean isContinue) {
		FinalHttp fh = new FinalHttp();
		downloadHandler = fh.download(getUrl(), //
				getPath(), //
				isContinue,// 是否断点
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {

						updateUI(count, current);
					}

					@Override
					public void onSuccess(File t) {
						if (!mIsStop)
							downLoadSuccess(t);
					}

					@Override
					public void onStart() {
						if (ConfigData.isDebug) {
							Log.d(TAG, "response download start");
						}
					}

					@Override
					public void onFailure(Throwable t, String strMsg) {
						doFailure(t, strMsg);

					}
				});

	}

	/**
	 * 下载成功路径
	 */
	void downLoadSuccess(File t) {

	}

	/**
	 * 
	 * 
	 * @return
	 */
	String getPath() {
		return null;
	}

	/**
	 * 
	 * 
	 * @param isCancel
	 */
	public void setCancel(boolean isCancel) {
		mIsCancel = isCancel;
	}

	/**
	 * 
	 * 
	 * @param s
	 * @return
	 */
	String transfer(String s) {
		s = s.replace("&quot;", "\\\"");
		s = s.replace("&lt;", "<");
		s = s.replace("&gt;", ">");
		s = s.replace("&amp;", "&");
		s = s.replace("&#036;", "'");
		s = s.replace("&nbsp;", " ");
		return s;
	}

	/**
	 * url
	 * 
	 * @param code
	 */
	abstract String getUrl();

	/**
	 * 成功
	 * 
	 * @param result
	 */
	abstract void doSuccess(String result);

	/**
	 * 失败
	 * 
	 * @param t
	 * @param strMsg
	 */
	abstract void doFailure(Throwable t, String strMsg);

	/**
	 * 取消
	 */
	abstract void doCancel();

	abstract void updateUI(long count, long current);

	public void stop() {
		mIsStop = true;
		if (downloadHandler != null)
			downloadHandler.stop();
	}

	public HttpHandler getDownloadHandler() {
		return downloadHandler;
	}

	public void setDownloadHandler(HttpHandler downloadHandler) {
		this.downloadHandler = downloadHandler;
	}

	public static int getStatusCode(Throwable t) {
		int httpcode = NET_ERROR_CODE;
		if (HttpResponseException.class.isInstance(t)) {
			HttpResponseException httpResponseException = (HttpResponseException) t;
			httpcode = httpResponseException.getStatusCode();
		}
		return httpcode;
	}

	public FinalHttp getHttpAddHeader() {
		FinalHttp fh = new FinalHttp();
		// fh.addHeader("token", "1");
		// fh.addHeader("uid", "1");
		return fh;
	}
}
