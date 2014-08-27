/**
 * The MIT License
 * Copyright (c) 2011 Kuali Mobility Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.kuali.kme;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.plugin.GCM.GCMPlugin;


public class GCMIntentService extends GCMBaseIntentService {

  public static final String ME="GCMReceiver";

  public GCMIntentService() {
    super("GCMIntentService");
  }
  private static final String TAG = "GCMIntentService";

  @Override
  public void onRegistered(Context context, String regId) {

    Log.v(ME + ":onRegistered", "Registration ID arrived!");
    Log.v(ME + ":onRegistered", regId);

    JSONObject json;

    try{
      json = new JSONObject().put("event", "registered");
      json.put("regid", regId);

      Log.v(ME + ":onRegisterd", json.toString());

      // Send this JSON data to the JavaScript application above EVENT should be set to the msg type
      // In this case this is the registration ID
      GCMPlugin.sendJavascript( json );
    }catch( JSONException e){
      // No message to the user is sent, JSON failed
      Log.e(ME + ":onRegisterd", "JSON exception");
    }
  }

  @Override
  public void onUnregistered(Context context, String regId) {
    Log.d(TAG, "onUnregistered - regId: " + regId);
  }

  @Override
  protected void onMessage(Context context, Intent intent) {
    Log.d(TAG, "onMessage - context: " + context);

    // Extract the payload from the message
    Bundle extras = intent.getExtras();
    if (extras != null) {
      try
      {
    	  
			Log.v(ME + ":onMessage extras Message:", extras.getString("message"));
			Log.v(ME + ":onMessage extras Title:", extras.getString("title"));
			Log.v(ME + ":onMessage extras URL:", extras.getString("url"));
			Log.v(ME + ":onMessage extras ID:", extras.getString("id"));
			
			JSONObject json;
			json = new JSONObject().put("event", "message");
			json.put("msg", extras.getString("message"));
			json.put("title", extras.getString("title"));
			json.put("id", extras.getString("id"));
			json.put("url", extras.getString("url"));
			json.put("msgcnt", extras.getString("msgcnt"));	        	
      	
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			int icon = Config.kStatusIcon;	        	
			Log.v(ME + ":onMessage NS", ns);

			CharSequence tickerText = extras.getString("title"); 	//Le message qui deroule dans la topBarre a la reception du message
			CharSequence contentTitle = extras.getString("title");	//Le titre de la notification
			CharSequence contentText = extras.getString("message");	//Le texte de la notification
			long when = System.currentTimeMillis();				
						
			Notification notification = new Notification(icon, tickerText, when);
			notification.flags |= Notification.FLAG_AUTO_CANCEL; // ferme la notification quand on clique dessus
			/*notification.defaults |= Notification.DEFAULT_SOUND;*/
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			Intent notificationIntent = new Intent(this, KME.class);

			if(notificationIntent.getBundleExtra(Config.kExtraPushTag) != null){
				Log.v(ME + ":getBundleExtra", Config.kExtraPushTag);
				notificationIntent.removeExtra(Config.kExtraPushTag);
			}
			notificationIntent.putExtra(Config.kExtraPushTag, json.toString());			
			
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			final int HELLO_ID = 1;

			mNotificationManager.notify(HELLO_ID, notification);
			Log.v(ME + ":onMessage ", json.toString());
    	  
			GCMPlugin.sendJavascript( json );
        // Send the MESSAGE to the Javascript application
      }
      catch( JSONException e)
      {
        Log.e(ME + ":onMessage", "JSON exception");
      }        	
    }


  }

  @Override
  public void onError(Context context, String errorId) {
    Log.e(TAG, "onError - errorId: " + errorId);
  }




}
