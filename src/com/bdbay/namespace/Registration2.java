package com.bdbay.namespace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Registration2 extends Activity {

	MyAppMyState appState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration2);

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
		}
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.backregister:
			finish();
			break;
		case R.id.registerregister:
			check_Uname();
			break;
		}
	}

	private void check_Uname() {

		EditText pin_editText = (EditText) findViewById(R.id.enter_pin_editText);
		EditText reenter_pin_editText = (EditText) findViewById(R.id.reenter_pin_editText);

		String pin = pin_editText.getText().toString();
		String reenter_pin = reenter_pin_editText.getText().toString();

		if (!pin.isEmpty() && !reenter_pin.isEmpty()) {

			JSONObject json = new JSONObject();
			try {
				if (validate_pin(pin, reenter_pin)) {

					json.put("Name", getIntent().getExtras().getString("Name"));
					json.put("Email", getIntent().getExtras()
							.getString("Email"));
					json.put("Address",
							getIntent().getExtras().getString("Address"));
					json.put("Phone", getIntent().getExtras()
							.getString("Phone"));
					json.put("u_name",
							getIntent().getExtras().getString("u_name"));
					json.put("u_pass",
							getIntent().getExtras().getString("u_pass"));
					json.put("Image", getIntent().getExtras()
							.getString("Image"));
					json.put("pin", pin);

					Log.d("JSON", json.toString());

					/****************** url of the website ****************/
					String URI = "AndroidRegister";

					// paring data
					JSONArray jArray = appState.loadWebValue(json, URI,
							"register");
					JSONObject json_data = null;

					if (!(jArray == null) && jArray.length() > 0) {
						json_data = jArray.getJSONObject(0);

						String register = json_data.getString("register");

						if (register.equalsIgnoreCase("ok")) {
							Toast.makeText(getBaseContext(),
									"You are successfully Registered",
									Toast.LENGTH_LONG).show();

							Intent data = new Intent();
							// Activity finished ok, return the data pathName
							setResult(Activity.RESULT_OK, data);
							finish();
						}
					}
				} else {
					pin_editText.setTextColor(Color.rgb(255, 0, 0));
				}
			} catch (JSONException e1) {
				Toast.makeText(getBaseContext(), "No Registration Value Found",
						Toast.LENGTH_LONG).show();
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"You may not have filed all the field ", Toast.LENGTH_LONG)
					.show();
		}

	}

	private boolean validate_pin(String pin, String reenter_pin) {

		if (pin.length() > 7) {
			if (pin.equals(reenter_pin)) {
				Pattern p = Pattern.compile("^[a-z0-9A-Z]",
						Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(pin);
				boolean b = m.find();

				if (!b) {
					Toast.makeText(getApplicationContext(),
							"There may be a special character in your pin ",
							Toast.LENGTH_LONG).show();
				} else {
					return true;
				}

			} else {
				Toast.makeText(getApplicationContext(),
						"You may not have re-entered your pin correctly ",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Your pin may be too short ", Toast.LENGTH_LONG).show();
		}
		return false;
	}
}
