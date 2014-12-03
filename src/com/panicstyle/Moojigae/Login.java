package com.panicstyle.Moojigae;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class Login {

	/*
	 * 		HttpClient httpClient = new DefaultHttpClient();
		HttpContext httpContext = new BasicHttpContext();
		CookieStore cookieStore = new BasicCookieStore();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

	 */
	private String userID;
	private String userPW;
	
	public String getUserID() {
		return userID;
	}
	
	public Boolean GetUserInfoXML(Context context) {
	    InputStream in = null;
	    String fileName = "LoginInfo.xml";
	    try {
		    FileInputStream input = context.openFileInput(fileName);
	    	in = new BufferedInputStream(input);
	    	StringBuffer out = new StringBuffer();
	    	byte[] buffer = new byte[4094];
	    	int readSize;
	    	while ( (readSize = in.read(buffer)) != -1) {
	    	    out.append(new String(buffer, 0, readSize));
	    	}
	    	String data = out.toString();
	    	
	    	XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	        factory.setNamespaceAware(true);
	        XmlPullParser xpp = factory.newPullParser();

	        xpp.setInput( new StringReader(data) );
	        int eventType = xpp.getEventType();
	        int type = 0;
	        while (eventType != XmlPullParser.END_DOCUMENT) {
	        	if(eventType == XmlPullParser.START_DOCUMENT) {
	        		System.out.println("Start document");
	        	} else if(eventType == XmlPullParser.START_TAG) {
	        		System.out.println("Start tag "+xpp.getName());
	        		String strTag = xpp.getName();
	        		if (strTag.equalsIgnoreCase("ID")) {
	        			type = 1;
	        		} else if (strTag.equalsIgnoreCase("Password")) {
	        			type = 2;
	        		} else {
	        			type = 0;
	        		}
	        	} else if (eventType == XmlPullParser.END_TAG) {
	        		System.out.println("End tag "+xpp.getName());
	        		type = 0;
	        	} else if (eventType == XmlPullParser.TEXT) {
	        		System.out.println("Text "+xpp.getText());
	        		if (type == 1) {
	        			userID = xpp.getText();
	        		} else if (type == 2) {
	        			userPW = xpp.getText();
	        		}
	        	}
	        	eventType = xpp.next();
	        }
	        System.out.println("End document");
	        return true;
	     } catch( Exception e ) {
	    	 System.out.println(e.getMessage());
	    	 return false;
	     } finally {
	    	 if (in != null){
	    		 try {
	    			 in.close();
	    		 } catch( IOException ioe ) {
	    		 }
	    	 }
	     }
	}
	
	
	public int LoginTo(HttpClient httpClient, HttpContext httpContext, Context context) {
	
		String url = "http://thegil.org/2014/bbs/login_check.php";//변경
		String logoutURL = "http://thegil.org/2014/bbs/logout.php";//변경

		if (!GetUserInfoXML(context)) {
	    	return -1;
		}
		
		HttpRequest httpRequest = new HttpRequest();
		
		String referer = "http://thegil.org/2014/bbs/login.php";
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("mb_id", userID));//변경
		nameValuePairs.add(new BasicNameValuePair("mb_password", userPW));//변경
		//요소지움 변경
        httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);

		httpRequest.requestGet(httpClient, httpContext, logoutURL, referer, "utf-8");//인코딩 변경
		String result = httpRequest.requestPost(httpClient, httpContext, url, nameValuePairs, referer, "utf-8");//인코딩 변경
        System.out.println("***********************************************************");
        System.out.println(result);
        System.out.println("***********************************************************");

        if( result.indexOf( "<div id=\"hd_login_msg\">" ) > 0 ) {
            System.out.println( "Login Success" );

            return 1;
        } else {
			String errMsg = "Login Fail";
            Log.i("tag", "Login Fail");
	    	System.out.println(errMsg);
	        return 0;
		}
	}
}
