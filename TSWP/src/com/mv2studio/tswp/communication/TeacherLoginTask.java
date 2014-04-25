package com.mv2studio.tswp.communication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mv2studio.tswp.core.Prefs;
import com.mv2studio.tswp.ui.MainActivity;
import com.mv2studio.tswp.ui.TeacherMainFragment;
import com.mv2studio.tswp.util.CommonUtils;

public class TeacherLoginTask extends AsyncTask<String, Void, Void>{

	ProgressDialog pd;
	protected boolean error = false;
	private Context context;
	private String emailText, passText;
	
	public TeacherLoginTask(Context context) {
		this.context = context;
	}
	
	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setTitle("Prihlásenie");
		pd.setMessage("Skúšam sa prihlásiť");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.show();
	};
	
	@Override
	protected Void doInBackground(String... a) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		if(a.length == 2) {
			emailText = a[0];
			passText = a[1];
		} else {
			emailText = Prefs.getString(TeacherMainFragment.USER_NAME_TAG, context);
			passText = Prefs.getString(TeacherMainFragment.USER_PASS_TAG, context);
		}
		
		pairs.add(new BasicNameValuePair("email", emailText));
		pairs.add(new BasicNameValuePair("hp", CommonUtils.getHashedString(passText)));
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://tswp.martinviszlai.com/login.php");
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = httpClient.execute(httpPost);
			
			String token = EntityUtils.toString(response.getEntity());
			error = token.length() != 32;
			if (error) return null;
			Log.e("", "Your token: "+token);
			Prefs.storeString(TeacherMainFragment.TOKEN_TAG, token, context);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	protected void onPostExecute(Void result) {
		pd.dismiss();
		if(error) {
			Toast.makeText(context, "Pri prihlásení nastala chyba", Toast.LENGTH_SHORT).show();
			return;
		}
		Prefs.storeBoolValue(MainActivity.P_LOGGED_KEY, true, context);
		Prefs.storeBoolValue(MainActivity.P_TEACHER_KEY, true, context);
		
		// store email and pass on successful login
		Prefs.storeString(TeacherMainFragment.USER_NAME_TAG, emailText, context);
		Prefs.storeString(TeacherMainFragment.USER_PASS_TAG, passText, context);
		
	}
}