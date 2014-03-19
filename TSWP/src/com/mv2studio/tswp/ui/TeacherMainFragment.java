package com.mv2studio.tswp.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mv2studio.tswp.R;
import com.mv2studio.tswp.db.Db;
import com.mv2studio.tswp.model.TClass;

public class TeacherMainFragment extends BaseFragment {

	private Context context;
	private static String IS_EDIT = "edit", CLASS_TAG = "class";
	
	
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
		private EditText name, desc;
		private Button startTimeBut, startDateBut, endTimeBut, endDateBut, fileChooserBut, cancel, ok;
		private String timeFormat = "HH:mm";
		private String dateFormat = "dd.MMM.yyyy";
		private Date startDate, endDate;
		private TClass thisClass;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setStyle(DialogFragment.STYLE_NO_TITLE, 0);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.teacher_edit_class, null);
			Bundle b = getArguments();
			thisClass = (TClass) b.getSerializable(CLASS_TAG);
			boolean isEdit = b.getBoolean(IS_EDIT);
			
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

						// send to server, notify adapter
						
						
						
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
			name.setTypeface(tCond);
			desc.setTypeface(tCond);

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

					// Alternatively, use FileUtils.getFile(Context, Uri)
					if (path != null && FileUtils.isLocal(path)) {
						File file = new File(path);
					}
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
}