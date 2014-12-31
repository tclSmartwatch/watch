package com.tcl.watch.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcl.watch.R;
import com.tcl.watch.bean.ControlBean;
import com.tcl.watch.bean.SensorBean;

public class DateChoiceAdapter extends MyBaseAdapter<ControlBean> {

	// 得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;

	public DateChoiceAdapter(Context mContext, ArrayList<ControlBean> mArraylist) {
		super(mContext, mArraylist);
		this.mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {

		ViewHolder holder;

		if (arg1 == null) {
			arg1 = mInflater.inflate(R.layout.activity_textview, null);
			holder = new ViewHolder();

			/* 得到控件的对象 */
			holder.text = (TextView) arg1.findViewById(R.id.historicdata0);

			arg1.setTag(holder);// 绑定ViewHolder对象
		} else {
			holder = (ViewHolder) arg1.getTag();// 取出ViewHolder对象
		}

		holder.text.setText("服务启动时间：" + mArraylist.get(arg0).getStartDate()
				+ " 结束时间：" + mArraylist.get(arg0).getStopDate());

		return arg1;
	}

	public final class ViewHolder {
		public TextView text;
	}
}
