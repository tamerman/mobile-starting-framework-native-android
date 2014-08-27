/**
 * 
 */
package com.phonegap.plugins.audioStreamer;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.kuali.kme.AudioStreamerService;
import org.kuali.kme.Config;
import org.kuali.kme.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.phonegap.plugins.statusBarNotification.StatusNotificationIntent;

public class AudioStreamer extends Plugin {

    protected static final String ME = "AudioStreamer";
    private String browserCallbackId = null;
    private NotificationManager mNotificationManager;
    private Context context;
    
    // Default Constructor
	public AudioStreamer() {}

    public void showNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        context = cordova.getActivity().getApplicationContext();
        mNotificationManager = (NotificationManager) context.getSystemService(ns);

        Notification noti = StatusNotificationIntent.buildNotification(context, "AUDIO_STREAM", "KME Audio", "Currently Playing");
        noti.flags = Notification.FLAG_FOREGROUND_SERVICE;
        mNotificationManager.notify("AUDIO_STREAM".hashCode(), noti);
    }
	
	
	public PluginResult execute(String action, JSONArray args, String callbackId) {
		Log.i(ME, "execute");
		PluginResult.Status status = PluginResult.Status.OK;
		String result = "";
		String streamURL = "";
		this.browserCallbackId = callbackId;
		
		if(action.equals("play")){
			Log.i(ME, "action = play");
			try {
				streamURL = args.getString(0);
				Log.i(ME, "Stream URL" + streamURL);
				
		    	Intent i = new Intent(cordova.getActivity().getApplicationContext(), AudioStreamerService.class);
		    	i.putExtra("streamURL", streamURL);
		    	if(cordova.getActivity().getApplicationContext().startService(i) == null){
					status = PluginResult.Status.ERROR;
		    	}
		    	
		    	if(Config.bShowAudioNotification){
			        String ns = Context.NOTIFICATION_SERVICE;
			        context = cordova.getActivity().getApplicationContext();
			        mNotificationManager = (NotificationManager) context.getSystemService(ns);
			    			 
			        long when = System.currentTimeMillis();
			        Notification noti = new Notification(Config.kStatusIcon, Config.kKMEHomeName + " Audio", when);
			        noti.flags |= Notification.FLAG_ONGOING_EVENT;

			        PackageManager pm = context.getPackageManager();
			        Intent notificationIntent = pm.getLaunchIntentForPackage(context.getPackageName());
			        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			        notificationIntent.putExtra("notificationTag", "AUDIO_STREAM");

			        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			        noti.setLatestEventInfo(context, Config.kKMEHomeName + " Audio", "Currently Playing", contentIntent);
			        			        
			        mNotificationManager.notify("AUDIO_STREAM".hashCode(), noti);
		    	}		        
			} catch (JSONException e) {
				e.printStackTrace();
				status = PluginResult.Status.ERROR;
			}	
		}else if(action.equals("pause")){
			Log.i(ME, "action = pause");
	    	Intent i = new Intent(cordova.getActivity().getApplicationContext(), AudioStreamerService.class);
	    	if(cordova.getActivity().getApplicationContext().stopService(i)){
		    	if(Config.bShowAudioNotification){
		    		String ns = Context.NOTIFICATION_SERVICE;
		    		mNotificationManager = (NotificationManager) context.getSystemService(ns);
		    		mNotificationManager.cancel("AUDIO_STREAM".hashCode());
		    	}
		    }else{
				status = PluginResult.Status.ERROR;	    		
	    	}
		}		
		return new PluginResult(status, result);
	}

}
