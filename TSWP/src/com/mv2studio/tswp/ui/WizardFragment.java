package com.mv2studio.tswp.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mv2studio.tswp.R;
import com.mv2studio.tswp.ui.MainActivity.OnBackPressedListener;

public class WizardFragment extends BaseFragment {

	private Button mainStudent, mainTeacher, studentMais;
	private Button studentNext, teacherNext;
	private Spinner faculty, year, dep;
	private EditText email, pass, name, surname;
	private LinearLayout mainLayout, studentLayout, teacherLayout;
	private TextView welcomeText, sutdentTitle1, studentTitle2, studentTitle3, teacherTitle1, teacherTitle2;
	private Context context;
	private boolean isTeacher, maisLogged;
	private int step = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		final Editor editor = prefs.edit();

		FrameLayout l = (FrameLayout) inflater.inflate(R.layout.wizard_fragment, null);

		mainStudent = (Button) l.findViewById(R.id.wizard_fragment_student_button);
		mainTeacher = (Button) l.findViewById(R.id.wizard_fragment_teacher_button);
		welcomeText = (TextView) l.findViewById(R.id.wizard_fragment_welcome_text);
		studentMais = (Button) l.findViewById(R.id.wizard_fragment_student_mais);
		studentNext = (Button) l.findViewById(R.id.wizard_fragment_student_next);
		teacherNext = (Button) l.findViewById(R.id.wizard_fragment_teacher_next);
		faculty = (Spinner) l.findViewById(R.id.wizard_fragment_student_faculty);
		year = (Spinner) l.findViewById(R.id.wizard_fragment_student_year);
		dep = (Spinner) l.findViewById(R.id.wizard_fragment_student_dep);
		email = (EditText) l.findViewById(R.id.wizard_fragment_teacher_email);
		pass = (EditText) l.findViewById(R.id.wizard_fragment_teacher_password);
		name = (EditText) l.findViewById(R.id.wizard_fragment_teacher_name);
		surname = (EditText) l.findViewById(R.id.wizard_fragment_teacher_surname);
		mainLayout = (LinearLayout) l.findViewById(R.id.wizard_fragment_welcome);
		studentLayout = (LinearLayout) l.findViewById(R.id.wizard_fragment_student_layout);
		teacherLayout = (LinearLayout) l.findViewById(R.id.wizard_fragment_teacher_reg_layout);

		sutdentTitle1 = (TextView) l.findViewById(R.id.wizard_fragment_student_layout_title);
		studentTitle2 = (TextView) l.findViewById(R.id.wizard_fragment_student_layout_title2);
		studentTitle3 = (TextView) l.findViewById(R.id.wizard_fragment_student_layout_title3);
		teacherTitle1 = (TextView) l.findViewById(R.id.wizard_fragment_teacher_layout_title);
		teacherTitle2 = (TextView) l.findViewById(R.id.wizard_fragment_teacher_layout_title2);
		sutdentTitle1.setTypeface(tThin);
		studentTitle2.setTypeface(tThin);
		studentTitle3.setTypeface(tThin);
		teacherTitle1.setTypeface(tThin);
		teacherTitle2.setTypeface(tThin);
		studentMais.setTypeface(tCondBold);
		studentNext.setTypeface(tCondBold);
		mainTeacher.setTypeface(tCondBold);
		name.setTypeface(tCondBold);
		surname.setTypeface(tCondBold);
		pass.setTypeface(tCondBold);
		email.setTypeface(tCondBold);

		// SPINERS
		final ArrayList<Faculty> deps = getDepartments();

		List<String> list = new ArrayList<String>();
		for (Faculty dep : deps)
			list.add(dep.faculty);

		// departments
		final ArrayAdapter<String> departmentsAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, new ArrayList<String>()); //new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, new ArrayList<String>());
		departmentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dep.setAdapter(departmentsAdapter);

		// faculties
		ArrayAdapter<String> facultyAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, list);//new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
		facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		faculty.setAdapter(facultyAdapter);
		faculty.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				List<String> items = new ArrayList<String>();
				for (Faculty fac : deps)
					if (fac.faculty.equals(faculty.getSelectedItem()))
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
		
		// years
		List<CharSequence> years =  Arrays.asList(getResources().getTextArray(R.array.years));
		final ArrayAdapter<String> yearAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, years);
		year.setAdapter(yearAdapter);
		

		// WIZARD BUTTONS
		OnClickListener buttonsListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = null;

				switch (v.getId()) {
				case R.id.wizard_fragment_student_button:
					step++;
					isTeacher = false;
					switchView(mainLayout, studentLayout);
					break;
				case R.id.wizard_fragment_teacher_button:
					step++;
					isTeacher = true;
					switchView(mainLayout, teacherLayout);
					break;
				case R.id.wizard_fragment_student_mais:
				case R.id.wizard_fragment_student_next:
					if (maisLogged) {
						Toast.makeText(context, "Welcome to TSWP", Toast.LENGTH_SHORT).show();
						editor.putBoolean(MainActivity.P_LOGGED_KEY, true);
						editor.putString(MainActivity.P_USER_TYPE_KEY, MainActivity.P_STUDENT_KEY);
						editor.apply();
						Intent i = new Intent(context, MainActivity.class);
						i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
					} else {
						text = "Prosím, najprv si stiahnite rozvrh z MAISu";
					}
					break;
				case R.id.wizard_fragment_teacher_next:
					String nameText = name.getText().toString();
					String surNameText = surname.getText().toString();
					String emailText = email.getText().toString();
					String passText = pass.getText().toString();

					// EMAIL & PASS IS A MUST
					if (emailText.isEmpty() || passText.isEmpty()) {
						text = "Musíte zadať email aj heslo.";

						// ERROR. ONLY TUKE TEACHER
					} else if (!emailText.endsWith("@tuke.sk")) {
						text = "Dostupné len pre emaily končiace '@tuke.sk'";

						// LOGIN OK
					} else if (nameText.isEmpty() && surNameText.isEmpty()) {
						// TODO: GET RESPONSE FROM SERVER

						editor.putBoolean(MainActivity.P_LOGGED_KEY, true);
						editor.putString(MainActivity.P_USER_TYPE_KEY, MainActivity.P_TEACHER_KEY);
						editor.apply();

						// ERROR. ENTER BOTH OR NONE
					} else if (nameText.isEmpty() || surNameText.isEmpty()) {
						text = "Zadajte meno aj priezvisko, alebo ani jeden údaj";

						// REGISTRATION
					} else {
						// TODO: SEND REQUEST FOR REGISTRATION TO SERVER.

						text = "Prosím, potvrdte email.";
					}
					break;
				}
				if (text != null)
					Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			}
		};
		mainStudent.setOnClickListener(buttonsListener);
		mainTeacher.setOnClickListener(buttonsListener);
		studentMais.setOnClickListener(buttonsListener);
		studentNext.setOnClickListener(buttonsListener);
		teacherNext.setOnClickListener(buttonsListener);
		mainStudent.setTypeface(tThin);
		mainTeacher.setTypeface(tThin);
		welcomeText.setTypeface(tCondBold);

		((MainActivity) context).setOnBackPressedListener(new OnBackPressedListener() {
			@Override
			public void doBack() {
				if (step == 1) {
					step--;
					switchView(isTeacher ? teacherLayout : studentLayout, mainLayout);
				} else {
					((MainActivity) context).finish();
				}
			}
		});

		return l;
	}

	public class Faculty {
		public String faculty;
		public List<String> dep;
	}

	private void switchView(View oldView, View newView) {
		final Animation anim_out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
		final Animation anim_in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
		anim_out.setDuration(100);
		anim_in.setDuration(300);

		oldView.setVisibility(View.GONE);
		oldView.startAnimation(anim_out);
		newView.setVisibility(View.VISIBLE);
		newView.startAnimation(anim_in);
	}

	private ArrayList<Faculty> getDepartments() {
		ArrayList<Faculty> deps = new ArrayList<WizardFragment.Faculty>();

		try {
			JSONArray obj = new JSONArray(getJsonFromAssets("school.json"));
			for (int i = 0; i < obj.length(); i++) {
				JSONObject dep = obj.getJSONObject(i);
				Faculty department = new Faculty();
				department.faculty = dep.getString("FACULTY");

				// LOAD BC
				List<String> list = new ArrayList<String>();
				JSONArray array = dep.getJSONArray("DEPARTMENT");
				for (int j = 0; j < array.length(); j++) {
					list.add(array.getString(j));
				}
				department.dep = list;

				deps.add(department);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return deps;
	}

	private String getJsonFromAssets(String path) {
		String jsonString = null;
		AssetManager am = getActivity().getAssets();
		try {
			InputStream is = am.open(path);
			int length = is.available();
			byte[] data = new byte[length];
			is.read(data);
			jsonString = new String(data);
		} catch (IOException e1) {
			// e1.printStackTrace();
		}

		return jsonString;
	}

	private class SpinnerAdapter extends ArrayAdapter {

		public SpinnerAdapter(Context context, int resource) {
			super(context, resource);
		}
		
		public SpinnerAdapter(Context context, int resource, List objects) {
			super(context, resource, objects);
		}

		public TextView getView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) super.getView(position, convertView, parent);
			v.setTypeface(tCondBold);
			v.setTextSize(20);
			return v;
		}

		public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
			TextView v = (TextView) super.getView(position, convertView, parent);
			v.setTypeface(tCondLight);
			v.setTextSize(20);
			v.setPadding(20, 15, 20, 15);
			return v;
		}

	}
}
