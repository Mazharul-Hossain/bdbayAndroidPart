package com.bdbay.namespace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Registration extends Activity {

	private static final int REQUEST_CODE = 0;

	MyAppMyState appState;

	boolean flag;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration);

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
		EditText Password = (EditText) findViewById(R.id.enterpasswordeditText);
		EditText Repassword = (EditText) findViewById(R.id.reEnterpasswordeditText);

		String u_Email = Email.getText().toString();
		String u_name = Username.getText().toString();
		String u_pass = Password.getText().toString();
		String u_pass2 = Repassword.getText().toString();

		if (!u_Email.isEmpty() && !u_name.isEmpty() && !u_pass.isEmpty()
				&& !u_pass2.isEmpty()) {

			flag = true;

			if (!validate_username(u_name)) {
				Username.setTextColor(Color.rgb(255, 0, 0));
				flag = false;
			}
			if (!validate_Email(u_Email)) {
				Email.setTextColor(Color.rgb(255, 0, 0));
				flag = false;
			}
			if (!validate_Password(u_pass, u_pass2)) {
				Password.setTextColor(Color.rgb(255, 0, 0));
				flag = false;
			}
			if (flag) {
				Intent intent = new Intent(Registration.this,
						Registration1.class);
				intent.putExtra("u_name", u_name);
				intent.putExtra("u_pass", u_pass);
				intent.putExtra("Email", u_Email);

				startActivityForResult(intent, REQUEST_CODE);
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

		if (!b) {
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
			String URI = "AndroidUsernameRegister";

			// paring data
			JSONArray jArray = null;
			jArray = appState.loadWebValue(json, URI, "username");

			if (!(jArray == null) && jArray.length() <= 0) {
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

		if (!b) {
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
			String URI = "AndroidEmailRegister";

			// paring data
			JSONArray jArray = null;
			jArray = appState.loadWebValue(json, URI, "email");

			if (!(jArray == null) && jArray.length() <= 0) {
				return true;
			}
			Toast.makeText(getApplicationContext(),
					"Email id that you have provided, is not available ",
					Toast.LENGTH_LONG).show();
		}
		return false;
	}

	/************** validate Password Method ***************/
	private boolean validate_Password(String u_pass, String u_pass2) {
		if (u_pass.length() > 5) {
			if (u_pass.equals(u_pass2)) {
				Pattern p = Pattern.compile("^([A-Za-z0-9_.]|[ ])+",
						Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(u_pass);
				boolean b = m.find();

				if (!b) {
					Toast.makeText(
							getApplicationContext(),
							"There may be a special character in your password ",
							Toast.LENGTH_LONG).show();
				} else {
					return true;
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"You may not have re-entered your password correctly ",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Your password may be too short ", Toast.LENGTH_LONG)
					.show();
		}
		return false;
	}

	/************** Override on Activity Return Result Method ***************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("Add Item", "If starting 6");
		if (requestCode == REQUEST_CODE) {
			Log.d("Add Item", "If starting 7");
			if (resultCode == Activity.RESULT_OK) {
				finish();
			}
		}
	}
}
