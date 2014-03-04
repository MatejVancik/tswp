package com.mv2studio.tswp.ui;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mv2studio.tswp.R;

public class WizardFragment extends Fragment {
	
	private ImageButton mainStudent, mainTeacher, studentMais;
	private Button studentNext, teacherNext;
	private Spinner faculty, year, dep;
	private EditText email, pass, name, surname;
	private LinearLayout mainLayout, studentLayout, teacherLayout;
	private Context context;
	private boolean isTeacher, maisLogged;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		context = getActivity();
		
		FrameLayout l = (FrameLayout)inflater.inflate(R.layout.wizard_fragment, null);
		
		mainStudent = (ImageButton) l.findViewById(R.id.wizard_fragment_student_button);
		mainTeacher = (ImageButton) l.findViewById(R.id.wizard_fragment_teacher_button);
		studentMais = (ImageButton) l.findViewById(R.id.wizard_fragment_student_mais);
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
		
		// WIZARD BUTTONS
		OnClickListener buttonsListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.wizard_fragment_student_button:
					isTeacher = false;
					switchView(mainLayout, mainStudent);
					break;
				case R.id.wizard_fragment_teacher_button:
					isTeacher = true;
					switchView(mainLayout, teacherLayout);
					break;
				case R.id.wizard_fragment_student_mais:
				case R.id.wizard_fragment_student_next:
					if(maisLogged) {
						
					} else {
						Toast.makeText(context, "Prosím, najprv si stiahnite rozvrh z MAISu", Toast.LENGTH_LONG).show();
					}
					break;
				case R.id.wizard_fragment_teacher_next:
					String nameText = name.getText().toString();
					String surNameText = surname.getText().toString();
					String emailText = email.getText().toString();
					String passText = pass.getText().toString();
					
					// EMAIL & PASS IS A MUST
					if(emailText.isEmpty() || passText.isEmpty()) {
						Toast.makeText(context, "Musíte zadať email aj heslo.", Toast.LENGTH_LONG).show();
						
					// ERROR. ONLY TUKE TEACHER
					} else if (!emailText.endsWith("@tuke.sk")) {
						Toast.makeText(context, "Dostupné len pre emaily končiace '@tuke.sk'", Toast.LENGTH_LONG).show();
						
					// LOGIN OK
					} else if(nameText.isEmpty() && surNameText.isEmpty()) {
						
						
					// ERROR. ENTER BOTH OR NONE
					} else if (nameText.isEmpty() || surNameText.isEmpty()) {
						Toast.makeText(context, "Zadajte meno aj priezvisko, alebo ani jeden údaj", Toast.LENGTH_LONG).show();
						
					// REGISTRATION
					} else {
						
					}
					break;
				}
			}
		};
		mainStudent.setOnClickListener(buttonsListener);
		mainTeacher.setOnClickListener(buttonsListener);
		studentMais.setOnClickListener(buttonsListener);
		studentNext.setOnClickListener(buttonsListener);
		teacherNext.setOnClickListener(buttonsListener);
		
		
		return l;
	}
	
	
	private void switchView(View oldView, View newView) {
		final Animation anim_out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out); 
	    final Animation anim_in  = AnimationUtils.loadAnimation(context, android.R.anim.fade_in); 

	    oldView.setVisibility(View.GONE);
	    oldView.startAnimation(anim_out);
	    newView.setVisibility(View.VISIBLE);
	    newView.startAnimation(anim_in);
	}
	
	
	
}
