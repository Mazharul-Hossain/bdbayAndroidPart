package com.bdbay.namespace;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Add_Adv_Category extends Activity implements
		OnItemSelectedListener {

	MyAppMyState appState;

	// this counts how many Gallery's are on the UI
	int mGalleryCount1 = 0;
	int mGalleryCount2 = 0;

	// this counts how many Gallery's have been initialized
	int mGalleryInitializedCount1 = 0;
	int mGalleryInitializedCount2 = 0;

	int category_pos;
	int sub_category_pos;
	int typeBar;
	int prod_id = 0;

	final static int Top_Category = 1;
	final static int Top_sub_Category = 2;
	final static int Top_add_item = 3;

	private static final int REQUEST_CODE = 0;

	private Spinner Category_Spinner;
	private Spinner sub_Category_Spinner;

	private AutoCompleteTextView category_autoCompleteTextView;
	private AutoCompleteTextView sub_Category_autoCompleteTextView;
	private TextView sub_Category_textView;

	ProgressDialog progDialog;
	ProgressThread progThread;

	static String[] category_List;
	static String[] sub_category_List;
	static int[] Cat_id = new int[0];
	static int[] sub_Cat_id = new int[0];

	String category_List_name;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_adv_category);

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
			finish();
		} else if (appState.getState().equalsIgnoreCase("LogIn")) {
			finish();
		}

		Category_Spinner = (Spinner) findViewById(R.id.category_Spinner);
		sub_Category_Spinner = (Spinner) findViewById(R.id.sub_Category_Spinner);

		Category_Spinner.setOnItemSelectedListener(Add_Adv_Category.this);
		sub_Category_Spinner.setOnItemSelectedListener(Add_Adv_Category.this);

		category_autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.category_autoCompleteTextView);
		sub_Category_autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.sub_Category_autoCompleteTextView);

		sub_Category_textView = (TextView) findViewById(R.id.sub_Category_textView);

		category_pos = 0;
		sub_category_pos = 0;
		category_List_name = new String();

		webUpdate(Top_Category);
	}

	/*
	 * public boolean onKeyUp(View arg0, Editable arg1, int arg2, KeyEvent arg3)
	 * { // TODO Auto-generated method stub Log.v("I am ", "KeyUp"); return
	 * true; }// End of onKeyUp
	 * 
	 * /************** web Update Method **************
	 */
	private void webUpdate(int i) {
		typeBar = i;
		showDialog(typeBar);
	}

	/************** Override onCreate Dialog Method ***************/
	@Override
	protected Dialog onCreateDialog(int id) {
		progDialog = null;

		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("Loading...");
		return progDialog;
	}

	/************** Override onPrepare Dialog Method ***************/
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		progThread = new ProgressThread(handler);
		progThread.start();
	}

	/************** Hnadler Class ***************/
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			int total = msg.getData().getInt("total");
			// progDialog.setProgress(total);
			if (total == 50) {
				SpinnerInflater(); // will be called inside
			} else if (total <= 0) {
				dismissDialog(typeBar);
				if (typeBar == Top_add_item && prod_id != 0) {
					next_page();
					finish_page();
				}
			}
		}
	};

	/************** Type List Inflater Method ***************/
	private void SpinnerInflater() {
		if (typeBar == Top_Category) {
			ArrayList<String> lst1 = new ArrayList<String>();
			lst1.addAll(Arrays.asList(category_List));

			ArrayAdapter<String> adapter_Category = new ArrayAdapter<String>(
					Add_Adv_Category.this,
					android.R.layout.simple_spinner_dropdown_item, lst1);

			ArrayList<String> lst11 = new ArrayList<String>();
			lst11.addAll(Arrays.asList(category_List));

			ArrayAdapter<String> adapter_Category1 = new ArrayAdapter<String>(
					Add_Adv_Category.this,
					android.R.layout.simple_spinner_dropdown_item, lst11);

			Category_Spinner.setAdapter(adapter_Category);
			category_autoCompleteTextView.setAdapter(adapter_Category1);

			category_autoCompleteTextView.addTextChangedListener(textChecker);

			mGalleryCount1 = 1;

			sub_Category_Spinner.setVisibility(View.INVISIBLE);
			sub_Category_textView.setVisibility(View.INVISIBLE);
			sub_Category_autoCompleteTextView.setVisibility(View.INVISIBLE);

		} else if (typeBar == Top_sub_Category) {
			Log.d("Main", "Main : Android Show Sub Category ");
			ArrayList<String> lst2 = new ArrayList<String>();
			lst2.addAll(Arrays.asList(sub_category_List));

			ArrayAdapter<String> adapter_sub_Category = new ArrayAdapter<String>(
					Add_Adv_Category.this,
					android.R.layout.simple_spinner_dropdown_item, lst2);
			sub_Category_Spinner.setAdapter(adapter_sub_Category);
			sub_Category_autoCompleteTextView.setAdapter(adapter_sub_Category);

			mGalleryCount2 = 1;

			sub_Category_Spinner.setVisibility(View.VISIBLE);
			sub_Category_textView.setVisibility(View.VISIBLE);
			sub_Category_autoCompleteTextView.setVisibility(View.VISIBLE);
			Log.d("Main", "Main : Android Show Sub Category ");
		}
	}

	/************** Autocomplete textview loader Method ***************/
	final TextWatcher textChecker = new TextWatcher() {
		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			category_List_name = category_autoCompleteTextView.getText()
					.toString();
			for (int i = 0; i < category_List.length; i++) {
				if (category_List[i].equalsIgnoreCase(category_List_name)) {
					category_pos = i;
				}
			}
			Log.d("Add Item", "Sub cat is calling Autocomplete textview");
			// webUpdate(Top_sub_Category);
		}
	};

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.back_adv_cat_button:
			finish();
			break;
		case R.id.save_adv_cat_button:
			webUpdate(Top_add_item);
			break;
		}
	}

	/************** Add Item Online Method ***************/
	private void add_Item() {
		try {
			JSONObject json = new JSONObject();

			json.put("product_name", getIntent().getExtras().getString("Name"));
			json.put("product_number", getIntent().getExtras().getInt("Number"));
			json.put("product_description",
					getIntent().getExtras().getString("Details"));
			json.put("product_price", getIntent().getExtras().getFloat("Price"));
			json.put("Image", getIntent().getExtras().getString("Image"));
			json.put("u_name", appState.getState());
			json.put("cat_id", Cat_id[category_pos]);

			if (sub_Cat_id.length > 0)
				json.put("sub_cat_id", sub_Cat_id[sub_category_pos]);
			else
				json.put("sub_cat_id", 0);
			json.put("cat_name", category_autoCompleteTextView.getText()
					.toString());
			json.put("sub_cat_name", sub_Category_autoCompleteTextView
					.getText().toString());

			Log.d("Add Item", json.toString());

			/****************** url of the website ****************/
			String URI = "AndroidAddItem";
			String Check = "add_item";

			// paring data
			JSONArray jArray = appState.loadWebValue(json, URI, Check);
			JSONObject json_data = null;

			if (!(jArray == null) && jArray.length() > 0) {
				json_data = jArray.getJSONObject(0);

				String check = json_data.getString("add_product");
				if (check.equalsIgnoreCase("ok")) {
					prod_id = json_data.getInt("id");
				}
			}
		} catch (JSONException e1) {
		} catch (ParseException e1) {
			e1.printStackTrace();
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
		Intent intent = new Intent(Add_Adv_Category.this, Add_adv.class);
		intent.putExtra("prod_id", prod_id);

		startActivityForResult(intent, REQUEST_CODE);
	}

	/************** Search Item Type Method ***************/
	private int SearchItem_catagory() {
		int checksum = 0;
		try {
			JSONObject json = new JSONObject();
			if (typeBar == Top_Category) {
				json.put("type", "type");

				/****************** url of the website ****************/
				String URI = "AndroidShowItemCategory";
				String Check = "Top_Category";

				// paring data
				JSONArray jArray = appState.loadWebValue(json, URI, Check);
				JSONObject json_data = null;

				if (!(jArray == null) && jArray.length() > 0) {
					Log.d("Main", "Main : Android Show Item Category");
					category_List = new String[jArray.length()];
					Cat_id = new int[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);

						category_List[i] = json_data.getString("name");
						Cat_id[i] = json_data.getInt("id");
					}
					checksum = 1;

					Log.d("Main", "Main : Android Show Item Category "
							+ checksum);
				}
			} else if (typeBar == Top_sub_Category) {
				json.put("id", Cat_id[category_pos]);
				json.put("name", category_autoCompleteTextView.getText()
						.toString());
				Log.d("Add Item", json.toString());

				/****************** url of the website ****************/
				String URI = "AndroidShowItemSubCategory";
				String Check = "Top_Sub_Category";

				// paring data
				JSONArray jArray = appState.loadWebValue(json, URI, Check);
				JSONObject json_data = null;

				if (!(jArray == null) && jArray.length() > 0) {
					Log.d("Add Item", "Result have");
					sub_category_List = new String[jArray.length()];
					sub_Cat_id = new int[jArray.length()];

					for (int i = 0; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);

						sub_category_List[i] = json_data.getString("name");
						sub_Cat_id[i] = json_data.getInt("id");
					}
					checksum = 1;
					Log.d("Add Item", "Sub cat finish");
				}
			}
		} catch (JSONException e1) {
			Toast.makeText(getBaseContext(), "No Login Value Found",
					Toast.LENGTH_LONG).show();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return checksum;
	}

	/************** Progress Threadr Class ***************/
	private class ProgressThread extends Thread {
		// Class constants defining state of the thread
		final static int DONE = 0;
		final static int RUNNING = 1;
		Handler mHandler;
		int mState;
		int total;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		@Override
		public void run() {

			Message msg;
			Bundle b;
			int checksum = 0;

			mState = RUNNING;
			total = 100;
			while (mState == RUNNING) {
				if (typeBar == Top_Category) {
					checksum = SearchItem_catagory();

					if (checksum == 1) {
						total = 50;
						msg = mHandler.obtainMessage();
						b = new Bundle();
						b.putInt("total", total);
						msg.setData(b);
						mHandler.sendMessage(msg);
					}

				} else if (typeBar == Top_sub_Category) {

					checksum = SearchItem_catagory();
					if (checksum == 1) {
						total = 50;
						msg = mHandler.obtainMessage();
						b = new Bundle();
						b.putInt("total", total);
						msg.setData(b);
						mHandler.sendMessage(msg);
					}

				} else if (typeBar == Top_add_item) {
					add_Item();
				}
				total = -1;
				msg = mHandler.obtainMessage();
				b = new Bundle();
				b.putInt("total", total);
				msg.setData(b);
				mHandler.sendMessage(msg);
				mState = DONE;
			}
		}
	}

	/************** on Spinner List Item Select Method ***************/
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("form", "onitemselected");
		switch (parent.getId()) {
		case R.id.category_Spinner:
			if (mGalleryInitializedCount1 < mGalleryCount1) {
				mGalleryInitializedCount1++;
			} else {
				category_pos = position;
				Log.d("Add Item", "Sub cat is calling");
				webUpdate(Top_sub_Category);
			}
			break;
		case R.id.sub_Category_Spinner:
			if (mGalleryInitializedCount2 < mGalleryCount2) {
				mGalleryInitializedCount2++;
			} else {
				sub_category_pos = position;
			}
			break;
		}

	}

	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	/************** Progress Threadr Class Ends ***************/
}
