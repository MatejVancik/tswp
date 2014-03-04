package com.mv2studio.tswp.communication;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class CommHelper {

	/**
	 * Sends http get request and returns string response
	 * @param url http url
	 * @return String response or null if exception occurs
	 */
	public static String getHttpGetReponse(String url) {
		try {
			final HttpGet getRequest = new HttpGet(url);
			// HttpParams httpParameters = new BasicHttpParams();
			// HttpConnectionParams.setConnectionTimeout(httpParameters, 2000);
			// HttpConnectionParams.setSoTimeout(httpParameters, 2000);

			DefaultHttpClient client = new DefaultHttpClient();// httpParameters);
			client.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));

			HttpResponse execute = client.execute(getRequest);
			HttpEntity entity = execute.getEntity();
			return EntityUtils.toString(entity, HTTP.UTF_8); 
			

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sends http post request and returns string response
	 * @param url http url
	 * @return String response or null if exception occurs
	 */
	public static String getHttpPostResponse(String url) {
		HttpClient httpclient;
		final HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 0);
		HttpConnectionParams.setSoTimeout(httpParams, 1);
		httpclient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(url);
		try {
			HttpResponse reponse = httpclient.execute(httppost);
			return EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;			
	}

}
