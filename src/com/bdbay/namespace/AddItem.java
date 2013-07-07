package com.bdbay.namespace;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddItem extends Activity {

	Bitmap Photo_URL = null;
	Button Photo_Button;

	MyAppMyState appState;

	private static final int REQUEST_CODE = 1;
	private static final int REQUEST_CODE1 = 0;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_item);

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
			finish();
		} else if (appState.getState().equalsIgnoreCase("LogIn")) {
			finish();
		}
		Photo_Button = (Button) findViewById(R.id.add_item_chose_photo_button);
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.add_item_back_button:
			finish();
			break;
		case R.id.add_item_button:
			add_Item();
			break;
		case R.id.add_item_chose_photo_button:
			photo_choser();
			break;
		}
	}

	/************** Add Item Method ***************/
	public void add_Item() {

		Log.d("Add Item", "Starting from here");

		EditText Name = (EditText) findViewById(R.id.add_item_entertitle_editText);
		EditText Details = (EditText) findViewById(R.id.add_item_enter_details_editText);
		EditText Price = (EditText) findViewById(R.id.add_item_enter_price_editText);
		EditText Number = (EditText) findViewById(R.id.add_item_enter_number_editText);

		String p_Name = Name.getText().toString();
		String p_Details = Details.getText().toString();
		String p_Price = Price.getText().toString();
		String p_Number = Number.getText().toString();

		if (!p_Name.isEmpty() && !p_Details.isEmpty() && !p_Price.isEmpty()
				&& !p_Number.isEmpty()) {

			Log.d("Add Item", "Starting 1");
			try {
				String p_Image = new String();
				if (!(Photo_URL == null)) {
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					Photo_URL.compress(Bitmap.CompressFormat.JPEG, 90, bao);
					byte[] ba = bao.toByteArray();
					p_Image = Base64.encodeBytes(ba);
				}

				Intent intent = new Intent(AddItem.this, Add_Adv_Category.class);
				intent.putExtra("Name", p_Name);
				intent.putExtra("Details", p_Details);
				intent.putExtra("Price", Float.valueOf(p_Price));
				intent.putExtra("Number", Integer.valueOf(p_Number));
				intent.putExtra("Image", p_Image);

				startActivityForResult(intent, REQUEST_CODE1);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					"You may not have filed all the field ", Toast.LENGTH_LONG)
					.show();
		}
	}

	/************** Photo Chosser Method ***************/
	public void photo_choser() {
		Intent intent = new Intent(AddItem.this, Dialog_Photo.class);
		startActivityForResult(intent, REQUEST_CODE);
	}

	/************** Override on Activity Return Result Method ***************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("Add Item", "If starting 6");
		if (requestCode == REQUEST_CODE) {
			Log.d("Add Item", "If starting 7");
			if (resultCode == Activity.RESULT_OK) {
				Photo_URL = appState.getbitmapImage();
				Drawable d = new BitmapDrawable(getResources(), Photo_URL);
				Photo_Button.setBackgroundDrawable(d);
			}
		} else if (requestCode == REQUEST_CODE1) {
			if (resultCode == Activity.RESULT_OK) {
				finish();
			}
		}
	}
}
