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

public class TeacherRegistrationTask extends AsyncTask<String, Void, Void> {
	ProgressDialog pd;
	String email, pass;
	boolean error = false;
	Context context;

	public TeacherRegistrationTask(Context context) {
		this.context = context;
	}


	protected void onPreExecute() {
		pd = new ProgressDialog(context);
		pd.setTitle("Registrácia");
		pd.setMessage("Snažím sa registrovať");
		pd.setCancelable(false);
		pd.setIndeterminate(true);
		pd.show();
	}

	@Override
	protected Void doInBackground(String... a) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("first_name", a[0]));
		pairs.add(new BasicNameValuePair("last_name", a[1]));
		pairs.add(new BasicNameValuePair("email", a[2]));
		pairs.add(new BasicNameValuePair("hp", CommonUtils.getHashedString(a[3])));
		email = a[2];
		pass = a[3];

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(URLs.registerUser);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = httpClient.execute(httpPost);

			String token = EntityUtils.toString(response.getEntity());
			error = token.length() != 32;
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
		if(error) {
			pd.dismiss();
			Toast.makeText(context, "Pri registrácii nastala chyba", Toast.LENGTH_SHORT).show();
			return;
		}
		pd.dismiss();
		new TeacherLoginTask(context){
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				((MainActivity)context).replaceFragment(new TeacherMainFragment());
			};
		}.execute(email, pass);
	}

}