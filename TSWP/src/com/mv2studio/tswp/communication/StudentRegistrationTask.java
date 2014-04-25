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
import com.mv2studio.tswp.ui.StudentEventFragment;
import com.mv2studio.tswp.ui.TeacherMainFragment;
import com.mv2studio.tswp.util.CommonUtils;

public class StudentRegistrationTask extends AsyncTask<String, Void, Void> {
	ProgressDialog pd;
	boolean error = false;
	Context context;

	public StudentRegistrationTask(Context context) {
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
		// String[] parts = a[0].split("@");
		// String name = parts[0].split(".")[0];
		// String surname = parts[0].split(".")[1];

		pairs.add(new BasicNameValuePair("email", a[0]));

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost("http://tswp.martinviszlai.com/register_student.php");
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = httpClient.execute(httpPost);

			String student_id = EntityUtils.toString(response.getEntity());
			Log.e("", "Your ids: " + student_id);
			Prefs.storeString(StudentEventFragment.ID_TAG, student_id, context);
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
		if (error) {
			pd.dismiss();
			Toast.makeText(context, "Pri registrácii nastala chyba", Toast.LENGTH_SHORT).show();
			return;
		}
	}

}