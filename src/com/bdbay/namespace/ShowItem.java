package com.bdbay.namespace;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ShowItem extends Activity {

	private static final int REQUEST_CODE = 0;

	int prod_id, prod_num, buying_num;

	MyAppMyState appState;

	private Button prod_order_button;

	private String owner;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_item);

		appState = (MyAppMyState) this.getApplicationContext();
		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
		}

		prod_id = getIntent().getExtras().getInt("prod_id");
		prod_order_button = (Button) findViewById(R.id.prod_order_button);

		ShowProd();
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.prod_back_button:
			finish();
			break;
		case R.id.prod_seller_TextView:
			// show seller profile
			break;
		case R.id.prod_order_button:
			if (owner.compareToIgnoreCase(appState.getState()) == 0) {
				// edit product details
			} else if (appState.getState().compareToIgnoreCase("LogIn") != 0) {
				buy_product();
			} else {
				Intent intent = new Intent(ShowItem.this, Login.class);
				startActivity(intent);
			}
			break;
		}
	}

	private void buy_product() {
		EditText prod_number_editText = (EditText) findViewById(R.id.prod_number_editText);
		if (!prod_number_editText.getText().toString().equals("")) {
			buying_num = Integer.valueOf(prod_number_editText.getText()
					.toString());
		} else {
			buying_num = 0;
		}

		if (buying_num > 0 && buying_num <= prod_num) {
			Intent intent = new Intent(ShowItem.this, Buy_Item.class);
			intent.putExtra("prod_id", prod_id);
			intent.putExtra("buying_num", buying_num);
			Log.d("JSON", "buy product activity starting");
			startActivityForResult(intent, REQUEST_CODE);
		}
	}

	/************** Override on Activity Return Result Method ***************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				finish();
			}
		}
	}

	/************** List Item Method ***************/
	private void ShowProd() {
		try {

			JSONObject json = new JSONObject();
			json.put("prod_id", prod_id);

			Log.d("JSON", json.toString());

			/****************** url of the website ****************/
			String URI = "AndroidShowFinalItem";
			String Check = "show_item";

			// paring data
			JSONArray jArray = appState.loadWebValue(json, URI, Check);

			JSONObject json_data = null;
			Log.d("JSON", "Returned from websote");
			if (!(jArray == null) && jArray.length() > 0) {
				Log.d("JSON", "Entered into parsing");

				json_data = jArray.getJSONObject(0);
				Log.d("JSON", json_data.toString());

				TextView prod_title = (TextView) findViewById(R.id.prod_title);
				prod_title.setText(json_data.getString("name"));
				Log.d("JSON", json_data.getString("name"));

				TextView prod_description = (TextView) findViewById(R.id.prod_description);
				prod_description.setText(json_data.getString("description"));
				Log.d("JSON", json_data.getString("description"));

				TextView prod_price = (TextView) findViewById(R.id.prod_price);
				prod_price.setText("Price : "
						+ String.valueOf(json_data.getDouble("price")));

				ImageView prod_imageview = (ImageView) findViewById(R.id.prod_imageView);
				String image = json_data.getString("image");
				if (!image.isEmpty()) {
					Drawable drawable = LoadImageFromWebOperations(image);
					prod_imageview.setImageDrawable(drawable);
				}

				TextView prod_number_TextView = (TextView) findViewById(R.id.prod_number_TextView);
				prod_num = json_data.getInt("number");
				prod_number_TextView.setText("Available : "
						+ String.valueOf(prod_num));
				Log.d("JSON", "" + prod_number_TextView);

				TextView prod_seller = (TextView) findViewById(R.id.prod_seller_TextView);
				owner = json_data.getString("seller_id");
				prod_seller.setText("Owner : " + json_data.getString("seller"));

				if (owner.compareToIgnoreCase(appState.getState()) == 0) {
					prod_order_button.setText("Edit");
				} else {
					prod_order_button.setText("Order");
				}
			} else {
				finish();
			}
		} catch (JSONException e1) {
			Toast.makeText(getBaseContext(), "No Product Value Value Found",
					Toast.LENGTH_LONG).show();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	}

	/************** Load Image From Web Method ***************/
	private Drawable LoadImageFromWebOperations(String url) {
		URL myFileUrl = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			java.io.InputStream is = conn.getInputStream();

			Bitmap bmImg = BitmapFactory.decodeStream(is);
			Drawable d = new BitmapDrawable(getResources(), bmImg);
			return d;
		} catch (IOException e) {
			// TODO Better error handling
			e.printStackTrace();
			return null;
		}

	}
}
