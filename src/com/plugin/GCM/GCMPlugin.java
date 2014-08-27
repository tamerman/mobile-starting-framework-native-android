package com.plugin.GCM;

import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.apache.cordova.api.PluginResult.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gcm.GCMRegistrar;


/**
 * @author awysocki
 *
 */

public class GCMPlugin extends Plugin {

	public static final String ME="GCMPlugin";

	public static final String REGISTER="register";
	public static final String UNREGISTER="unregister";

	public static Plugin gwebView;
	private static String gECB;
	private static String gSenderID;

	@Override
	public PluginResult execute(String action, JSONArray data, String callbackId){

		PluginResult result = null;

		Log.v(ME + ":execute", "action=" + action);

		if (REGISTER.equals(action)) {

			Log.v(ME + ":execute", "data=" + data.toString());

			try {

				JSONObject jo= new JSONObject(data.toString().substring(1, data.toString().length()-1));

				gwebView = this;

				Log.v(ME + ":execute", "jo=" + jo.toString());

				gECB = (String)jo.get("ecb");
				gSenderID = (String)jo.get("senderID");

				Log.v(ME + ":execute", "ECB="+gECB+" senderID="+gSenderID );



				// Added this code so we wouldn't constantly be registering, but only registering when necessary. 
				// Take from Step 4 on http://developer.android.com/guide/google/gcm/gs.html
				GCMRegistrar.checkDevice(this.cordova.getContext());
				Log.v(ME + ":execute", "*** REACHES HERE: checkDevice***");


//				GCMRegistrar.checkManifest(this.cordova.getContext());
				Log.v(ME + ":execute", "*** REACHES HERE: checkManifest***");


				final String regId = GCMRegistrar.getRegistrationId(this.cordova.getContext());
				if (regId.equals("")) {
					Log.v(ME + ":execute", "regId is null");
					GCMRegistrar.register(this.cordova.getContext(), gSenderID);
				} else {
					Log.v(ME + ":execute", "Already registered");
				}

				// GCMRegistrar.register(this.ctx.getContext(), gSenderID);


				Log.v(ME + ":execute", "GCMRegistrar.register called ");

				result = new PluginResult(Status.OK);
			}
			catch (JSONException e) {
				Log.e(ME, "Got JSON Exception "
						+ e.getMessage());
				result = new PluginResult(Status.JSON_EXCEPTION);
			}
		}else if (UNREGISTER.equals(action)) {

			GCMRegistrar.unregister(this.ctx.getContext());
			Log.v(ME + ":" + UNREGISTER, "GCMRegistrar.unregister called ");

		}else{
			result = new PluginResult(Status.INVALID_ACTION);
			Log.e(ME, "Invalid action : "+action);
		}

		return result;
	}


	public static void sendJavascript( JSONObject _json ){
		String _d =  "javascript:"+gECB+"(" + _json.toString() + ")";
		Log.v(ME + ":sendJavascript", _d);

		if(gwebView != null){
			gwebView.sendJavascript( _d );
		}else{
			Log.v(ME + ":Error", "No WebView Available");
		}
	}


	/**
	 * Gets the Directory listing for file, in JSON format
	 * @param file The file for which we want to do directory listing
	 * @return JSONObject representation of directory list. e.g {"filename":"/sdcard","isdir":true,"children":[{"filename":"a.txt","isdir":false},{..}]}
	 * @throws JSONException
	 */


}
