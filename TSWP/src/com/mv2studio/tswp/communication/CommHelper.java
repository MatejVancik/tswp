package com.mv2studio.tswp.communication;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
import org.apache.http.client.utils.URLEncodedUtils;
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
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.mv2studio.tswp.core.Prefs;
import com.mv2studio.tswp.ui.TeacherMainFragment;

/**
 * helper to communicate with server. Handle POST and GET requests and responses.
 */
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
	
	public static String getHttpGetReponse(String url, String[][] pairs) {
		try {
			ArrayList<NameValuePair> getPairs = new ArrayList<NameValuePair>();
			for(String[] p : pairs) {
				if(p == null) continue;
				System.out.println(" PAIR: "+p[0]+" -- "+p[1]);
				getPairs.add(new BasicNameValuePair(p[0], p[1]));
			}
			
			String paramString = URLEncodedUtils.format(getPairs, "utf-8");
			url += paramString;
			System.out.println("Sending GET to: "+url);
			final HttpGet getRequest = new HttpGet(url);

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
		System.out.println("POST Requesting url: "+url);
		for(String[] p : pairs) {
			if(p == null) continue;
			System.out.println(" PAIR: "+p[0]+" -- "+p[1]);
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
	
    public static int sendFile(String path, Context context, String eventID) {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost httppost = new HttpPost(URLs.fileUpload+"?token="+Prefs.getString(TeacherMainFragment.TOKEN_TAG, context)+"&event="+eventID);
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
    
    public static void downloadFile(final Context context, final String title, final String name, final int id) {
    	new AsyncTask<Void, Integer, Void>(){
    		
    		protected void onPreExecute() {
    			Toast.makeText(context, "Sťahujem súbor", Toast.LENGTH_SHORT).show();
    		}
    		
    		protected void onPostExecute(Void result) {
    			Toast.makeText(context, "Súbor stiahnutý", Toast.LENGTH_SHORT).show();
    		}
    		
    		@Override
    		protected Void doInBackground(Void... param) {
    		    int count;
    		    try {
    		        URL url = new URL(URLs.fileDownload+"?file="+id);
    		        System.out.println("url: "+url);
    		        URLConnection conexion = url.openConnection();
    		        conexion.connect();
    		        // this will be useful so that you can show a tipical 0-100% progress bar
    		        int lenghtOfFile = conexion.getContentLength();
    		        String mimeType = conexion.getContentType();
    		        // downlod the file
    		        InputStream input = new BufferedInputStream(url.openStream());
    		        
    		        String savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "TSWP" + File.separator ;
    		        
    		        new File(savePath).mkdirs();
    		        
    		        OutputStream output = new FileOutputStream(savePath + File.separator + name);

    		        byte data[] = new byte[1024];

    		        long total = 0;

    		        while ((count = input.read(data)) != -1) {
    		            total += count;
    		            // publishing the progress....
    		            publishProgress((int)(total*100/lenghtOfFile));
    		            System.out.println(data);
    		            output.write(data, 0, count);
    		        }
    		        
    		        output.flush();
    		        output.close();
    		        input.close();
    		    } catch (Exception e) {
    		    	e.printStackTrace();
    		    }
    		    return null;
    		}
    		
    		int prev = 0;
//    		protected void onProgressUpdate(Integer[] values) {
//    			if(prev < values[0]) {
//    				prev = values[0];
//    				activity.setActionBarInfoText(activity.getString(R.string.navbar_downloading_song)+values[0]+"%", this);
//    			}
//    		}
    		
    	}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
    }

}
