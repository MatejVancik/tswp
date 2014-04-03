package com.mv2studio.tswp.communication;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;

import com.mv2studio.tswp.core.Prefs;
import com.mv2studio.tswp.ui.TeacherMainFragment;

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
	
	 
	public static String getHttpPostResponse(String url, String[][] pairs) {
		ArrayList<NameValuePair> postPairs = new ArrayList<NameValuePair>();
		for(String[] p : pairs) {
			postPairs.add(new BasicNameValuePair(p[0], p[1]));
		}
		
		HttpClient httpclient = new DefaultHttpClient();
		try {
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(postPairs));
			
			HttpResponse reponse = httpclient.execute(httppost);
			return EntityUtils.toString(reponse.getEntity(), HTTP.UTF_8);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;			
	}
	
    public static int sendFile(String path, Context context, int eventID) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost("swp.martinviszlai.com/upload.php?token="+Prefs.getString(TeacherMainFragment.TOKEN_TAG, context)+"&event="+eventID);
            File file = new File(path);
            MultipartEntity mpEntity = new MultipartEntity();
            ContentBody cbFile = new FileBody(file);
            mpEntity.addPart("uploaded_file", cbFile);

            httppost.setEntity(mpEntity);
            System.out.println("executing request " + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            int ret = -1;
            System.out.println("RESPONSE STATUS: " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
                String fromServer = EntityUtils.toString(resEntity).trim();

                System.out.println("RESPONSE FROM SERVER: '" + fromServer + "'");
//                ret = new Integer(fromServer);

                // odpoveď  -  id súboru
                // poslať späť id + tagy!
                // dostať tagy pre subor
                
//                String[] tags = path.replace(Preferences.getPreferences().getFolderPath()+File.separator, "").split(Pattern.quote(System.getProperty("file.separator")));
//                sendTagsForID(ret, tags);
                
            }
            if (resEntity != null) {
                resEntity.consumeContent();
            }

            httpclient.getConnectionManager().shutdown();
            return ret;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }

    }

}
