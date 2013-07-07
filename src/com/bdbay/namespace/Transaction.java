package com.bdbay.namespace;

import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Transaction extends ListActivity {

	protected static final int List_Inflater_Handler = 50;
	MyAppMyState appState;
	private Vector<RowData> data;
	RowData rd;
	private LayoutInflater mInflater;

	public JSONObject json; // json object send to server
	public String URI; // id of server
	public String check; // pass string of server
	public int page_index; // search result page number

	ProgressDialog progDialog;
	ProgressThread progThread;
	private int typeBar;

	private int[] prod_id;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.transaction);

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
			finish();
		} else if (appState.getState().equalsIgnoreCase("LogIn")) {
			finish();
		}
		data = new Vector<RowData>();

		page_index = 0;
		webUpdate(0);

		Log.d("Main ", "Hi !!! : Activity Crossed");
	}

	/************** web Update Method ***************/
	private void webUpdate(int i) {
		Log.d("Main", "Main : Web Update Started " + i);
		typeBar = i;
		showDialog(typeBar);
		Log.d("Main", "Main : Web Update Finished ");
	}

	/************** Override onCreate Dialog Method ***************/
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d("Main", "Main : Progress Dialog Started");

		progDialog = null;

		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("Loading...");
		Log.d("Main", "Main : Progress Dialog Finished");
		return progDialog;
	}

	/************** Override onPrepare Dialog Method ***************/
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		Log.d("Main", "Main : Progress Dialog Started 2");
		progThread = new ProgressThread(handler);
		progThread.start();
		Log.d("Main", "Main : Progress Dialog Finished 2");
	}

	/************** Hnadler Class ***************/
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			Log.d("Main", "Main : Progress Dialog Handler Started");
			int total = msg.getData().getInt("total");
			// progDialog.setProgress(total);
			if (total == List_Inflater_Handler) {
				Log.d("Main", "Main : List Inflater Started");
				ListInflater(); // will be called inside
			} else if (total <= 0) {
				dismissDialog(typeBar);
				Log.d("Main", "Main : Progress Dialog Handler Finisheded");
				// progThread.setState(ProgressThread.DONE);
			}
		}
	};

	/************** List Inflater Method ***************/
	private void ListInflater() {
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		CustomAdapter adapter = new CustomAdapter(this, R.layout.transaction,
				R.id.title, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
		Log.d("Main", "Main : List Inflater Finished");
	}

	/************** Search Item Method ***************/
	private int SearchItem() {
		URI = null;
		check = null;
		int checksum = 0;
		try {
			json = new JSONObject();
			json.put("u_name", appState.getState());
			json.put("index", 0);

			check = "SearchItem";
			/****************** url of the website ****************/
			URI = "AndroidSearchItem";
			Log.d("JSON", json.toString());

			// paring data
			JSONArray jArray = appState.loadWebValue(json, URI, check);
			// JSONArray jArray = new JSONArray(result);

			JSONObject json_data = null;
			data = new Vector<RowData>();
			Log.d("JSON", "Returned from web service");
			if (!(jArray == null) && jArray.length() > 0) {

				prod_id = new int[jArray.length()];
				for (int i = 0; i < jArray.length(); i++) {
					json_data = jArray.getJSONObject(i);

					prod_id[i] = json_data.getInt("id");
					rd = new RowData(json_data.getString("name"),
							json_data.getString("date"),
							json_data.getString("price"));

					data.add(rd);
				}
				checksum = 1;
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return checksum;
	}

	/************** Progress Threadr Class ***************/
	private class ProgressThread extends Thread {

		final static int DONE = 0;
		final static int RUNNING = 1;

		Handler mHandler;
		int mState;
		int total;
		int checksum;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		@Override
		public void run() {

			Message msg;
			Bundle b;
			checksum = 0;

			mState = RUNNING;
			total = 100;
			while (mState == RUNNING) {

				checksum = SearchItem();
				mState = DONE;
				if (checksum == 1) {

					total = List_Inflater_Handler;
					msg = mHandler.obtainMessage();
					b = new Bundle();
					b.putInt("total", total);
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
				if (mState == DONE) {

					total = -1;
					msg = mHandler.obtainMessage();
					b = new Bundle();
					b.putInt("total", total);
					msg.setData(b);
					mHandler.sendMessage(msg);
				}
			}
		}
	}

	/************** Progress Threadr Class Ends ***************/

	/************** on List Item Click Method ***************/
	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Log.d("Main", "Main : Product List Item selected !");
		Intent intent = new Intent(Transaction.this, ShowItem.class);
		intent.putExtra("prod_id", prod_id[position]);
		startActivity(intent);

	}

	/************** Row Data Class ***************/
	private class RowData {

		protected String mTitle = null;
		protected String mDate = null;
		protected String mCost = null;

		RowData(String title, String image, String detail) {
			mDate = image;
			mTitle = title;
			mCost = detail;
		}

		@Override
		public String toString() {
			return mDate + " " + mTitle + " " + mCost;
		}
	}

	/************** Row Data Class Ends ***************/

	/************** Custom Adapter Class ***************/
	private class CustomAdapter extends ArrayAdapter<RowData> {

		public CustomAdapter(Context context, int resource,
				int textViewResourceId, List<RowData> objects) {

			super(context, resource, textViewResourceId, objects);
		}

		/************** get View Method ***************/
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			TextView title = null;
			TextView detail = null;
			TextView date = null;
			RowData rowData = getItem(position);

			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.list_trasaction, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();
			title = holder.gettitle();
			title.setText(rowData.mTitle);

			detail = holder.getdetail();
			detail.setText(rowData.mCost);

			date = holder.getdate();
			date.setText(rowData.mCost);

			return convertView;
		}

		/************** View Holder Class ***************/
		private class ViewHolder {

			private final View mRow;
			private TextView title = null;
			private TextView cost = null;
			private TextView date = null;

			/************** View Holder Method ***************/
			public ViewHolder(View row) {
				mRow = row;
			}

			/************** Title View Method ***************/
			public TextView gettitle() {
				if (null == title) {
					title = (TextView) mRow.findViewById(R.id.adv_title);
				}
				return title;
			}

			/************** Text View Method ***************/
			public TextView getdetail() {
				if (null == cost) {
					cost = (TextView) mRow.findViewById(R.id.adv_cost);
				}
				return cost;
			}

			/************** Text View Method ***************/
			public TextView getdate() {
				if (null == date) {
					date = (TextView) mRow.findViewById(R.id.adv_date);
				}
				return date;
			}
		}
		/************** View Holder Class Ends ***************/
	}
	/************** Custom Adapter Class Ends ***************/

}
