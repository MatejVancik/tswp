/**
 * 
 */
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

/**
 * @author Viszlai
 *
 */
public class SuscribtionTask extends AsyncTask<String, Void, Void> {
	boolean error = false;
	Context context;

	public SuscribtionTask(Context context) {
		this.context = context;
	}

	protected void onPreExecute() {
	}

	@Override
	protected Void doInBackground(String... a) {
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();

		pairs.add(new BasicNameValuePair("student_id", a[1]));
		pairs.add(new BasicNameValuePair("event_id", a[2]));

		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(a[0]);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = httpClient.execute(httpPost);

			String suscribed = EntityUtils.toString(response.getEntity());
			Log.e("", "Suscribed: " + suscribed);
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
		
	}

}