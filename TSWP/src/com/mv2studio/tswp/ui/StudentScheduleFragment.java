package com.mv2studio.tswp.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mv2studio.tswp.R;
import com.mv2studio.tswp.core.NotificationService;
import com.mv2studio.tswp.db.Db;
import com.mv2studio.tswp.model.TClass;
import com.mv2studio.tswp.model.TClass.State;

public class StudentScheduleFragment extends BaseFragment {

	private Context context;
	private int[] days = { R.string.day_1, R.string.day_2, R.string.day_3, R.string.day_4, R.string.day_5, R.string.day_6, R.string.day_7 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		View l = inflater.inflate(R.layout.fragment_student_main, null);
		ListView list = (ListView) l.findViewById(R.id.fragment_student_main_list);

		final List<TClass> classes = new Db(context).getAllClasses();
		final ScheduleAdapter adapter = new ScheduleAdapter(context, 0, classes);
		list.setAdapter(adapter);
		final Handler h = new Handler();
		final Runnable r = new Runnable() {
			
			@Override
			public void run() {
				for (TClass clas : classes) {
					clas.setState(State.NUL);
				}

				for (int i = 0; i < classes.size(); i++) {
					TClass cl = classes.get(i);
					Date start = cl.getStart();
					Date end = cl.getEnd();
					
					Calendar ee = Calendar.getInstance();
					ee.setTime(end);
					
					Calendar ss = Calendar.getInstance();
					ss.setTime(start);
					
					Calendar cStart = Calendar.getInstance();
					cStart.setTime(new Date());
					cStart.set(Calendar.WEEK_OF_MONTH, cStart.get(Calendar.WEEK_OF_MONTH));
					cStart.set(Calendar.HOUR_OF_DAY, start.getHours());
					cStart.set(Calendar.MINUTE, start.getMinutes());
					cStart.set(Calendar.DAY_OF_WEEK, ss.get(Calendar.DAY_OF_WEEK));

					Calendar cEnd = Calendar.getInstance();
					cEnd.setTime(new Date());
					cEnd.set(Calendar.WEEK_OF_MONTH, cEnd.get(Calendar.WEEK_OF_MONTH));
					cEnd.set(Calendar.HOUR_OF_DAY, end.getHours());
					cEnd.set(Calendar.MINUTE, end.getMinutes());
					cEnd.set(Calendar.DAY_OF_WEEK, ee.get(Calendar.DAY_OF_WEEK));

					Calendar nowCalendar = Calendar.getInstance();
					nowCalendar.setTime(new Date());
					
					if (nowCalendar.before(cEnd)) {
						if (nowCalendar.before(cStart)) {
							cl.setState(State.NEXT);
						} else {
							cl.setState(State.RUN);
						}
						break;
					}
				}
				adapter.notifyDataSetChanged();
				
				h.postDelayed(this, 60000);
			}
		};
		h.post(r);
		
		
		return l;
	}
	
	

	private class ScheduleAdapter extends ArrayAdapter<TClass> {

		private List<TClass> data;
		private LayoutInflater inflater;
		private String dateFormat = "HH:mm";
		private SimpleDateFormat sdf;

		public ScheduleAdapter(Context context, int resource, List<TClass> objects) {
			super(context, resource, objects);
			data = objects;
			sdf = new SimpleDateFormat(dateFormat);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final TClass cl = getItem(position);
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.class_item, null);
				holder.t = (TextView) convertView.findViewById(R.id.class_item_text1);
				holder.tt = (TextView) convertView.findViewById(R.id.class_item_text2);
				holder.ttt = (TextView) convertView.findViewById(R.id.class_item_text3);
				holder.image = (ImageView) convertView.findViewById(R.id.class_item_mark);
				holder.line = convertView.findViewById(R.id.class_item_line);
				holder.header = convertView.findViewById(R.id.class_item_day_header);
				holder.headerDay = (TextView) convertView.findViewById(R.id.class_item_day_text);
				holder.t.setTypeface(tCond);
				holder.tt.setTypeface(tCondBold);
				holder.ttt.setTypeface(tCondBold);
				holder.button = (ImageButton) convertView.findViewById(R.id.class_item_button);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			MarginLayoutParams marginParams = new MarginLayoutParams(holder.image.getLayoutParams());
			int marginTop = 0;
			if (cl.isNewDay()) {
				holder.header.setVisibility(View.VISIBLE);
				holder.headerDay.setText(getString(days[cl.getStart().getDay() - 1]));
				marginTop = 80;
			} else {
				holder.header.setVisibility(View.GONE);
			}
			marginParams.setMargins(0, marginTop, 0, 0);
			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
			holder.image.setLayoutParams(layoutParams);
			
			holder.button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cl.setNotify(!cl.isNotify());
					new Db(context).setNotifyOnClass(cl.getId(), cl.isNotify());
					if (cl.isNotify()) {
						v.setBackgroundResource(R.drawable.circle_green_selector);
						((ImageButton) v).setImageResource(R.drawable.vv);
						NotificationService.setAlarm(cl, getActivity());
						
					} else {
						v.setBackgroundResource(R.drawable.circle_red_selector);
						((ImageButton) v).setImageResource(R.drawable.xx);
						NotificationService.cancelAlarm(cl, getActivity());
						
					}

				}
			});

			if (cl.isNotify()) {
				holder.button.setBackgroundResource(R.drawable.circle_green_selector);
				holder.button.setImageResource(R.drawable.vv);
			} else {
				holder.button.setBackgroundResource(R.drawable.circle_red_selector);
				holder.button.setImageResource(R.drawable.xx);
			}
			
			switch(cl.getState()) {
			case NEXT:
				holder.image.setImageResource(R.drawable.next);
				holder.line.setBackgroundResource(R.drawable.oragne_gray_gradient);
				break;
			case NUL:
				holder.image.setImageResource(R.drawable.circle_gray);
				holder.line.setBackgroundResource(R.color.gray_total);
				break;
			case RUN:
				holder.image.setImageResource(R.drawable.play);
				holder.line.setBackgroundResource(R.drawable.green_gray_gradient);
				break;
			}

			holder.t.setText(cl.getName());
			holder.tt.setText(cl.getRoom());
			holder.ttt.setText(sdf.format(cl.getStart()) + " - " + sdf.format(cl.getEnd()));

			return convertView;
		}

		private class ViewHolder {
			public TextView t, tt, ttt;
			public ImageButton button;
			public ImageView image;
			public View line;
			public View header;
			public TextView headerDay;
		}

	}

}
