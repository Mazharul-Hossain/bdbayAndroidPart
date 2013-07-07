package com.bdbay.namespace;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class MyAppMyState extends Application {

	private static volatile MyAppMyState instance = null; // singleton pattern

	private String name;
	private String u_name; // u_name
	private String Type;

	private final String base_URI = "http://10.0.2.2/bdbay_beta/index.php/Welcome/";
	// private final String base_secured_URI =
	// "https://10.0.2.2/bdbay_beta/index.php/Welcome/";

	private Bitmap bitmapImage; // for Dialog photo

	private int Number_Connection_Retry = 0; // for retry internet connection

	public MyAppMyState() {
		super();
		u_name = "LogIn";
		myMap = new LinkedHashMap<Integer, Search_Index_Class>(10);
	}

	/************** singleton pattern ***************/
	public static MyAppMyState getInstance() {
		if (instance == null) {
			synchronized (MyAppMyState.class) {
				if (instance == null) {
					instance = new MyAppMyState();
				}
			}
		}
		return instance;
	}

	/************** get your user name Method ***************/
	public String getState() {
		return u_name;
	}

	public void setState(String s) {
		u_name = s;
	}

	/************** get your name Method ***************/
	public String getName() {
		return name;
	}

	public void setName(String s) {
		name = s;
	}

	/************** get your type Method ***************/
	public String getType() {
		return Type;
	}

	public void setType(String s) {
		Type = s;
	}

	/************** get your bitmap Image Method ***************/
	public Bitmap getbitmapImage() {
		return bitmapImage;
	}

	public void setbitmapImage(Bitmap bm) {
		bitmapImage = bm;
	}

	/************** Check Internet Method ***************/
	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			// There are no active networks.
			return false;
		} else
			return true;
	}

	/************** Http Method Retry Internet Handler Method ***************/

	/************** load Web Value Method ***************/
	public JSONArray loadWebValue(JSONObject json, String provided_URI,
			String Check) {
		InputStream is = null;
		StringBuilder sb;
		String result = new String();
		JSONArray jArray = null;
		try {
			jArray = new JSONArray(result);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		String URI = null;
		if (Check.equalsIgnoreCase("Log_in")) {
			URI = base_URI + provided_URI; // base_secured_URI
			Log.d("Singleton", "Webupdate : Secured Transection is used : "
					+ URI);
		} else {
			URI = base_URI + provided_URI;
			Log.d("Singleton", "Webupdate : Common Transection is used : "
					+ URI);
		}

		if (isNetworkConnected()) {
			try {
				Log.d(Check, json.toString());

				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair(Check, json
						.toString()));

				HttpParams httpParameters = new BasicHttpParams();

				/*
				 * Set the timeout in milliseconds until a connection is
				 * established. The default value is zero, that means the
				 * timeout is not used.
				 */
				int timeoutConnection = 3 * 1000;
				HttpConnectionParams.setConnectionTimeout(httpParameters,
						timeoutConnection);

				/*
				 * Set the default socket timeout (SO_TIMEOUT) in milliseconds
				 * which is the timeout for waiting for data.
				 */
				int timeoutSocket = 5 * 1000;
				HttpConnectionParams
						.setSoTimeout(httpParameters, timeoutSocket);

				HttpClient httpclient = new DefaultHttpClient(httpParameters);
				HttpPost httppost = new HttpPost(URI);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				HttpResponse response = null;
				response = httpclient.execute(httppost);

				HttpEntity entity = null;
				if (response != null) {
					entity = response.getEntity();
				}
				if (entity != null) {
					is = entity.getContent();
				}
				// convert response to string
				if (is != null) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is, "iso-8859-1"), 8);
					sb = new StringBuilder();
					sb.append(reader.readLine() + "\n");

					String line = "0";
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					is.close();
					result = sb.toString();
					Log.d("log_tag", "Result in connection : " + result);

					jArray = new JSONArray(result);
				}
			} catch (Exception e) {
				Number_Connection_Retry++;
				Log.d("exception_tag", "Number_Connection_Retry : "
						+ Number_Connection_Retry);
				if (Number_Connection_Retry < 5) {
					jArray = loadWebValue(json, provided_URI, Check);
				} else {
					Number_Connection_Retry = 0;
					return jArray;
				}
			}
		}
		Number_Connection_Retry = 0;
		return jArray;
	}

	/***************************************************************
	 *********************** Data Structur for Android *************
	 ***************************************************************/

	int search_index;
	LinkedHashMap<Integer, Search_Index_Class> myMap;

	/************** Class having value ***************/
	public class Search_Index_Class {

		public JSONObject json;
		public String provided_URI;
		public String Check;
		public int page_index;
		public int user_mode;
		public int Request_code_webupdate;

		public Search_Index_Class(JSONObject json1, String provided_URI1,
				String Check1, int user_mode1, int Request_code_webupdate1) {
			json = json1;
			provided_URI = provided_URI1;
			Check = Check1;
			page_index = 0;
			user_mode = user_mode1;
			Request_code_webupdate = Request_code_webupdate1;

		}
	}

	/************** create a new session Method ***************/
	public void create_session(JSONObject json1, String provided_URI1,
			String Check1, int user_mode1, int search_index1,
			int Request_code_webupdate1) {

		if (search_index1 == 0) {
			myMap.clear();
		} else {
			if ((search_index + 1) > search_index1) {
				boolean flag = true;
				while (flag) {
					Iterator<Entry<Integer, Search_Index_Class>> it = myMap
							.entrySet().iterator();

					while (it.hasNext()) {
						Entry<Integer, Search_Index_Class> entry = it.next();
						if (search_index1 <= entry.getKey()) {
							myMap.remove(entry.getKey());
							flag = true;
							break;
						}
						flag = false;
					}
				}
			}
		}
		search_index = search_index1;
		Search_Index_Class search_Index_Class = new Search_Index_Class(json1,
				provided_URI1, Check1, user_mode1, Request_code_webupdate1);
		myMap.put(search_index, search_Index_Class);
	}

	/************** returns the class from hash table Method ***************/
	public Search_Index_Class get_index(int search_index1) {
		return myMap.get(search_index1);
	}

	/************** set index in the class Method ***************/
	public void set_index(int search_index1, int offset) {
		Search_Index_Class search_Index_Class = myMap.get(search_index1);
		if (!(search_Index_Class == null)) {
			myMap.remove(search_index1);
			search_Index_Class.page_index += offset;
			myMap.put(search_index1, search_Index_Class);
		}
	}
}
