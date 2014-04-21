package com.mv2studio.tswp.ui;

import java.io.File;

import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mv2studio.mynsa.R;

public class WebFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.webview, null);

		WebView w = (WebView) v.findViewById(R.id.webview);
		w.loadUrl("http://student.tuke.sk/");
		w.getSettings().setJavaScriptEnabled(true);
		
		// This will handle downloading. It requires Gingerbread, though
	    final DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

	    // This is where downloaded files will be written, using the package name isn't required
	    // but it's a good way to communicate who owns the directory
	    final File destinationDir = new File (Environment.getExternalStorageDirectory(), getActivity().getPackageName());
	    if (!destinationDir.exists()) {
	        destinationDir.mkdir(); // Don't forget to make the directory if it's not there
	    }
	    
	    w.setWebViewClient(new WebViewClient() {
	        @Override
	        public boolean shouldOverrideUrlLoading (WebView view, String url) {
	            boolean shouldOverride = false;
	            // We only want to handle requests for mp3 files, everything else the webview
	            // can handle normally
	            if (url.toLowerCase().endsWith(".ics")) {
	                shouldOverride = true;
	                Uri source = Uri.parse(url);

	                // Make a new request pointing to the mp3 url
	                DownloadManager.Request request = new DownloadManager.Request(source);
	                // Use the same file name for the destination
	                File destinationFile = new File (destinationDir, source.getLastPathSegment());
	                request.setDestinationUri(Uri.fromFile(destinationFile));
	                // Add it to the manager
	                manager.enqueue(request);
	            }
	            return shouldOverride;
	        }
	    });
		
	    w.setDownloadListener(new DownloadListener() {
	        public void onDownloadStart(String url, String userAgent,
	                String contentDisposition, String mimetype,
	                long contentLength) {
	        		System.out.println("url: "+url);
	        }
	    });
		
		
		return v;
	}
	
}
