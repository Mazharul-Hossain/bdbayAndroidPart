package com.bdbay.namespace;

import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

	EditText Username;
	EditText Password;

	MyAppMyState appState = null;
	String state;

	JSONArray jArray;
	String result = null;
	InputStream is = null;
	StringBuilder sb = null;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		appState = (MyAppMyState) this.getApplicationContext();
		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
		}
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.register_now_login_textView:
			Intent intent = new Intent(Login.this, Registration.class);
			startActivity(intent);
			finish();
			break;
		case R.id.forgot_password_textView:
			Intent intent1 = new Intent(Login.this, profileRecovery.class);
			startActivity(intent1);
			finish();
			break;
		case R.id.login:
			login();
			break;
		}
	}

	/************** Log in Method ***************/
	public void login() {

		Username = (EditText) findViewById(R.id.username);
		Password = (EditText) findViewById(R.id.password);

		String u_name = Username.getText().toString();
		String u_pass = Password.getText().toString();

		String URI = null;
		String Check = null;

		if (!u_name.isEmpty() && !u_pass.isEmpty()) {

			JSONObject json = new JSONObject();
			try {
				json.put("u_name", u_name);
				json.put("u_pass", u_pass);
				Log.d("JSON", json.toString());

				URI = "AndroidLogin";
				Check = "Log_in";

				JSONArray jArray = appState.loadWebValue(json, URI, Check);
				JSONObject json_data = null;

				String u_name1 = null;
				String name = null;
				String type = null;

				if (!(jArray == null) && jArray.length() > 0) {
					for (int i = 0; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);
						u_name1 = json_data.getString("u_name");
						name = json_data.getString("name");
						type = json_data.getString("type");
					}
					appState.setState(u_name1);
					appState.setName(name);
					appState.setType(type);
				}
				if (!appState.getState().equalsIgnoreCase("login")) {
					Toast.makeText(getApplicationContext(),
							"You have login " + appState.getName(),
							Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(),
							"You have failed to login ", Toast.LENGTH_LONG)
							.show();
				}

				finish();
			} catch (JSONException e) {
				Log.e("log_tag", "Error in login : " + e.toString());
			}

		} else {
			Toast.makeText(getApplicationContext(),
					"You may not have filed all the item ", Toast.LENGTH_LONG)
					.show();
		}
	}
}
