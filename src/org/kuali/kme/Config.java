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

import org.kuali.kme.R;

public class Config {

	// This is the the server address of your KME installation.  
//	public static final String	PRD_SERVER 		= "https://m.iu.edu?native=yes&phonegap=2.2.0&header=show";
	public static final String	kLaunchURL 		= "http://143.160.208.51:9999/mdot?native=yes&phonegap=2.2.0&header=show";
	
	// Be sure to change this to the name that will be used in your KME installation.
	public static final String	kKMEHomeName	= "Kuali Mobile";

	// Change all but the ".push" at the end to match your apps package name. 
	public static final String	kExtraPushTag	= "org.kuali.kme.push";

	// Change all but the ".event" at the end to match your apps package name. 
	public static final String 	kExtraEventTag	= "org.kuali.kme.event";

	// Normally for local Notifications. Currently Unused. 
	// public static final String 	UPCOMING		= "Upcoming Event:\n";	
		
	// This is the amount of time you would like the splashscreen to be displayed. 
	// This parameter is in milliseconds. 
	public static final int 	kSplashDelay 	= 3000;	

	// This is the integer key for your splash image, as defined by the generated 
	// R.java. "ic_splash" will correspond to your filename. 
	public static final int 	kSplashImage 	= R.drawable.splash;	

	// This is the integer key for your launcher icon image, as defined by the 
	// generated R.java. "ic_launcher" will correspond to your filename. 
	public static final int 	kLauncherIcon	= R.drawable.ic_launcher;

	// This is the integer key for your status bar icon image, as defined by the 
	// generated R.java. "ic_stat_notify" will correspond to your filename. 
	public static final int		kStatusIcon		= R.drawable.ic_stat_notify;
	
	// If you are using the AudioStreamer plugin this will toggle whether to show a status bar notification 
	// stating that an audio stream is currently laying.  
	public static final boolean bShowAudioNotification = true;
	
}
