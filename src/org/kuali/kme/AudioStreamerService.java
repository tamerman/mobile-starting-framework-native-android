package org.kuali.kme;


import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class AudioStreamerService extends Service {

	private static final String ME = "AudioStreamerService";
	MediaPlayer mp;
	String 		streamURL;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate(){
		mp = new MediaPlayer(); 		
		mp.setVolume(1.0f, 1.0f);
		mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				Log.i(ME, "onPrepared(): ");
				mp.start();
			}
		});
		super.onCreate();
	}
	
	@Override
	public int onStartCommand (Intent intent, int flags, int startId){
		Bundle extras = intent.getExtras();
		String streamURL = extras.getString("streamURL");
		Log.i(ME, "onStartCommand(): " + streamURL);
		
		try {
			mp.setDataSource(streamURL);
			mp.prepareAsync();
//			mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//				public void onPrepared(MediaPlayer mp) {
//					Log.i(ME, "onPrepared(): ");
//					mp.start();
//				}
//			});			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return START_STICKY;
	}
	
	public void onDestroy(){
		Log.i(ME, "onDestroy()");
		if(mp.isPlaying()){
			mp.stop();
		}
		mp.release();
		super.onDestroy();
	}
	
}
