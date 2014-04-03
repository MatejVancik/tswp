package com.mv2studio.tswp.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mv2studio.tswp.R;
import com.mv2studio.tswp.adapter.SpinnerAdapter;
import com.mv2studio.tswp.communication.CommHelper;
import com.mv2studio.tswp.core.Prefs;
import com.mv2studio.tswp.model.Department;
import com.mv2studio.tswp.model.Faculty;
import com.mv2studio.tswp.model.TClass;

public class TeacherMainFragment extends BaseFragment {

	private Context context;
	private static String IS_EDIT = "edit", CLASS_TAG = "class";
	public static final String TOKEN_TAG = "token";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		View v = inflater.inflate(R.layout.fragment_teacher_main, null);
		v.findViewById(R.id.fragment_teacher_main_add_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putBoolean(IS_EDIT, false);
				EditorDialog dialog = new EditorDialog();
				dialog.setArguments(b);
				dialog.show(getFragmentManager(), "dialog");
			}
		});
		ListView list = (ListView) v.findViewById(R.id.fragment_teacher_main_list);
		TeacherScheduleAdapter adapter = new TeacherScheduleAdapter(context, 0, new ArrayList<TClass>());
		list.setAdapter(adapter);
		return v;

	}

	private class TeacherScheduleAdapter extends ArrayAdapter<TClass> {

		private List<TClass> data;
		private LayoutInflater inflater;

		public TeacherScheduleAdapter(Context context, int resource, List<TClass> objects) {
			super(context, resource, objects);
			data = objects;
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
					Bundle b = new Bundle();
					b.putBoolean(IS_EDIT, true);
					b.putSerializable(CLASS_TAG, cl);
					EditorDialog dialog = new EditorDialog();
					dialog.setArguments(b);
					dialog.show(getFragmentManager(), "dialog");
				}
			});


			// holder.t.setText(cl.getName());
			// holder.tt.setText(cl.getRoom());
			// holder.ttt.setText(cl.getStart().toString());

			return convertView;
		}

		private class ViewHolder {
			public TextView t, tt, ttt;
			public ImageButton button;
			public ImageView image;
			public View line;
		}

	}

	public abstract class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
	}

	public abstract class TimePickerFragment extends DialogFragment implements OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		}
	}

	private class EditorDialog extends DialogFragment {

		private TextView start, end, title, attach;
		private EditText name, desc, room;
		private Button startTimeBut, startDateBut, endTimeBut, endDateBut, fileChooserBut, cancel, ok;
		private Spinner yearSpin, facSpin, DepSpin;
		private String timeFormat = "HH:mm";
		private String dateFormat = "dd.MMM.yyyy";
		private Date startDate, endDate;
		private TClass thisClass;
		private LinearLayout filesLayout;
		private ArrayList<UploadFileHolder> files;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NO_TITLE, 0);
			files = new ArrayList<TeacherMainFragment.UploadFileHolder>();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.teacher_edit_class, null);
			Bundle b = getArguments();
			thisClass = (TClass) b.getSerializable(CLASS_TAG);
			boolean isEdit = b.getBoolean(IS_EDIT);
			endDate = new Date();
			startDate = new Date();
			filesLayout = (LinearLayout) v.findViewById(R.id.class_edit_files_layout);
			
			
			final ArrayList<Faculty> deps = Department.getDepartments(getActivity());
			
			List<String> list = new ArrayList<String>();
			for (Faculty dep : deps)
				list.add(dep.faculty);
			
			yearSpin = (Spinner) v.findViewById(R.id.class_edit_student_year);
			List<CharSequence> years =  Arrays.asList(getResources().getTextArray(R.array.years));
			final ArrayAdapter<String> yearAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, years);
			yearSpin.setAdapter(yearAdapter);
			
			DepSpin = (Spinner) v.findViewById(R.id.class_edit_student_dep);
			final ArrayAdapter<Department> departmentsAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, new ArrayList<Department>()); //new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, new ArrayList<String>());
			DepSpin.setAdapter(departmentsAdapter);
			
			facSpin = (Spinner) v.findViewById(R.id.class_edit_student_faculty);
			ArrayAdapter<String> facultyAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, list);//new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
			facSpin.setAdapter(facultyAdapter);
			facSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					List<Department> items = new ArrayList<Department>();
					for (Faculty fac : deps)
						if (fac.faculty.equals(facSpin.getSelectedItem()))
							items = fac.dep;
					departmentsAdapter.clear();
					departmentsAdapter.addAll(items);
					departmentsAdapter.notifyDataSetChanged();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					departmentsAdapter.clear();
					departmentsAdapter.notifyDataSetChanged();
				}
			});
			
			
			
			
			OnClickListener timeDatelickListener = new OnClickListener() {

				@Override
				public void onClick(final View v) {

					// set time
					if (v.getId() == R.id.class_edit_start_time || v.getId() == R.id.class_edit_end_time) {
						new TimePickerFragment() {
							@Override
							public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
								((Button) v).setText(String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute));
								setDate(v.getId() == R.id.class_edit_end_time ? endDate : startDate, hourOfDay, minute);
							}
						}.show(getFragmentManager(), "timePicker");

					} else if (v.getId() == R.id.class_edit_start_date || v.getId() == R.id.class_edit_end_date) {
						new DatePickerFragment() {

							@Override
							public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
								((Button) v).setText(dayOfMonth + "." + monthOfYear + "." + year);
								setDate(v.getId() == R.id.class_edit_end_date ? endDate : startDate, year, monthOfYear, dayOfMonth);
							}
						}.show(getFragmentManager(), "datePicker");
					}

				}
			};

			OnClickListener clickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					switch (v.getId()) {
					case R.id.class_edit_ok:
						if(name.getText().toString().isEmpty())
							name.setError("Musíte zadať názov udalosti");
						if(desc.getText().toString().isEmpty())
							desc.setError("Musíte zadať popis udalosti");
						
						
						
						// send to server, notify adapter
						TClass cl = new TClass(name.getText().toString(), desc.getText().toString(),
								room.getText().toString(), startDate, endDate, false, false);
						
						int id = ((Department)DepSpin.getSelectedItem()).id;
						int year = yearSpin.getSelectedItemPosition();
						
						new SendClassTask(cl, files, id, year).execute();
						
						
						break;
					case R.id.class_edit_cancel:
						EditorDialog.this.dismiss();
						break;
					case R.id.class_edit_choose_file:
						Intent getContentIntent = FileUtils.createGetContentIntent();

						Intent intent = Intent.createChooser(getContentIntent, "Select a file");
						startActivityForResult(intent, 1234);
						break;
					}

				}
			};

			
			
			SimpleDateFormat timeF = new SimpleDateFormat(timeFormat), dateF = new SimpleDateFormat(dateFormat);

			ok = (Button) v.findViewById(R.id.class_edit_ok);
			cancel = (Button) v.findViewById(R.id.class_edit_cancel);
			ok.setTypeface(tCondBold);
			cancel.setTypeface(tCondBold);

			title = (TextView) v.findViewById(R.id.class_edit_title);
			start = (TextView) v.findViewById(R.id.class_edit_start_title);
			end = (TextView) v.findViewById(R.id.class_edit_end_title);
			attach = (TextView) v.findViewById(R.id.class_edit_attach);
			attach.setTypeface(tThin);
			start.setTypeface(tThin);
			end.setTypeface(tThin);
			title.setTypeface(tCondBold);
			title.setText(isEdit ? "Upraviť udalosť" : "Pridať udalosť");

			name = (EditText) v.findViewById(R.id.class_edit_name);
			desc = (EditText) v.findViewById(R.id.class_edit_desc);
			room = (EditText) v.findViewById(R.id.class_edit_room);
			name.setTypeface(tCond);
			desc.setTypeface(tCond);
			room.setTypeface(tCond);

			startTimeBut = (Button) v.findViewById(R.id.class_edit_start_time);
			startTimeBut.setOnClickListener(timeDatelickListener);
			startDateBut = (Button) v.findViewById(R.id.class_edit_start_date);
			startDateBut.setOnClickListener(timeDatelickListener);
			endDateBut = (Button) v.findViewById(R.id.class_edit_end_date);
			endDateBut.setOnClickListener(timeDatelickListener);
			endTimeBut = (Button) v.findViewById(R.id.class_edit_end_time);
			endTimeBut.setOnClickListener(timeDatelickListener);
			fileChooserBut = (Button) v.findViewById(R.id.class_edit_choose_file);

			Date current = new Date();
			startTimeBut.setText(timeF.format(current));
			startDateBut.setText(dateF.format(current));
			Calendar c = Calendar.getInstance();
			c.setTime(current);
			c.add(Calendar.HOUR_OF_DAY, 1);
			c.add(Calendar.MINUTE, 30);
			current = c.getTime();
			endTimeBut.setText(timeF.format(current));
			endDateBut.setText(dateF.format(current));

			fileChooserBut.setTypeface(tCond);
			startTimeBut.setTypeface(tCond);
			startDateBut.setTypeface(tCond);
			endTimeBut.setTypeface(tCond);
			endDateBut.setTypeface(tCond);

			fileChooserBut.setOnClickListener(clickListener);
			ok.setOnClickListener(clickListener);
			cancel.setOnClickListener(clickListener);
			
			
			if(isEdit) {
				name.setText(thisClass.getName());
				desc.setText(thisClass.getRoom());
				
				current = thisClass.getStart();
				startTimeBut.setText(timeF.format(current));
				startDateBut.setText(dateF.format(current));
				
				current = thisClass.getEnd();
				endTimeBut.setText(timeF.format(current));
				endDateBut.setText(dateF.format(current));
			}

			return v;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			super.onActivityResult(requestCode, resultCode, data);

			switch (requestCode) {
			case 1234:
				if (resultCode == Activity.RESULT_OK) {

					final Uri uri = data.getData();

					// Get the File path from the Uri
					String path = FileUtils.getPath(getActivity(), uri);
					final UploadFileHolder f = new UploadFileHolder(path, context);
					f.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							files.remove(f);
							filesLayout.removeView(f.getView());
						}
					});
					files.add(f);
					filesLayout.addView(f.getView());
				}
				break;
			}

		}

		private void setDate(Date date, int year, int month, int day) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DAY_OF_MONTH, day);
			if (date == startDate)
				startDate = c.getTime();
			else if (date == endDate)
				endDate = c.getTime();
		}

		private void setDate(Date date, int hour, int minute) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			if (date == startDate)
				startDate = c.getTime();
			else if (date == endDate)
				endDate = c.getTime();
		}

	}
	
	private class SendClassTask extends AsyncTask<Void, Void, Void>{

		TClass cl;
		ArrayList<UploadFileHolder> files;
		int id, year;
		
		public SendClassTask(TClass cl, ArrayList<UploadFileHolder> files, int id, int year) {
			this.cl = cl;
			this.files = files;
			this.id = id;
			this.year = year;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String retID = CommHelper.getHttpPostResponse("http://tswp.martinviszlai.com/create_event.php?token="+Prefs.getString(TOKEN_TAG, context), 
					new String[][] {{"title", cl.getName()}, 
				{"desc", cl.getDesc()},
				{"place", cl.getRoom()},
				{"start_date", sdf.format(cl.getStart())},
				{"end_date", sdf.format(cl.getEnd())},
				{"department", id+""},
				{"year", year+""}
			});
			
			Log.w("", "token: "+Prefs.getString(TOKEN_TAG, context));
			
			Log.e("", "title: "+cl.getName()+"  place: "+cl.getRoom()+"  desc: "+cl.getDesc()+"  dep: "+id+"  year: "+year+"time: "+ sdf.format(cl.getStart()));
			Log.e("", "GET BACK: '"+retID+"'");
			
			return null;
		}
		
	}
	
	
	
	public static class UploadFileHolder {
		ImageButton but;
		String path;
		View v;
		
		public UploadFileHolder(String path, Context context) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.upload_item, null);
			TextView text = (TextView) v.findViewById(R.id.upload_item_text);
			String[] title = path.split("/");
			this.path = path;
			text.setText(title[title.length-1]);
			but = (ImageButton) v.findViewById(R.id.upload_item_remove);
		}
		
		public void setOnClickListener(OnClickListener listener) {
			but.setOnClickListener(listener);
		}
		
		public String getPath() {
			return path;
		}
		
		public View getView() {
			return v;
		}
		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			return path.equals(((UploadFileHolder)o).getPath()); 
		}
	}
}