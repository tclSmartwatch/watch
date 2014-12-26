package com.tcl.watch.ui;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tcl.watch.R;
import com.tcl.watch.bean.SensorBean;

public class SensorAdapter extends MyBaseAdapter<SensorBean> {

	// 得到一个LayoutInfalter对象用来导入布局
	private LayoutInflater mInflater;
	StringBuffer stringBuffer = new StringBuffer();

	public SensorAdapter(Context mContext, ArrayList<SensorBean> mArraylist) {
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

		/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
		stringBuffer.setLength(0);

		stringBuffer
				.append(mArraylist.get(arg0).getDate())
				.append("\n")
				.append(ShowActivity.getLatituede(mArraylist.get(arg0)
						.getLatituede()))
				.append("	")
				.append(ShowActivity.getLongtitude(mArraylist.get(arg0)
						.getLongitude())).append("\n").append("海拔：")
				.append(mArraylist.get(arg0).getAltitude()).append("米 ")
				.append("方向：").append(mArraylist.get(arg0).getBearing())
				.append(" ").append("速度：")
				.append(mArraylist.get(arg0).getSpeed()).append("km/h\n")
				.append("角速度：").append(mArraylist.get(arg0).getMsenv())
				.append("rad/s\n\n");

		holder.text.setText(stringBuffer.toString());

		return arg1;
	}

	public final class ViewHolder {
		public TextView text;
	}
}
