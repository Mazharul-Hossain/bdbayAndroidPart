package com.bdbay.namespace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class profileRecovery extends Activity {

	MyAppMyState appState;

	boolean flag;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_recovery);

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
		}
		flag = true;
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.backregister:
			finish();
			break;
		case R.id.registernext:
			check_Uname();
			break;
		}
	}

	/************** check Uname Method ***************/
	private void check_Uname() {

		EditText Email = (EditText) findViewById(R.id.enteremailaddresseditText);
		EditText Username = (EditText) findViewById(R.id.enterusernameeditText);

		String u_Email = Email.getText().toString();
		String u_name = Username.getText().toString();

		if (!u_Email.isEmpty() || !u_name.isEmpty()) {

			flag = false;

			if (!u_name.isEmpty()) {
				if (validate_username(u_name)) {
					Username.setTextColor(255);
					flag = false;

					Toast.makeText(
							getApplicationContext(),
							"Sorry ! We can not find the username Defined by you. ",
							Toast.LENGTH_LONG).show();
				} else {
					flag = true;
				}
			}
			if (!u_Email.isEmpty() && (flag == false)) {
				if (validate_Email(u_Email)) {
					Email.setTextColor(255);
					flag = false;

					Toast.makeText(
							getApplicationContext(),
							"Sorry ! We can not find the Email id Defined by you. ",
							Toast.LENGTH_LONG).show();
				} else {
					flag = true;
				}
			}
			if (flag) {
				Toast.makeText(getApplicationContext(),
						"We have send an Email to your email id ",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"You may not have filed all the field ", Toast.LENGTH_LONG)
					.show();
		}

	}

	/************** validate u_name Method ***************/
	private boolean validate_username(String u_name) {

		Pattern p = Pattern.compile("^[A-Za-z0-9._]+$",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(u_name);
		boolean b = m.find();

		if (b) {
			Toast.makeText(getApplicationContext(),
					"There may not have entered your user name correctly ",
					Toast.LENGTH_LONG).show();
		} else {
			JSONObject json = new JSONObject();
			try {
				json.put("u_name", u_name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.d("JSON", json.toString());

			/****************** url of the website ****************/
			String URI = "AndroidUsernameRecover";

			// paring data
			JSONArray jArray = null;
			jArray = appState.loadWebValue(json, URI, "username");

			if (!(jArray == null) && jArray.length() > 0) {
				return true;
			}
			Toast.makeText(getApplicationContext(),
					"User name that you have provided, is not available ",
					Toast.LENGTH_LONG).show();
		}
		return false;
	}

	/************** validate Email Method ***************/
	private boolean validate_Email(String u_Email) {

		Pattern p = Pattern.compile(
				"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(u_Email);
		boolean b = m.find();

		if (b) {
			Toast.makeText(getApplicationContext(),
					"There may not have entered your email correctly ",
					Toast.LENGTH_LONG).show();
		} else {
			JSONObject json = new JSONObject();
			try {
				json.put("Email", u_Email);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Log.d("JSON", json.toString());

			/****************** url of the website ****************/
			String URI = "AndroidEmailRecover";

			// paring data
			JSONArray jArray = null;
			jArray = appState.loadWebValue(json, URI, "email");

			if (!(jArray == null) && jArray.length() > 0) {
				return true;
			}
			Toast.makeText(getApplicationContext(),
					"Email id that you have provided, is not available ",
					Toast.LENGTH_LONG).show();
		}
		return false;
	}
}
