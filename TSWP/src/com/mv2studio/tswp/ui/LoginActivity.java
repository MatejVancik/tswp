package com.mv2studio.tswp.ui;

import com.mv2studio.tswp.R;
import com.mv2studio.tswp.R.id;
import com.mv2studio.tswp.R.layout;
import com.mv2studio.tswp.R.menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// Views
		final EditText 
		editName = (EditText) findViewById(R.id.activity_login_name),
		editPass = (EditText) findViewById(R.id.activity_login_password);
		
		Button loginButton = (Button) findViewById(R.id.activity_login_button);
		
		loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String name = editName.getText().toString();
				String pass = editPass.getText().toString();
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
