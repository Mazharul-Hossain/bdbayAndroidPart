package com.bdbay.namespace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Pin_request extends Activity {

	MyAppMyState appState;
	TextView view_credit_deducted_textView;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pin_request);

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
			finish();
		} else if (appState.getState().equalsIgnoreCase("LogIn")) {
			finish();
		}
		Log.d("Add Item Time",
				"" + getIntent().getExtras().getFloat("requires_credit"));

		view_credit_deducted_textView = (TextView) findViewById(R.id.view_credit_deducted_textView);
		view_credit_deducted_textView
				.setText("After insertinting your pin number, we will deduct : "
						+ String.valueOf(getIntent().getExtras().getFloat(
								"requires_credit"))
						+ " ccredit from your balance");
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.pin_req_back_button:
			finish();
			break;
		case R.id.pin_req_update_button:
			update();
			break;
		}
	}

	/************** Update Method ***************/
	private void update() {
		EditText pin_editText = (EditText) findViewById(R.id.credit_deducted__pin_editText);
		EditText reenter_pin_editText = (EditText) findViewById(R.id.credit_deducted_repin_editText);

		String pin = pin_editText.getText().toString();
		String reenter_pin = reenter_pin_editText.getText().toString();

		if (!pin.isEmpty() && !reenter_pin.isEmpty()) {
			if (validate_pin(pin, reenter_pin)) {

				try {
					JSONObject json = new JSONObject();

					json.put("amount",
							getIntent().getExtras().getFloat("requires_credit"));
					json.put("transaction_id", getIntent().getExtras()
							.getString("transaction_id"));
					json.put("pin", pin);
					json.put("u_name", appState.getState());

					Log.d("Add Item Time", json.toString());

					/****************** url of the website ****************/
					String URI = "AndroidCutMoney";
					String Check = "cut_money";

					// paring data
					JSONArray jArray = appState.loadWebValue(json, URI, Check);
					JSONObject json_data = null;

					if (!(jArray == null) && jArray.length() > 0) {
						json_data = jArray.getJSONObject(0);

						boolean flag = true;
						String check1 = json_data.getString("pin");
						if (check1.equalsIgnoreCase("not ok")) {
							Toast.makeText(getBaseContext(),
									"Your pin number mis-matched",
									Toast.LENGTH_LONG).show();
							flag = false;
						}
						String check2 = json_data.getString("balance");
						if (check2.equalsIgnoreCase("not ok")) {
							Toast.makeText(
									getBaseContext(),
									"Your balance is less thanthe required balance",
									Toast.LENGTH_LONG).show();
							flag = false;
						}
						if (flag) {
							finish_page();
						}
					}
				} catch (JSONException e1) {
					Toast.makeText(getBaseContext(), "No Login Value Found",
							Toast.LENGTH_LONG).show();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	/************** Finish Activity Method ***************/
	private void finish_page() {
		Intent data1 = new Intent();
		// Activity finished ok, return the data pathName
		setResult(Activity.RESULT_OK, data1);
		finish();
	}

	/************** Validate Pin Method ***************/
	private boolean validate_pin(String pin, String reenter_pin) {

		if (pin.length() > 7) {
			if (pin.equals(reenter_pin)) {
				Pattern p = Pattern.compile("[^a-z0-9A-Z ]",
						Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(pin);
				boolean b = m.find();

				if (b) {
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
	/************** Request Pin Class Ends ***************/
}
