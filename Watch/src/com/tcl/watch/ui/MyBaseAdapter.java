package com.tcl.watch.ui;

import java.util.ArrayList;

import com.tcl.watch.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyBaseAdapter<T> extends BaseAdapter {

	ArrayList<T> mArraylist;
	Context mContext;

	public MyBaseAdapter(Context mContext, ArrayList<T> mArraylist) {
		this.mContext = mContext;
		this.mArraylist = mArraylist;
	}

	@Override
	public int getCount() {
		if (mArraylist != null) {
			return mArraylist.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		if (null == mArraylist || arg0 >= mArraylist.size())
			return null;
		return mArraylist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return arg1;
	}

	public void setArrayList(ArrayList<T> arrayList) {
		this.mArraylist = arrayList;
		this.notifyDataSetChanged();
	}
}
