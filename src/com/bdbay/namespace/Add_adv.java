package com.bdbay.namespace;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Add_adv extends Activity {

	MyAppMyState appState;

	private int cur_year;
	private int cur_month;
	private int cur_day;

	private int starting_year;
	private int starting_month;
	private int starting_day;

	static final int DATE_DIALOG_ID = 99;
	static final int PROGRESS_DIALOG_ID = 91;

	static final int requires_credit_PROGRESS_DIALOG_ID = 1;
	static final int available_credit_PROGRESS_DIALOG_ID = 2;

	private int finishing_year;
	private int finishing_month;
	private int finishing_day;

	int activeDate;

	final static int starting_Date = 1;
	final static int finishing_Date = 2;

	private static final int REQUEST_CODE = 0;

	TextView adv_starting_date_textView;
	TextView adv_finishing_date_textView;
	TextView requires_credit_textView;
	TextView available_credit_textView;

	float requires_credit;
	float available_credit;

	String transection_id;
	int priority_id;

	int typeBar;
	ProgressDialog progDialog;
	ProgressThread progThread;

	private RadioGroup rg1;

	private String priority1;
	private String priority2;
	private String priority3;

	private boolean calculate_flag;

	static String[] monthList = { "January", "February", "March", "April",
			"May", "June", "July", "August", "September", "October",
			"November", "December" };

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_adv);

		Log.d("Add Item", "Next page started");
		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
			finish();
		} else if (appState.getState().equalsIgnoreCase("LogIn")) {
			finish();
		}

		adv_starting_date_textView = (TextView) findViewById(R.id.adv_starting_date_textView);
		adv_finishing_date_textView = (TextView) findViewById(R.id.adv_finishing_date_textView);

		requires_credit_textView = (TextView) findViewById(R.id.requires_credit_textView);
		available_credit_textView = (TextView) findViewById(R.id.available_credit_textView);
		setCurrentDateOnView();

		rg1 = (RadioGroup) findViewById(R.id.radioGroup1);

		rg1.setOnCheckedChangeListener(listener1);
		priority_id = 1;

		transection_id = "";
		calculate_flag = false;

		webUpdate(available_credit_PROGRESS_DIALOG_ID);
	}

	/************** display current date Method ***************/
	public void setCurrentDateOnView() {

		final Calendar c = Calendar.getInstance();
		finishing_year = starting_year = cur_year = c.get(Calendar.YEAR);
		finishing_month = starting_month = cur_month = c.get(Calendar.MONTH);
		finishing_day = starting_day = cur_day = c.get(Calendar.DAY_OF_MONTH);

		adv_starting_date_textView.setText("( M - D - YYYY ) : "
				+ monthList[cur_month] + " - " + cur_day + " - " + cur_year);

		adv_finishing_date_textView.setText("( M - D - YYYY ) : "
				+ monthList[cur_month] + " - " + cur_day + " - " + cur_year);

		activeDate = 0;
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.add_adv_back_button:
			finish();
			break;
		case R.id.adv_starting_date_textView:
			activeDate = starting_Date;
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.adv_starting_date_textView1:
			activeDate = starting_Date;
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.adv_finishing_date_textView:
			activeDate = finishing_Date;
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.adv_finishing_date_textView3:
			activeDate = finishing_Date;
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.add_credit_textView:
			break;
		case R.id.calculate_button:
			webUpdate(requires_credit_PROGRESS_DIALOG_ID);
			break;
		case R.id.add_adv_next_button:
			next_page();
			break;
		}
	}

	/************** Next Page Method ***************/
	private void next_page() {
		if (calculate_flag && !transection_id.isEmpty()) {
			calculate_flag = false;
			Intent intent = new Intent(Add_adv.this, Pin_request.class);
			intent.putExtra("transaction_id", transection_id);
			intent.putExtra("requires_credit", requires_credit);
			startActivityForResult(intent, REQUEST_CODE);

		} else {
			webUpdate(requires_credit_PROGRESS_DIALOG_ID);
		}
	}

	/************** Override onCreate Date Picker Dialog Method ***************/
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// set date picker as current date
			return new DatePickerDialog(this, datePickerListener, cur_year,
					cur_month, cur_day);
		case PROGRESS_DIALOG_ID:
			progDialog = null;

			progDialog = new ProgressDialog(this);
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setMessage("Loading...");
			return progDialog;
		}
		return null;
	}

	/************** Override onPrepare Dialog Method ***************/
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case DATE_DIALOG_ID:
			((DatePickerDialog) dialog)
					.updateDate(cur_year, cur_month, cur_day);
			break;
		case PROGRESS_DIALOG_ID:
			progThread = new ProgressThread(handler);
			progThread.start();
			break;
		}
	}

	/************** on Date Set Listener Method ***************/
	private final OnCheckedChangeListener listener1 = new OnCheckedChangeListener() {

		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.radio0:
				priority_id = 1;
				calculate_flag = false;
				break;
			case R.id.radio1:
				priority_id = 2;
				calculate_flag = false;
				break;
			case R.id.radio2:
				priority_id = 3;
				calculate_flag = false;
				break;
			}
		}
	};
	/************** on Date Set Listener Method ***************/
	private final OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			if (activeDate == starting_Date) {
				starting_year = selectedYear;
				starting_month = selectedMonth;
				starting_day = selectedDay;

				adv_starting_date_textView.setText("( M - D - YYYY ) : "
						+ monthList[starting_month] + " - " + starting_day
						+ " - " + starting_year);
				calculate_flag = false;
				activeDate = 0;
			} else if (activeDate == finishing_Date) {
				finishing_year = selectedYear;
				finishing_month = selectedMonth;
				finishing_day = selectedDay;

				adv_finishing_date_textView.setText("( M - D - YYYY ) : "
						+ monthList[finishing_month] + " - " + finishing_day
						+ " - " + finishing_year);
				calculate_flag = false;
				activeDate = 0;
			}
		}
	};

	/************** web Update Method ***************/
	private void webUpdate(int i) {
		typeBar = i;
		showDialog(PROGRESS_DIALOG_ID);
	}

	/************** Hnadler Class ***************/
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			int total = msg.getData().getInt("total");
			// progDialog.setProgress(total);
			if (total == 50) {
				requires_credit_textView.setText(String
						.valueOf(requires_credit));
				if (transection_id.isEmpty()) {
					Toast.makeText(getBaseContext(),
							"You don't have enough money", Toast.LENGTH_LONG)
							.show();
				}

			} else if (total == 40) {
				available_credit_textView.setText(String
						.valueOf(available_credit));

				RadioButton rb0 = (RadioButton) findViewById(R.id.radio0);
				rb0.setText("Proirity 1 :" + priority1 + " tk/day");

				RadioButton rb1 = (RadioButton) findViewById(R.id.radio1);
				rb1.setText("Proirity 2 :" + priority2 + " tk/day");

				RadioButton rb2 = (RadioButton) findViewById(R.id.radio2);
				rb2.setText("Proirity 3 :" + priority3 + " tk/day");
			} else if (total == 30) {
				Toast.makeText(getBaseContext(),
						"Date is not inserted corectly", Toast.LENGTH_LONG)
						.show();
			} else if (total <= 0) {
				dismissDialog(PROGRESS_DIALOG_ID);
			}
		}
	};

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

	/************** Search Item Type Method ***************/
	private int SearchItem_catagory() {
		int checksum = 0;
		try {
			if (typeBar == requires_credit_PROGRESS_DIALOG_ID) {
				JSONObject json = new JSONObject();

				json.put("u_name", appState.getState());
				json.put("prod_id", getIntent().getExtras().getInt("prod_id"));

				json.put("prod_priority", priority_id);

				json.put("starting_year", starting_year);
				json.put("starting_month", starting_month);
				json.put("starting_day", starting_day);

				json.put("finishing_year", finishing_year);
				json.put("finishing_month", finishing_month);
				json.put("finishing_day", finishing_day);

				json.put("transaction_id", transection_id);

				/****************** url of the website ****************/
				String URI = "AndroidAddTempTransaction";
				String Check = "credit_transaction";

				// paring data
				JSONArray jArray = appState.loadWebValue(json, URI, Check);
				JSONObject json_data = null;

				if (!(jArray == null) && jArray.length() > 0) {
					json_data = jArray.getJSONObject(0);

					requires_credit = (float) json_data.getDouble("amount");
					transection_id = json_data.getString("string");
					calculate_flag = true;
					checksum = 1;
				}
			} else if (typeBar == available_credit_PROGRESS_DIALOG_ID) {
				JSONObject json = new JSONObject();
				json.put("u_name", appState.getState());
				json.put("prod_id", getIntent().getExtras().getInt("prod_id"));

				/****************** url of the website ****************/
				String URI = "AndroidItemMeasureMoney";
				String Check = "requires_credit";

				// paring data
				JSONArray jArray = appState.loadWebValue(json, URI, Check);
				JSONObject json_data = null;

				if (!(jArray == null) && jArray.length() > 0) {
					json_data = jArray.getJSONObject(0);

					available_credit = (float) json_data.getDouble("balance");
					priority1 = json_data.getString("priority1");
					priority2 = json_data.getString("priority2");
					priority3 = json_data.getString("priority3");
					calculate_flag = false;
					checksum = 1;
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
				if (typeBar == requires_credit_PROGRESS_DIALOG_ID) {
					checksum = SearchItem_catagory();

					if (checksum == 1) {
						calculate_flag = true;
						total = 50;
						msg = mHandler.obtainMessage();
						b = new Bundle();
						b.putInt("total", total);
						msg.setData(b);
						mHandler.sendMessage(msg);
					} else {
						calculate_flag = false;
						total = 30;
						msg = mHandler.obtainMessage();
						b = new Bundle();
						b.putInt("total", total);
						msg.setData(b);
						mHandler.sendMessage(msg);
					}

				} else if (typeBar == available_credit_PROGRESS_DIALOG_ID) {

					checksum = SearchItem_catagory();
					if (checksum == 1) {
						total = 40;
						msg = mHandler.obtainMessage();
						b = new Bundle();
						b.putInt("total", total);
						msg.setData(b);
						mHandler.sendMessage(msg);
					}

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

	/************** Progress Threadr Class Ends ***************/

}
