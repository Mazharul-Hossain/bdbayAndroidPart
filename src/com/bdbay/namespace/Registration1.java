package com.bdbay.namespace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registration1 extends Activity {

	private static final int REQUEST_CODE = 0;
	private static final int REQUEST_CODE1 = 1;
	MyAppMyState appState;

	Bitmap Photo_URL = null;
	Button Photo_Button;
	private boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration1);

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
		}
		Photo_Button = (Button) findViewById(R.id.add_item_chose_photo_button);
		flag = true;
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.backregister:
			finish();
			break;
		case R.id.add_item_chose_photo_button:
			photo_choser();
			break;
		case R.id.registernext:
			check_Uname();
			break;
		}
	}

	/************** Photo Chosser Method ***************/
	public void photo_choser() {
		Intent intent = new Intent(Registration1.this, Dialog_Photo.class);
		startActivityForResult(intent, REQUEST_CODE1);
	}

	private void check_Uname() {

		EditText Name = (EditText) findViewById(R.id.enternameeditText);
		EditText Address = (EditText) findViewById(R.id.enteraddresseditText);
		EditText Phone = (EditText) findViewById(R.id.enterphoneeditText);

		String u_Name = Name.getText().toString();
		String u_Address = Address.getText().toString();
		String u_Phone = Phone.getText().toString();

		if (!u_Name.isEmpty() && !u_Address.isEmpty() && !u_Phone.isEmpty()) {

			flag = true;

			if (!validate_Phone(u_Phone)) {
				Phone.setTextColor(Color.rgb(255, 0, 0));
				flag = false;
			}
			if (!validate_Name(u_Name)) {
				Name.setTextColor(Color.rgb(255, 0, 0));
				flag = false;
			}
			if (flag) {
				Intent intent = new Intent(Registration1.this,
						Registration2.class);
				intent.putExtras(getIntent().getExtras());
				intent.putExtra("Phone", u_Phone);
				intent.putExtra("Name", u_Name);
				intent.putExtra("Address", u_Address);

				String p_Image = new String();
				if (!(Photo_URL == null)) {
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					Photo_URL.compress(Bitmap.CompressFormat.JPEG, 90, bao);
					byte[] ba = bao.toByteArray();

					try {
						p_Image = Base64.encodeBytes(ba, 0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				intent.putExtra("Image", p_Image);

				startActivityForResult(intent, REQUEST_CODE);
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"You may not have filed all the field ", Toast.LENGTH_LONG)
					.show();
		}

	}

	private boolean validate_Name(String u_Name) {
		Pattern p = Pattern.compile("^([A-Za-z]|[ ])+$",
				Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(u_Name);
		boolean b = m.find();

		if (!b) {
			Toast.makeText(getApplicationContext(),
					"There may not have entered your name correctly ",
					Toast.LENGTH_LONG).show();
		} else {
			return true;
		}
		return false;
	}

	private boolean validate_Phone(String u_Phone) {
		if (u_Phone.length() == 11) {

			Pattern p = Pattern.compile("^[0-9]+$", Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(u_Phone);
			boolean b = m.find();

			if (!b) {
				Toast.makeText(getApplicationContext(),
						"There may not have entered your phone no correctly ",
						Toast.LENGTH_LONG).show();
			} else {
				JSONObject json = new JSONObject();
				try {
					json.put("Phone", u_Phone);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("JSON", json.toString());

				/****************** url of the website ****************/
				String URI = "AndroidUserphoneRegister";

				// paring data
				JSONArray jArray = appState
						.loadWebValue(json, URI, "userphone");
				if (!(jArray == null) && jArray.length() <= 0) {
					return true;
				}
				Toast.makeText(
						getBaseContext(),
						"Your phone number may be already in use. "
								+ "If u forgot your pasword recover it",
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"Your phone no is not of 11 digits ", Toast.LENGTH_LONG)
					.show();
		}
		return false;
	}

	/************** Override on Activity Return Result Method ***************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				Intent data1 = new Intent();
				// Activity finished ok, return the data pathName
				setResult(Activity.RESULT_OK, data1);
				finish();
			}
		} else if (requestCode == REQUEST_CODE1) {
			if (resultCode == Activity.RESULT_OK) {
				Photo_URL = appState.getbitmapImage();
				Drawable d = new BitmapDrawable(getResources(), Photo_URL);
				Photo_Button.setBackgroundDrawable(d);
			}
		}
	}
}
