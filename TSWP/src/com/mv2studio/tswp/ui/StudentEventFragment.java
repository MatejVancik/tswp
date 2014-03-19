package com.mv2studio.tswp.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.mv2studio.tswp.communication.CommHelper;
import com.mv2studio.tswp.db.Db;
import com.mv2studio.tswp.model.TClass;
import com.mv2studio.tswp.model.TClass.State;

public class StudentEventFragment extends BaseFragment {

	private Context context;
	private int[] days = { R.string.day_1, R.string.day_2, R.string.day_3, R.string.day_4, R.string.day_5, R.string.day_6, R.string.day_7 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		View l = inflater.inflate(R.layout.fragment_student_main, null);
		ListView list = (ListView) l.findViewById(R.id.fragment_student_main_list);

		final List<TClass> classes = new Db(context).getAllEvents();
		final ScheduleAdapter adapter = new ScheduleAdapter(context, 0, classes);
		list.setAdapter(adapter);

		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				
//				String json = CommHelper.getHttpGetReponse("url");
				
				int id;
				String name, room;
				Date start, end;
				
//				new TClass(name, room, start, end, false, false);
				
				return null;
			}
			
		}.execute();
		
		return l;
	}
	
	

	private class ScheduleAdapter extends ArrayAdapter<TClass> {

		private List<TClass> data;
		private LayoutInflater inflater;
		private String dateFormat = "HH:mm";
		private String firstFormat = "dd.MMM.yyyy HH:mm";
		private SimpleDateFormat sdf, firstDateFormat;

		public ScheduleAdapter(Context context, int resource, List<TClass> objects) {
			super(context, resource, objects);
			data = objects;
			sdf = new SimpleDateFormat(dateFormat);
			firstDateFormat = new SimpleDateFormat(firstFormat);
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final TClass cl = getItem(position);
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.class_item, null);
				convertView.findViewById(R.id.class_item_mark_layout).setVisibility(View.GONE);
				holder.t = (TextView) convertView.findViewById(R.id.class_item_text1);
				holder.tt = (TextView) convertView.findViewById(R.id.class_item_text2);
				holder.ttt = (TextView) convertView.findViewById(R.id.class_item_text3);
				holder.t.setTypeface(tCond);
				holder.tt.setTypeface(tCondBold);
				holder.ttt.setTypeface(tCondBold);
				holder.button = (ImageButton) convertView.findViewById(R.id.class_item_button);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			holder.button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cl.setNotify(!cl.isNotify());
					new Db(context).setNotifyOnClass(cl.getId(), cl.isNotify());
					if (cl.isNotify()) {
						v.setBackgroundResource(R.drawable.circle_green_selector);
						((ImageButton) v).setImageResource(R.drawable.vv);
					} else {
						v.setBackgroundResource(R.drawable.circle_red_selector);
						((ImageButton) v).setImageResource(R.drawable.xx);
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
			

			holder.t.setText(cl.getName());
			holder.tt.setText(cl.getRoom());
			holder.ttt.setText(firstDateFormat.format(cl.getStart()) + " - " + sdf.format(cl.getEnd()));

			return convertView;
		}

		private class ViewHolder {
			public TextView t, tt, ttt;
			public ImageButton button;
		}

	}

}
