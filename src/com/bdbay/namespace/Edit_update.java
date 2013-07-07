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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Edit_update extends Activity {

	private static final int REQUEST_CODE = 0;

	int prod_id, prod_num, buying_num;

	MyAppMyState appState;

	private Button prod_order_button;

	private String owner;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_update);

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
		case R.id.prod_delete_button:
			delete_prod();
			break;
		case R.id.prod_edit_button:
			edit();
			break;
		case R.id.prod_order_button:
			next_page();
			finish_page();
			break;
		}
	}

	private void edit() {

		Log.d("Add Item", "Next page starting....");
		Intent intent = new Intent(Edit_update.this, EditItem.class);
		intent.putExtra("prod_id", prod_id);

		startActivityForResult(intent, REQUEST_CODE);

	}

	private void delete_prod() {
		JSONObject json = new JSONObject();
		try {
			json.put("prod_id", getIntent().getExtras().getInt("prod_id"));
			/****************** url of the website ****************/
			String URI = "AndroidSearchItem";
			String check = "";

			Log.d("JSON", json.toString());
			// paring data
			JSONArray jArray = appState.loadWebValue(json, URI, check);
			JSONObject json_data = null;

			Log.d("JSON", "Returned from web service");

			if (!(jArray == null) && jArray.length() > 0) {

				Log.d("JSON", "Enterde into parsing service");
				json_data = jArray.getJSONObject(0);

				String string = json_data.getString("type");
				if (string.compareToIgnoreCase("delete") == 0) {

					finish_page();
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/************** Finish page Method ***************/
	private void finish_page() {
		Intent data1 = new Intent();
		// Activity finished ok, return the data pathName
		setResult(Activity.RESULT_OK, data1);
		finish();
	}

	/************** Next Page Method ***************/
	private void next_page() {
		Log.d("Add Item", "Next page starting....");
		Intent intent = new Intent(Edit_update.this, Add_adv.class);
		intent.putExtra("prod_id", prod_id);

		startActivityForResult(intent, REQUEST_CODE);
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
				prod_price.setText(String.valueOf("Price : "
						+ json_data.getDouble("price")));

				ImageView prod_imageview = (ImageView) findViewById(R.id.prod_imageView);
				String image = json_data.getString("image");
				if (!image.isEmpty()) {
					Drawable drawable = LoadImageFromWebOperations(image);
					prod_imageview.setImageDrawable(drawable);
				}

				TextView prod_number_TextView = (TextView) findViewById(R.id.prod_number_TextView);
				prod_num = json_data.getInt("number");
				prod_number_TextView.setText("Available products : "
						+ String.valueOf(prod_num));
				Log.d("JSON", "" + prod_number_TextView);

				TextView prod_seller = (TextView) findViewById(R.id.prod_seller_TextView);
				owner = json_data.getString("seller_id");
				prod_seller.setText(json_data.getString("seller"));
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
