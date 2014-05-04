package com.mv2studio.tswp.ui;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mv2studio.mynsa.R;
import com.mv2studio.tswp.adapter.SpinnerAdapter;
import com.mv2studio.tswp.communication.CommHelper;
import com.mv2studio.tswp.communication.TeacherLoginTask;
import com.mv2studio.tswp.communication.URLs;
import com.mv2studio.tswp.core.Prefs;
import com.mv2studio.tswp.model.Department;
import com.mv2studio.tswp.model.EventFile;
import com.mv2studio.tswp.model.Faculty;
import com.mv2studio.tswp.model.TClass;
import com.mv2studio.tswp.ui.MainActivity.OnRefreshClickListener;

public class TeacherMainFragment extends BaseFragment {

	private Context context;
	private static String IS_EDIT = "edit";
	static String CLASS_TAG = "class";
	public static final String TOKEN_TAG = "token", USER_NAME_TAG = "name", USER_PASS_TAG = "pass";
	private String timeFormat = "HH:mm";
	private String dateFormat = "dd.MMM.yyyy";
	private TeacherScheduleAdapter adapter;
	private View loadingView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		View v = inflater.inflate(R.layout.fragment_teacher_main, null);
		Button add = (Button) v.findViewById(R.id.fragment_teacher_main_add_button);
		add.setTypeface(tCondBold);
		add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putBoolean(IS_EDIT, false);
				EditorDialog dialog = new EditorDialog();
				dialog.setArguments(b);
				dialog.show(getFragmentManager(), "dialog");
			}
		});
		ListView attachmentList = (ListView) v.findViewById(R.id.fragment_teacher_main_list);
		adapter = new TeacherScheduleAdapter(context, 0, new ArrayList<TClass>());
		attachmentList.setAdapter(adapter);
		attachmentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				System.out.println("event clicked");
				Bundle b = new Bundle();
				b.putSerializable(TeacherMainFragment.CLASS_TAG, adapter.getItem(position));
				EventPreviewDialog dialog = new EventPreviewDialog();
				dialog.setArguments(b);
				dialog.show(getFragmentManager(), "dialog");
			}
		});
		loadingView = v.findViewById(R.id.fragment_teacher_loading);
		new TeacherLoginTask(context).execute();
		new DownloadEventsTask().execute();

		final RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setInterpolator(getActivity(), android.R.anim.accelerate_decelerate_interpolator);
		rotate.setDuration(1000);

		((MainActivity) getActivity()).setOnRefreshClickListener(new OnRefreshClickListener() {
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

		return v;

	}

	private class DownloadEventsTask extends AsyncTask<Void, Void, Void> {

		ArrayList<TClass> classes = new ArrayList<TClass>();

		@Override
		protected void onPreExecute() {
			loadingView.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(Void... params) {
			System.out.println("REQUEST FOR JSON TO: " + URLs.getEvents + "?token=" + Prefs.getString(TOKEN_TAG, context));
			String json = CommHelper.getHttpGetReponse(URLs.getEvents + "?token=" + Prefs.getString(TOKEN_TAG, context));
			if (json == null) {
				cancel(true);
				return null;
			}
			System.out.println("JSON FROM SERVER: " + json);
			// [{"id":16,"title":"a","desc":"a","start_date":"2015-04-07 09:32:44",
			// "end_date":"2015-04-07 09:32:44","place ":null,"files":[10]}]
			SimpleDateFormat timeF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			try {
				JSONArray mainArray = new JSONArray(json);
				for (int i = 0; i < mainArray.length(); i++) {
					JSONObject obj = mainArray.getJSONObject(i);
					String title = obj.getString("title"), desc = obj.getString("desc"), room = obj.getString("place");
					int id = obj.getInt("id"), year = obj.getInt("year"), dep = obj.getInt("department");
					Date start = timeF.parse(obj.getString("start_date")), end = timeF.parse(obj.getString("end_date"));
					System.out.println("creating task id: " + id);
					TClass cl = new TClass(id, title, room, start, end, false, false, false);
					cl.setDesc(desc);
					cl.setDepartment(dep);
					cl.setYear(year);

					JSONArray filesArray = obj.getJSONArray("files");
					for (int j = 0; j < filesArray.length(); j++) {
						JSONObject fileObj = filesArray.getJSONObject(j);
						int fileId = fileObj.getInt("id");
						String fileName = fileObj.getString("name");

						cl.addFile(new EventFile(fileName, fileId));
					}

					classes.add(cl);

				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			adapter.clear();
			adapter.addAll(classes);
			adapter.notifyDataSetChanged();
			loadingView.setVisibility(View.GONE);
		}

	}

	private class DownloadStudentsTask extends AsyncTask<String, Void, Void> {

		ArrayList<String> students = new ArrayList<String>();

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(String... params) {
			System.out.println("REQUEST FOR JSON TO: " + URLs.getSubscriptions + "?token=" + Prefs.getString(TOKEN_TAG, context) + "\n with event_id: " + params[0]);
			ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("event_id", params[0]));
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URLs.getSubscriptions + "?token=" + Prefs.getString(TOKEN_TAG, context));
			String json = null;
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(pairs));
				HttpResponse response = httpClient.execute(httpPost);
				json = EntityUtils.toString(response.getEntity());
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (json == null) {
				cancel(true);
				return null;
			}
			System.out.println("JSON FROM SERVER: " + json);

			try {
				JSONArray array = new JSONArray(json);

				for (int i = 0; i < array.length(); i++) {
					students.add(array.getString(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		protected void onPostExecute(Void result) {
			// TODO zobrazit studentov
		}

	}

	private class TeacherScheduleAdapter extends ArrayAdapter<TClass> {

		private List<TClass> data;
		private LayoutInflater inflater;
		private SimpleDateFormat sdf;

		public TeacherScheduleAdapter(Context context, int resource, List<TClass> objects) {
			super(context, resource, objects);
			data = objects;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			sdf = new SimpleDateFormat(timeFormat + " dd.MM.");
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
				holder.attach = (TextView) convertView.findViewById(R.id.event_item_files);
				convertView.findViewById(R.id.event_item_button).setVisibility(View.GONE);
				holder.attach.setTypeface(tCond);
				holder.date.setTypeface(tCond);
				holder.title.setTypeface(tCond);
				holder.desc.setTypeface(tCondBold);
				holder.room.setTypeface(tCondBold);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText(cl.getName());
			holder.desc.setText(cl.getDesc());
			holder.room.setText(cl.getRoom());
			holder.attach.setText("Počet príloh: " + cl.getFiles().size());
			holder.date.setText(sdf.format(cl.getStart()) + " - " + sdf.format(cl.getEnd()));

			return convertView;
		}

		private class ViewHolder {
			public TextView title, desc, room, date, attach;
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

		private TextView start, end, forPeople, title, attach;
		private EditText name, desc, room;
		private Button startTimeBut, startDateBut, endTimeBut, endDateBut, fileChooserBut, cancel, ok, delete;
		private Spinner yearSpin, facSpin, depSpin;

		private Date startDate, endDate;
		private TClass thisClass;
		private LinearLayout filesLayout;
		private ArrayList<UploadFileHolder> files;
		private ArrayList<Integer> filesToDelete;

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
			final boolean isEdit = b.getBoolean(IS_EDIT);
			endDate = new Date();
			startDate = new Date();
			filesLayout = (LinearLayout) v.findViewById(R.id.class_edit_files_layout);

			final ArrayList<Faculty> allFacts = Department.getDepartments(getActivity());

			List<String> list = new ArrayList<String>();
			for (Faculty dep : allFacts)
				list.add(dep.faculty);

			yearSpin = (Spinner) v.findViewById(R.id.class_edit_student_year);
			List<CharSequence> years = Arrays.asList(getResources().getTextArray(R.array.years));
			final ArrayAdapter<String> yearAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, years);
			yearSpin.setAdapter(yearAdapter);

			depSpin = (Spinner) v.findViewById(R.id.class_edit_student_dep);
			final ArrayAdapter<Department> departmentsAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, new ArrayList<Department>());

			depSpin.setAdapter(departmentsAdapter);

			facSpin = (Spinner) v.findViewById(R.id.class_edit_student_faculty);
			ArrayAdapter<String> facultyAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, list);

			facSpin.setAdapter(facultyAdapter);
			facSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					List<Department> items = new ArrayList<Department>();
					for (Faculty fac : allFacts)
						if (fac.faculty.equals(facSpin.getSelectedItem())) items = fac.dep;
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
								((Button) v).setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
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
						case R.id.class_edit_delete:
							new AlertDialog.Builder(context).setMessage("Naozaj chcete vymazať udalosť\n " + thisClass.getName() + "?").setNegativeButton("Nie", null)
									.setPositiveButton("Áno", new DialogInterface.OnClickListener() {

										@Override
										public void onClick(DialogInterface dialog, int which) {
											new Thread(new Runnable() {

												@Override
												public void run() {
													System.out.println("Zmazane eventy: "
															+ CommHelper.getHttpPostResponse(URLs.deleteEvent + "?token=" + Prefs.getString(TOKEN_TAG, context),
																	new String[][] { { "event_id", thisClass.getId() + "" } }));
													EditorDialog.this.dismiss();
												}
											}).start();

										}
									}).show();

							break;
						case R.id.class_edit_ok:
							if (name.getText().toString().isEmpty()) name.setError("Musíte zadať názov udalosti");
							if (desc.getText().toString().isEmpty()) desc.setError("Musíte zadať popis udalosti");

							// send to server, notify adapter
							TClass cl = new TClass(name.getText().toString(), desc.getText().toString(), room.getText().toString(), startDate, endDate, false, false);

							int id = ((Department) depSpin.getSelectedItem()).id;
							int year = yearSpin.getSelectedItemPosition() + 1;
							if (isEdit) {
								new SendClassTask(thisClass.getId(), cl, files, filesToDelete, id, year).execute();
							} else {
								new SendClassTask(cl, files, id, year).execute();
							}
							new DownloadEventsTask().execute();
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
			delete = (Button) v.findViewById(R.id.class_edit_delete);
			ok.setTypeface(tCondBold);
			cancel.setTypeface(tCondBold);
			delete.setTypeface(tCondBold);

			title = (TextView) v.findViewById(R.id.class_edit_title);
			start = (TextView) v.findViewById(R.id.class_edit_start_title);
			end = (TextView) v.findViewById(R.id.class_edit_end_title);
			attach = (TextView) v.findViewById(R.id.class_edit_attach);
			forPeople = (TextView) v.findViewById(R.id.class_edit_for_title);
			forPeople.setTypeface(tCond);
			attach.setTypeface(tCond);
			start.setTypeface(tCond);
			end.setTypeface(tCond);
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
			delete.setOnClickListener(clickListener);

			if (isEdit) {
				v.findViewById(R.id.class_edit_delete_layout).setVisibility(View.VISIBLE);
				name.setText(thisClass.getName());
				desc.setText(thisClass.getDesc());
				room.setText(thisClass.getRoom());

				current = thisClass.getStart();
				startTimeBut.setText(timeF.format(current));
				startDateBut.setText(dateF.format(current));

				current = thisClass.getEnd();
				endTimeBut.setText(timeF.format(current));
				endDateBut.setText(dateF.format(current));

				yearSpin.setSelection(thisClass.getYear() - 1);
				for (Faculty faculties : allFacts) {
					for (Department d : faculties.dep) {
						if (d.id == thisClass.getDepartment()) {
							System.out.println("SAME DEPARTMENT: " + faculties.faculty + "   dep: " + d.id + "   pos:" + departmentsAdapter.getPosition(d) + "    facPos:"
									+ facultyAdapter.getPosition(faculties.faculty));
							facSpin.setSelection(facultyAdapter.getPosition(faculties.faculty));
							depSpin.setSelection(departmentsAdapter.getPosition(d));
						}
					}
				}

				filesToDelete = new ArrayList<Integer>();

				for (final EventFile file : thisClass.getFiles()) {
					final View fv = inflater.inflate(R.layout.upload_item, null);
					TextView text = (TextView) fv.findViewById(R.id.upload_item_text);
					text.setTypeface(tCond);
					ImageButton but = (ImageButton) fv.findViewById(R.id.upload_item_remove);
					but.setBackgroundResource(R.drawable.circle_blue_selector);
					text.setText(file.name);
					but.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// add to delete list
							filesToDelete.add(file.id);
							filesLayout.removeView(fv);
						}
					});
					filesLayout.addView(fv);
				}

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
			if (date == startDate) startDate = c.getTime();
			else if (date == endDate) endDate = c.getTime();
		}

		private void setDate(Date date, int hour, int minute) {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.set(Calendar.HOUR_OF_DAY, hour);
			c.set(Calendar.MINUTE, minute);
			if (date == startDate) startDate = c.getTime();
			else if (date == endDate) endDate = c.getTime();
		}

	}

	private class SendClassTask extends AsyncTask<Void, Void, Void> {

		TClass cl;
		ArrayList<UploadFileHolder> files;
		ArrayList<Integer> filesToDelte;
		int depId, year, id;
		boolean isEdit = false;

		public SendClassTask(int id, TClass cl, ArrayList<UploadFileHolder> files, ArrayList<Integer> filesToDelte, int depId, int year) {
			this(cl, files, depId, year);
			this.id = id;
			this.filesToDelte = filesToDelte;
			isEdit = true;
		}

		public SendClassTask(TClass cl, ArrayList<UploadFileHolder> files, int depId, int year) {
			this.cl = cl;
			this.files = files;
			this.depId = depId;
			this.year = year;
		}

		@Override
		protected void onPreExecute() {
			Toast.makeText(context, "Odosielamudalosť", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			String url = (isEdit ? URLs.editEvent : URLs.createEvent) + "?token=" + Prefs.getString(TOKEN_TAG, context);

			String[] editIdParam = { "id", id + "" };
			if (!isEdit) editIdParam = null;

			String retID = CommHelper.getHttpPostResponse(
					url,
					new String[][] { editIdParam, { "title", cl.getName() }, { "desc", cl.getDesc() }, { "place", cl.getRoom() }, { "start_date", sdf.format(cl.getStart()) },
							{ "end_date", sdf.format(cl.getEnd()) }, { "department", depId + "" }, { "year", year + "" } });

			if (filesToDelte != null && !filesToDelte.isEmpty()) {
				String delteString = "";
				for (int id : filesToDelte) {
					delteString += id + ";";
				}
				String delResponse = CommHelper.getHttpGetReponse(URLs.deleteFiles+"?token=" + Prefs.getString(TOKEN_TAG, context) + "&", new String[][] { { "file_id",
						delteString } });
				System.out.println("Delte: " + delResponse);
			}

			for (UploadFileHolder f : files) {
				CommHelper.sendFile(f.path, context, String.valueOf(retID));
			}

			Log.w("", "token: " + Prefs.getString(TOKEN_TAG, context));

			Log.e("", "title: " + cl.getName() + "  place: " + cl.getRoom() + "  desc: " + cl.getDesc() + "  dep: " + depId + "  year: " + year + "time: " + sdf.format(cl.getStart()));
			Log.e("", "GET BACK: '" + retID + "'");

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Toast.makeText(context, "Udalosť bola odoslaná", Toast.LENGTH_SHORT).show();
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
			text.setText(title[title.length - 1]);
			but = (ImageButton) v.findViewById(R.id.upload_item_remove);
		}

		public void setOnClickListener(OnClickListener listener) {
			but.setOnClickListener(listener);
		}

		public String getPath() {
			return path;
		}

		public ImageButton getButton() {
			return but;
		}

		public View getView() {
			return v;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) return false;
			return path.equals(((UploadFileHolder) o).getPath());
		}
	}

	private class EventPreviewDialog extends DialogFragment {
		private TextView title, desc, room, time, attachTitle, studentsTitle, placeTitle;
		private LinearLayout filesLayout, studentsLayout;
		private ImageButton button;
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
			final View v = inflater.inflate(R.layout.event_teacher_preview, null);

			Bundle b = getArguments();
			thisClass = (TClass) b.getSerializable(TeacherMainFragment.CLASS_TAG);

			title = (TextView) v.findViewById(R.id.event_preview_title);
			desc = (TextView) v.findViewById(R.id.event_preview_desc);
			room = (TextView) v.findViewById(R.id.event_preview_room);
			time = (TextView) v.findViewById(R.id.event_preview_time);
			attachTitle = (TextView) v.findViewById(R.id.event_preview_attach_title);
			studentsTitle = (TextView) v.findViewById(R.id.event_preview_students);
			placeTitle = (TextView) v.findViewById(R.id.event_preview_place_title);
			button = (ImageButton) v.findViewById(R.id.event_item_button);

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle b = new Bundle();
					b.putBoolean(IS_EDIT, true);
					b.putSerializable(CLASS_TAG, thisClass);
					EditorDialog dialog = new EditorDialog();
					dialog.setArguments(b);
					dialog.show(getFragmentManager(), "dialog");
					EventPreviewDialog.this.dismiss();
				}
			});

			title.setTypeface(tCondBold);
			desc.setTypeface(tCond);
			room.setTypeface(tCondBold);
			time.setTypeface(tCond);
			attachTitle.setTypeface(tCondLight);
			studentsTitle.setTypeface(tCondLight);
			placeTitle.setTypeface(tCondLight);

			title.setText(thisClass.getName());
			desc.setText(thisClass.getDesc());
			room.setText(thisClass.getRoom());
			time.setText(sdf.format(thisClass.getStart()) + " - " + sdf.format(thisClass.getEnd()));

			filesLayout = (LinearLayout) v.findViewById(R.id.event_preview_files_layout);

			if (thisClass.getFiles().size() == 0) {
				filesLayout.setVisibility(View.GONE);
			} else {
				attachTitle.setText("Prílohy (" + thisClass.getFiles().size() + ")");
				for (final EventFile file : thisClass.getFiles()) {
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
			}
			String eventId = String.valueOf(thisClass.getId());
			new DownloadStudentsTask() {
				protected void onPostExecute(Void result) {
					studentsLayout = (LinearLayout) v.findViewById(R.id.event_preview_students_layout);

					if (students.size() == 0) {
						studentsLayout.setVisibility(View.GONE);
					} else {
						studentsLayout.setVisibility(View.VISIBLE);
						studentsTitle.setText("Prihlásení študenti (" + students.size() + ")");
						for (final String student : students) {
							TextView studentTV = new TextView(context);
							studentTV.setTypeface(tCond);
							studentTV.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
							studentTV.setTextSize(18);
							studentTV.setText(student);
							studentsLayout.addView(studentTV);
						}
					}
				}
			}.execute(eventId);

			return v;
		}
	}

}