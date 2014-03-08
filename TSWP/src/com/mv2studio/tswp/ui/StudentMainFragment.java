package com.mv2studio.tswp.ui;

import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mv2studio.tswp.R;
import com.mv2studio.tswp.db.Db;
import com.mv2studio.tswp.model.TClass;

public class StudentMainFragment extends BaseFragment {

	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		
		LinearLayout l = (LinearLayout) inflater.inflate(R.layout.fragment_student_main, null);
		ListView list = (ListView) l.findViewById(R.id.fragment_student_main_list);
		
		List<TClass> classes = new Db(context).getAllClasses();
		list.setAdapter(new ScheduleAdapter(context, 0, classes));
		
		return l;
	}
	
	
	
	private class ScheduleAdapter extends ArrayAdapter<TClass>  {

		private List<TClass> data;
		private LayoutInflater inflater;
		
		public ScheduleAdapter(Context context, int resource, List<TClass> objects) {
			super(context, resource, objects);
			data = objects;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			final TClass cl = getItem(position);
			ViewHolder holder;
			
			if(convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.class_item, null);
				holder.t = (TextView) convertView.findViewById(R.id.class_item_text1);
				holder.tt = (TextView) convertView.findViewById(R.id.class_item_text2);
				holder.ttt = (TextView) convertView.findViewById(R.id.class_item_text3);
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
					v.setBackgroundColor(cl.isNotify() ? Color.GREEN : Color.RED);
//					((ImageButton)v).setImageBitmap(bm);
				}
			});
			
			holder.button.setBackgroundColor(cl.isNotify() ? Color.GREEN : Color.RED);
			holder.t.setText(cl.getName());
			holder.tt.setText(cl.getRoom());
			holder.ttt.setText(cl.getStart().toString());
			
			return convertView;
		}
		
		private class ViewHolder {
			public TextView t, tt, ttt;
			public ImageButton button;
		}

	}
	
}
