/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gcm;

import static com.google.android.gcm.GCMConstants.DEFAULT_INTENT_SERVICE_CLASS_NAME;

import org.kuali.kme.Config;
import org.kuali.kme.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



/**
 * {@link BroadcastReceiver} that receives GCM messages and delivers them to
 * an application-specific {@link GCMBaseIntentService} subclass.
 * <p>
 * By default, the {@link GCMBaseIntentService} class belongs to the application
 * main package and is named
 * {@link GCMConstants#DEFAULT_INTENT_SERVICE_CLASS_NAME}. To use a new class,
 * the {@link #getGCMIntentServiceClassName(Context)} must be overridden.
 */
public class GCMBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "GCMBroadcastReceiver";

    @Override
    public final void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive: " + intent.getAction());
        String className = getGCMIntentServiceClassName(context);
        Log.v(TAG, "GCM IntentService class: " + className);

        
		Bundle extras = intent.getExtras();
//		Set<String> keys = extras.keySet();
//
//        Iterator<String> it = keys.iterator();
//        String key = "";
//        while(it.hasNext()){
//        	key = it.next();
//    		Log.v(TAG, "key:" + key + " value:" + extras.getString(key));
//        }	
		
        
		// Show a Toast Notification if App is not visible (ie in background. Not running, etc) 
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		if(!sharedPrefs.getBoolean("visible", true) && extras != null && extras.containsKey("emergency") && "true".equals(extras.getString("emergency"))){

            CharSequence title = extras.getString("title");
            CharSequence message = extras.getString("message");
            
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.toast_layout, null);

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(Config.kLauncherIcon);
			TextView text = (TextView) layout.findViewById(R.id.text);
			text.setText(title + ":\n" + message);

			Toast toast = new Toast(context);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();			
		}
                
        // Delegates to the application-specific intent service.
        GCMBaseIntentService.runIntentInService(context, intent, className);
        setResult(Activity.RESULT_OK, null /* data */, null /* extra */);
    }

    /**
     * Gets the class name of the intent service that will handle GCM messages.
     */
    protected String getGCMIntentServiceClassName(Context context) {
        String className = context.getPackageName() +
                DEFAULT_INTENT_SERVICE_CLASS_NAME;
        return className;
    }

}
