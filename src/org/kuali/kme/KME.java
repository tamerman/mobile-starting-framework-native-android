/*
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

	 http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
 */

package org.kuali.kme;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.DroidGap;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class KME extends DroidGap{
	
	public static final String TITLE = "ALARM_TITLE";
	public static final String URL = "ALARM_URL";
	public static final String SUBTITLE = "ALARM_SUBTITLE";
	public static final String TICKER_TEXT = "ALARM_TICKER";
	public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
	
	public static final String ME = "KME";	
	public Push push;
	public Registration reg;
	public SharedPreferences sharedPrefs;
	
	private BroadcastReceiver receiver;	
	
	/** Called when the activity resumes. */
	protected void onResume(){
		Log.d(ME, "---- onResume");
		super.onResume();
		
		Bundle extras = getIntent().getExtras();
		if(extras != null){
			Log.d(ME, "Has extras.");          
            if(extras.containsKey(Config.kExtraPushTag)){
	            String msg = extras.getString(Config.kExtraPushTag);
	            getIntent().removeExtra(Config.kExtraPushTag);
	            try {
	            	JSONObject obj = (JSONObject) new JSONTokener(msg).nextValue();
	                push.setTitle(obj.getString("title"));
	                push.setMessage(obj.getString("msg"));     
	                push.setUrl(obj.getString("url"));     
	                push.setID(obj.getLong("id"));
	                push.setUnread(true);              
	            } catch (JSONException e) {
					e.printStackTrace();
				}
	
	            Log.v(ME, "Title: 	" + push.getTitle());
	            Log.v(ME, "Message: " + push.getMessage());
	            Log.v(ME, "URL: " + push.getUrl());
	            Log.v(ME, "ID:      " + push.getID());
            }
			
			String title = extras.getString(TITLE);
			String url = extras.getString(URL);
			String id = extras.getString(NOTIFICATION_ID);
			String sub = extras.getString(SUBTITLE);
			
			JSONObject obj = new JSONObject();
	        try {
				obj.put("msg", sub);
				obj.put("id", id);
				obj.put("url", url);
	        } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			

	        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
			Editor editor = sharedPrefs.edit();
			editor.putString("key", obj.toString());
			editor.commit();
			Log.d("--ONRESUME", "Received: " + obj.toString());		

			
			Log.d(ME, "Key JSON: " + obj.toString());
		}else{
			Log.d(ME, "No extras.");
		}
		
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean("visible", true);
		editor.commit();
		
		IntentFilter filter = new IntentFilter("android.intent.action.MAIN");
		receiver = new BroadcastReceiver(){
			public void onReceive(Context context, Intent intent){
				String title = intent.getStringExtra(TITLE);
				String url = intent.getStringExtra(URL);
				String id = intent.getStringExtra(NOTIFICATION_ID);
				String sub = intent.getStringExtra(SUBTITLE);
								
		        JSONObject obj = new JSONObject();
		        try {
					obj.put("msg", sub);
					obj.put("id", id);
					obj.put("url", url);
		        } catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
				Editor editor = sharedPrefs.edit();
				editor.putString("key", obj.toString());
				editor.commit();
		        
				Log.d("ONRECEIVE", "Received: " + obj.toString());				
				
				appView.loadUrl("javascript:EventNotifications.onIncoming();");
			}
		};
		this.registerReceiver(receiver, filter);
	}	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        super.init();
        super.setIntegerProperty("splashscreen", Config.kSplashImage);

        
		String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath(); // "/data/data/edu.educause.conference/cache";
		Log.d(ME, "--- appCachePath " + appCachePath);
		appView.getSettings().setDomStorageEnabled(true);
		appView.getSettings().setAppCacheMaxSize(1024*1024*8);
		appView.getSettings().setAppCachePath(appCachePath);
		appView.getSettings().setAllowFileAccess(true);
		appView.getSettings().setAppCacheEnabled(true);
		appView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        
        appView.setWebViewClient(new myClient(this, appView));   
        
        // Add Push object to webview as a javascript interface. 
        push = new Push(this, appView);       
        reg = new Registration(this, appView);   

        appView.addJavascriptInterface(push, "Push");
        appView.addJavascriptInterface(reg, "Registration");
		
     
        
        if(Config.kLaunchURL != null){
			super.loadUrl(Config.kLaunchURL, Config.kSplashDelay);        	
        }else{
			super.loadUrl("file:///android_asset/www/index.html");			        	
        }
        
        WebSettings wsettings = appView.getSettings();
        wsettings.setBuiltInZoomControls(false);
        wsettings.setSupportZoom(false);
    }

	/** Called when the activity pauses. */
	protected void onPause(){
		Log.d("KAULIDAYSACTIVITY", "---- onPause");
		super.onPause();
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean("visible", false);
		editor.commit();
		this.unregisterReceiver(this.receiver);
	}
    
    private class myClient extends CordovaWebViewClient{
    	
    	private DroidGap context;

    	public myClient(DroidGap ctx, CordovaWebView webview){
    		super(ctx, webview);
    		context = ctx;
    	}
    	
    	// Don't use this constructor. It's super loses the appView leading to NPE's
		public myClient(DroidGap ctx) {
			super(ctx);
			context = ctx;
		}    	

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon){
			super.onPageStarted(view, url, favicon);
			Log.d(ME, "----- onPageStarted started");
			if(context.hasWindowFocus()){
				context.spinnerStart("", "Loading...");
			}			

			Log.d(ME, "----- onPageStarted finished");
		}
		
		
		@Override
		public void onPageFinished(WebView view, String url){
	    	Log.d(ME, "----- onPageFinish started!");
//	    	Log.d(ME, reg.toString());
	    	
//			view.loadUrl("javascript:updateRegistration()");						    	
//			view.loadUrl("javascript:var hasPushFunctionality = false;");
//			view.loadUrl("javascript:if(hasPushFunctionality){alert('Push is Enabled');}");
			context.spinnerStop();

			super.onPageFinished(view, url);
	    	Log.d(ME, "----- onPageFinish finished!");
		}
    }
    
    private class Registration{
    	private WebView mAppView;
        private DroidGap mGap;
        
        private String deviceName;
        private String regId;
        private String deviceId;        
        private String username;

    	public Registration(DroidGap gap, WebView view){
            mAppView = view;
            mGap = gap;    	
            deviceName = "";
            regId = "";
            deviceId = "";
            username = "";
    	}
    	@JavascriptInterface
		public String getUsername() {
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
	        return sharedPrefs.getString("username", "");
		}
    	
    	@JavascriptInterface
		public void setUsername(String username) {
			this.username = username;
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
	        Editor editor = sharedPrefs.edit();
			editor.putString("username", username);
			editor.commit();
		}
    	@JavascriptInterface
		public String getDeviceName() {
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
	        return sharedPrefs.getString("deviceName", "");
		}
    	@JavascriptInterface
		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
			Editor editor = sharedPrefs.edit();
			editor.putString("deviceName", deviceName);
			editor.commit();
		}
    	@JavascriptInterface
		public String getRegId() {
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
	        return sharedPrefs.getString("regId", "");
		}
    	@JavascriptInterface
		public void setRegId(String regId) {
			this.regId = regId;
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
			Editor editor = sharedPrefs.edit();
			editor.putString("regId", regId);
			editor.commit();			
		}
    	@JavascriptInterface
		public String getDeviceId() {
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
	        return sharedPrefs.getString("deviceId", "");
		}
		
    	@JavascriptInterface
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
	        SharedPreferences sharedPrefs = mGap.getSharedPreferences(ME, 0);
			Editor editor = sharedPrefs.edit();
			editor.putString("deviceId", deviceId);
			editor.commit();
		}
    	@JavascriptInterface
		public String toString(){
			String result = "\n";
			result += "Name: " + this.getDeviceName() + "\n";
			result += "RegId: " + this.getRegId() + "\n";
			result += "DevId: " + this.getDeviceId() + "\n";
			result += "Username: " + this.getUsername() + " "; 
			return result;
		}
		
    }
    
    // Push class to be a javascript interface. 
    private class Push{
    	private String title;
    	private String message;
    	private String url;
    	private Long id;
    	private boolean unread;
    	private WebView mAppView;
        private DroidGap mGap;
        
        public Push(DroidGap gap, WebView view){
            mAppView = view;
            mGap = gap;
            unread = false;
        }    	
        @JavascriptInterface
        public void markRead(){
        	this.title = null;
        	this.message = null;
        	this.url = null;
        	this.id = null;
        	this.unread = false;
        }
        @JavascriptInterface
        public void setUnread(boolean unread){
        	this.unread = unread;
        }
        @JavascriptInterface
        public boolean hasPush(){
        	return unread;
        }
        @JavascriptInterface
        public boolean isUnread(){
        	return unread;
        }
        @JavascriptInterface
        public void setTitle(String title){
        	this.title = title;
        }
        @JavascriptInterface
        public String getTitle(){
        	return title;
        }
        @JavascriptInterface
        public void setMessage(String message){
        	this.message = message;
        }
        @JavascriptInterface
        public String getMessage(){
        	return message;
        }
        @JavascriptInterface
        public void setUrl(String url){
        	this.url = url;
        }
        @JavascriptInterface
        public String getUrl(){
        	return url;
        }        
        @JavascriptInterface
        public void setID(Long id){
        	this.id = id;
        }
        @JavascriptInterface
        public Long getID(){
        	return id;
        }        
    }
    
}

