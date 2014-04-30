package com.mv2studio.tswp.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mv2studio.mynsa.R;
import com.mv2studio.tswp.communication.CommHelper;
import com.mv2studio.tswp.communication.SuscribtionTask;
import com.mv2studio.tswp.communication.TeacherRegistrationTask;
import com.mv2studio.tswp.communication.URLs;
import com.mv2studio.tswp.core.Prefs;
import com.mv2studio.tswp.db.Db;
import com.mv2studio.tswp.model.EventFile;
import com.mv2studio.tswp.model.TClass;
import com.mv2studio.tswp.ui.MainActivity.OnRefreshClickListener;

public class StudentEventFragment extends BaseFragment {

	private Context context;
	private int[] days = { R.string.day_1, R.string.day_2, R.string.day_3, R.string.day_4, R.string.day_5, R.string.day_6, R.string.day_7 };

	private ScheduleAdapter adapter;
	
	public static final String DEP_TAG = "STUDENT_DEPARTMENT", 
			   				  YEAR_TAG = "STUDENT_YEAR",
							  ID_TAG = "student_id";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		View l = inflater.inflate(R.layout.fragment_student_main, null);
		ListView list = (ListView) l.findViewById(R.id.fragment_student_main_list);
		
		final List<TClass> classes = new Db(context).getAllEvents();
		adapter = new ScheduleAdapter(context, 0, classes);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("event clicked");
				Bundle b = new Bundle();
				b.putSerializable(TeacherMainFragment.CLASS_TAG, adapter.getItem(position));
				EventPreviewDialog dialog= new EventPreviewDialog();
				dialog.setArguments(b);
				dialog.show(getFragmentManager(), "dialog");
			}
		});
		final RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setInterpolator(getActivity(), android.R.anim.accelerate_decelerate_interpolator);
		rotate.setDuration(1000);
		
		((MainActivity)getActivity()).setOnRefreshClickListener(new OnRefreshClickListener() {
			@Override
			public void onRefresh(final ImageButton button) {
				new DownloadEventsTask() {
					protected void onPreExecute() {
						super.onPreExecute();
						button.startAnimation(rotate);
					}
					
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						button.clearAnimation();
					}
					
					protected void onCancelled() {
						super.onCancelled();
						button.clearAnimation();
					}
					
				}.execute();
			}
		});
		
		new DownloadEventsTask().execute();
		
		new Thread(
		new Runnable() {
			@Override
			public void run() {
				new Db(context).cleanEvents();
			}
		}).start();
		
		return l;
	}
	
	
	private class DownloadEventsTask extends AsyncTask<Void, Void, Void> {
		
		protected void onPostExecute(Void result) {
			adapter.clear();
			adapter.addAll(new Db(context).getAllEvents());
			adapter.notifyDataSetChanged();
		}
		
		@Override
		protected void onCancelled() {
			super.onCancelled();
			Toast.makeText(context, "Zapnite si prosím internet", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			Db db = new Db(context);
			db.cleanEvents();
			String json = CommHelper.getHttpPostResponse(
					URLs.getEvents, 
					new String[][]{{"department", Prefs.getIntValue(DEP_TAG, context)+""},{"year", Prefs.getString(YEAR_TAG, context)}}
			);
			if(json == null) {
				cancel(true);
				return null;
			}
			System.out.println("json: "+json);
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int id;
			String name, room, desc;
			Date start, end;
			
			ArrayList<TClass> classes = new ArrayList<TClass>();
			
			try {
				JSONArray array = new JSONArray(json);
				for(int i = 0; i < array.length(); i++) {
					JSONObject obj = array.getJSONObject(i);
					id = obj.getInt("id");
					name = obj.getString("title");
					desc = obj.getString("desc");
					room = obj.getString("place");
					start = format.parse(obj.getString("start_date"));
					end = format.parse(obj.getString("end_date"));
					JSONArray files = obj.getJSONArray("files");
					TClass cl = new TClass(id, name, room, start, end, false, false, false);
					cl.setDesc(desc);
					
					
					
					for(int j = 0; j < files.length(); j++) {
						JSONObject fileObj = files.getJSONObject(j);
						int fileId = fileObj.getInt("id");
						String fileName = fileObj.getString("name");
						cl.addFile(new EventFile(fileName, fileId));
					}
					
					classes.add(cl);
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
//			db.cleanEvents();
			db.insertEvents(classes);
			
			return null;
		}
		
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
				convertView = inflater.inflate(R.layout.event_item, null);
				holder.title = (TextView) convertView.findViewById(R.id.event_item_title);
				holder.desc = (TextView) convertView.findViewById(R.id.event_item_desc);
				holder.room = (TextView) convertView.findViewById(R.id.event_item_room);
				holder.date = (TextView) convertView.findViewById(R.id.event_item_date);
				holder.files = (TextView) convertView.findViewById(R.id.event_item_files);
				holder.files.setTypeface(tCondBold);
				holder.date. setTypeface(tCond);
				holder.title.setTypeface(tCondBold);
				holder.desc.setTypeface(tCond);
				holder.room.setTypeface(tCondBold);
				holder.button = (ImageButton) convertView.findViewById(R.id.event_item_button);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			
			holder.button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					cl.setNotify(!cl.isNotify());
					new Thread(
					new Runnable() {
						public void run() {
							new Db(context).setNotifyOnClass(cl.getId(), cl.isNotify());
						}
					}).start();
					
					if (cl.isNotify()) {
						v.setBackgroundResource(R.drawable.circle_green_selector);
						((ImageButton) v).setImageResource(R.drawable.vv);
						new SuscribtionTask(getActivity()).execute(URLs.suscribe,Prefs.getString(StudentEventFragment.ID_TAG, context), String.valueOf(cl.getId()));
					} else {
						v.setBackgroundResource(R.drawable.circle_red_selector);
						((ImageButton) v).setImageResource(R.drawable.xx);
						new SuscribtionTask(getActivity()).execute(URLs.unsuscribe,Prefs.getString(StudentEventFragment.ID_TAG, context), String.valueOf(cl.getId()));
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
			

			holder.title.setText(cl.getName());
			holder.desc.setText(cl.getDesc());
			holder.room.setText(cl.getRoom());
			holder.files.setText("Prílohy: "+cl.getFiles().size());
			holder.date.setText(sdf.format(cl.getStart())+" - "+sdf.format(cl.getEnd()));

			return convertView;
		}

		private class ViewHolder {
			public TextView title, desc, room, date, files;
			public ImageButton button;
		}

	}
	
	
	@SuppressLint("ValidFragment")
	private class EventPreviewDialog extends DialogFragment {
		private TextView title, desc, room, time, attachTitle, placeTitle;
		private LinearLayout filesLayout;
		private TClass thisClass;
		private SimpleDateFormat sdf;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			sdf = new SimpleDateFormat("HH:mm dd.MMMM");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.event_preview, null);
			
			Bundle b = getArguments();
			thisClass = (TClass) b.getSerializable(TeacherMainFragment.CLASS_TAG);
			
			title = (TextView) v.findViewById(R.id.event_preview_title);
			desc = (TextView) v.findViewById(R.id.event_preview_desc);
			room = (TextView) v.findViewById(R.id.event_preview_room);
			time = (TextView) v.findViewById(R.id.event_preview_time);
			attachTitle = (TextView) v.findViewById(R.id.event_preview_attach_title);
			placeTitle = (TextView) v.findViewById(R.id.event_preview_place_title);
			
			title.setTypeface(tCondBold);
			desc.setTypeface(tCond);
			room.setTypeface(tCondBold);
			time.setTypeface(tCond);
			attachTitle.setTypeface(tCondLight);
			placeTitle.setTypeface(tCondLight);
			
			title.setText(thisClass.getName());
			desc.setText(thisClass.getDesc());
			room.setText(thisClass.getRoom());
			time.setText(sdf.format(thisClass.getStart())+" - "+sdf.format(thisClass.getEnd()));
			
			filesLayout = (LinearLayout) v.findViewById(R.id.event_preview_files_layout);
			
			if(thisClass.getFiles().size() == 0) filesLayout.setVisibility(View.GONE);
			
			for(final EventFile file: thisClass.getFiles()) {
				final View fv = inflater.inflate(R.layout.upload_item, null);
				TextView text = (TextView) fv.findViewById(R.id.upload_item_text);
				text.setTypeface(tCond);
				ImageButton but = (ImageButton) fv.findViewById(R.id.upload_item_remove);
				but.setImageResource(R.drawable.download);
				but.setBackgroundResource(R.drawable.circle_blue_selector);
				text.setText(file.name);
				but.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// download file
						CommHelper.downloadFile(context, thisClass.getName(), file.name, file.id);
					}
				});
				filesLayout.addView(fv);
			}
			
			return v;
		}
		
	}

}
