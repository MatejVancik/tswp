package com.mv2studio.tswp.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mv2studio.mynsa.R;
import com.mv2studio.tswp.adapter.SpinnerAdapter;
import com.mv2studio.tswp.communication.StudentRegistrationTask;
import com.mv2studio.tswp.communication.TeacherLoginTask;
import com.mv2studio.tswp.communication.TeacherRegistrationTask;
import com.mv2studio.tswp.core.MaisCalendarParser;
import com.mv2studio.tswp.core.Prefs;
import com.mv2studio.tswp.model.Department;
import com.mv2studio.tswp.model.Faculty;
import com.mv2studio.tswp.ui.MainActivity.OnBackPressedListener;
import com.mv2studio.tswp.util.CommonUtils;

public class WizardFragment extends BaseFragment {

	private Button mainStudent, mainTeacher, studentMais;
	private Button teacherNext;
	private Spinner faculty, year, dep;
	private EditText teacherEmail, studentEmail, pass, name, surname;
	private LinearLayout mainLayout, studentLayout, teacherLayout;
	private TextView welcomeText, studentTitle1, studentTitle2, studentTitle3, teacherTitle1, teacherTitle2;
	private Context context;
	private boolean isTeacher, maisLogged;
	private int step = 0;
	private ArrayAdapter<Department> departmentsAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();

		FrameLayout l = (FrameLayout) inflater.inflate(R.layout.wizard_fragment, null);

		mainStudent = (Button) l.findViewById(R.id.wizard_fragment_student_button);
		mainTeacher = (Button) l.findViewById(R.id.wizard_fragment_teacher_button);
		welcomeText = (TextView) l.findViewById(R.id.wizard_fragment_welcome_text);
		studentMais = (Button) l.findViewById(R.id.wizard_fragment_student_mais);
		teacherNext = (Button) l.findViewById(R.id.wizard_fragment_teacher_next);
		faculty = (Spinner) l.findViewById(R.id.wizard_fragment_student_faculty);
		year = (Spinner) l.findViewById(R.id.wizard_fragment_student_year);
		dep = (Spinner) l.findViewById(R.id.wizard_fragment_student_dep);
		teacherEmail = (EditText) l.findViewById(R.id.wizard_fragment_teacher_email);
		studentEmail = (EditText) l.findViewById(R.id.wizard_fragment_student_email);
		pass = (EditText) l.findViewById(R.id.wizard_fragment_teacher_password);
		name = (EditText) l.findViewById(R.id.wizard_fragment_teacher_name);
		surname = (EditText) l.findViewById(R.id.wizard_fragment_teacher_surname);
		mainLayout = (LinearLayout) l.findViewById(R.id.wizard_fragment_welcome);
		studentLayout = (LinearLayout) l.findViewById(R.id.wizard_fragment_student_layout);
		teacherLayout = (LinearLayout) l.findViewById(R.id.wizard_fragment_teacher_reg_layout);

		studentTitle1 = (TextView) l.findViewById(R.id.wizard_fragment_student_layout_title);
		studentTitle2 = (TextView) l.findViewById(R.id.wizard_fragment_student_layout_title2);
		studentTitle3 = (TextView) l.findViewById(R.id.wizard_fragment_student_layout_title3);
		teacherTitle1 = (TextView) l.findViewById(R.id.wizard_fragment_teacher_layout_title);
		teacherTitle2 = (TextView) l.findViewById(R.id.wizard_fragment_teacher_layout_title2);
		studentTitle1.setTypeface(tThin);
		studentTitle2.setTypeface(tThin);
		studentTitle3.setTypeface(tThin);
		teacherTitle1.setTypeface(tThin);
		teacherTitle2.setTypeface(tThin);
		studentMais.setTypeface(tCondBold);
		mainTeacher.setTypeface(tCondBold);
		name.setTypeface(tCondBold);
		surname.setTypeface(tCondBold);
		pass.setTypeface(tCondBold);
		teacherEmail.setTypeface(tCondBold);
		studentEmail.setTypeface(tCondBold);
		
		// SPINERS
		final ArrayList<Faculty> deps = Department.getDepartments(getActivity());

		List<String> list = new ArrayList<String>();
		for (Faculty dep : deps)
			list.add(dep.faculty);

		// departments
		departmentsAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, new ArrayList<Department>()); // new
																																// ArrayAdapter<String>(context,
																																// android.R.layout.simple_spinner_item,
																																// new
																																// ArrayList<String>());
		departmentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dep.setAdapter(departmentsAdapter);

		// faculties
		ArrayAdapter<String> facultyAdapter = new SpinnerAdapter(context, android.R.layout.simple_spinner_item, list);// new
																														// ArrayAdapter<String>(context,
																														// android.R.layout.simple_spinner_item,
																														// list);
		facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		faculty.setAdapter(facultyAdapter);
		faculty.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				List<Department> items = new ArrayList<Department>();
				for (Faculty fac : deps)
					if (fac.faculty.equals(faculty.getSelectedItem())) items = fac.dep;
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
		List<CharSequence> years = Arrays.asList(getResources().getTextArray(R.array.years));
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
						String studentEmailText = studentEmail.getText().toString();
						// ERROR. ONLY TUKE TEACHER
						if (!studentEmailText.endsWith("@student.tuke.sk")) {
							studentEmail.setError("Dostupné len pre emaily končiace '@student.tuke.sk'");
						}
						Intent getContentIntent = FileUtils.createGetContentIntent();

						Intent intent = Intent.createChooser(getContentIntent, "Vyberte svoj rozvrh");
						startActivityForResult(intent, 1234);
						break;
					case R.id.wizard_fragment_teacher_next:
						String nameText = name.getText().toString();
						String surNameText = surname.getText().toString();
						String emailText = teacherEmail.getText().toString();
						String passText = pass.getText().toString();

						// EMAIL & PASS IS A MUST
						if (emailText.isEmpty() || passText.isEmpty()) {
							teacherEmail.setError("Musíte zadať email aj heslo.");
						}

						// ERROR. ONLY TUKE TEACHER
						if (!emailText.endsWith("@tuke.sk")) {
							teacherEmail.setError("Dostupné len pre emaily končiace '@tuke.sk'");
						}

						// LOGIN OK
						if (nameText.isEmpty() && surNameText.isEmpty()) {
							new TeacherLoginTask(getActivity()) {
								@Override
								protected void onPostExecute(Void result) {
									super.onPostExecute(result);
									if (!error) {
										((MainActivity) context).setRrefreshVisibility(true);
										((MainActivity) context).replaceFragment(new TeacherMainFragment());
									}
								}
							}.execute(emailText, passText);

							// ERROR. ENTER BOTH OR NONE
						} else if (nameText.isEmpty() || surNameText.isEmpty()) {
							text = "Zadajte meno aj priezvisko, alebo ani jeden údaj";

							// REGISTRATION
						} else {
							new TeacherRegistrationTask(getActivity()).execute(nameText, surNameText, emailText, passText);
						}
						break;
				}
				if (text != null) Toast.makeText(context, text, Toast.LENGTH_LONG).show();
			}
		};
		mainStudent.setOnClickListener(buttonsListener);
		mainTeacher.setOnClickListener(buttonsListener);
		studentMais.setOnClickListener(buttonsListener);
		teacherNext.setOnClickListener(buttonsListener);
		mainStudent.setTypeface(tThin);
		mainTeacher.setTypeface(tThin);
		welcomeText.setTypeface(tCondBold);
		teacherNext.setTypeface(tCondBold);

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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
			case 1234:
				if (resultCode == Activity.RESULT_OK) {

					final Uri uri = data.getData();

					// Get the File path from the Uri
					final String path = FileUtils.getPath(getActivity(), uri);
					String type = path.substring(path.length() - 4, path.length()).toUpperCase();
					System.out.println("TPYE: " + type);
					if (!type.equals(".ICS")) {
						Toast.makeText(context, "Súbor musí byť formátu .ICS", Toast.LENGTH_SHORT).show();
					} else {

						new AsyncTask<Void, Void, Boolean>() {

							@Override
							protected Boolean doInBackground(Void... params) {
								try {
									MaisCalendarParser.parseCalendar(context, path);

									Prefs.storeString(StudentEventFragment.YEAR_TAG, String.valueOf(year.getSelectedItemPosition() + 1), context);
									Prefs.storeIntValue(StudentEventFragment.DEP_TAG, departmentsAdapter.getItem(dep.getSelectedItemPosition()).id, context);
									Log.e("", "SAVING DEPARTMENT AS: " + departmentsAdapter.getItem(dep.getSelectedItemPosition()).name);

									Prefs.storeBoolValue(MainActivity.P_LOGGED_KEY, true, context);
									Prefs.storeString(MainActivity.P_USER_TYPE_KEY, MainActivity.P_STUDENT_KEY, context);

								} catch (NumberFormatException e) {
									e.printStackTrace();
									return false;
								} catch (IOException e) {
									e.printStackTrace();
									return false;
								}
								return true;
							}

							@Override
							protected void onPostExecute(Boolean result) {
								if (!result) {
									Toast.makeText(context, "Pri čítaní rozvrhu nastala chyba", Toast.LENGTH_SHORT).show();
									return;
								}
								studentMais.setBackgroundResource(R.drawable.button_selector_green);
							
								studentMais.setText("Dokončiť");
								studentMais.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										if (studentEmail.getError() == null){
										new StudentRegistrationTask(getActivity()).execute(studentEmail.getText().toString());
										Intent i = new Intent(context, MainActivity.class);
										i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(i);
										Toast.makeText(context, "Welcome to TSWP", Toast.LENGTH_SHORT).show();
										}
									}
								});
							}
						}.execute();

					}

				}
				break;
		}

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

}
