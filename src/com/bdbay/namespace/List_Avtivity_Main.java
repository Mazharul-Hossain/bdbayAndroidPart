package com.bdbay.namespace;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bdbay.namespace.MyAppMyState.Search_Index_Class;

public class List_Avtivity_Main extends ListActivity implements
		OnItemSelectedListener {

	final static int First_run_webupdate = 0, Search_product_webupdate = 1,
			Search_category_webupdate = 2, Search_subcategory_webupdate = 3,
			Search_showroom_webupdate = 4;

	final static int Category_Request_Code = 1, Sub_category_Request_Code = 2;

	final static int Top_item = 1, Top_search = 2, Top_category = 3,
			Sub_category = 4;

	public JSONObject json; // json object send to server
	public String URI; // id of server
	public String check; // pass string of server
	public int page_index; // search result page number

	Search_Index_Class search_Index_Class;

	int has_NEXT_VIEW;
	int offset;
	boolean has_next_search_page;
	private boolean ENABLE_has_next_search_page = false;
	private boolean EDIT_has_next_search_page = false;
	private boolean ADD_has_next_search_page = false;

	int usage_mode;
	final static int USER_MODE = 1, GUEST_MODE = 2, SHOWROOM_MODE = 3;

	protected static final int Spinner_Inflater_Handler = 50,
			List_Inflater_Handler = 40;

	// this counts how many Gallery's are on the UI
	int mGalleryCount = 0;
	// this counts how many Gallery's have been initialized
	int mGalleryInitializedCount = 0;

	int typeBar = -1;
	int searchItemcategory;
	int searchItemSubcategory;
	int searchItem_category_select;

	ProgressDialog progDialog;
	ProgressThread progThread;

	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;

	MyAppMyState appState;
	String state;

	static String[] title;

	static String[] detail;

	private final Integer[] imgid = { R.drawable.bsfimg, R.drawable.bsfimg2,
			R.drawable.bsfimg4, R.drawable.bsfimg5 };

	static int[] prod_id = new int[0];
	static String[] showroom_id = new String[0];

	static String[] searchItemCat_List = new String[0];
	static String[] searchItemSubcat_List = new String[0];
	static int[] searchItemCat_id = new int[0];
	static int[] searchItemCat_sub_id = new int[0];

	private Spinner searchItem_spinner;
	private Spinner searchItem_spinner1;

	ArrayAdapter<String> adapterSearchItem;
	ArrayAdapter<String> adapterSearchItem1;

	private boolean check_showroom;
	String selected_showroom_id;

	private TextView search_error_textView;
	private boolean search_error_flag;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Log.d("Main", "Main : Activity Started");

		appState = (MyAppMyState) this.getApplicationContext();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
		}
		data = new Vector<RowData>();

		Log.d("Main", "Main : Activity Started 1");
		searchItem_spinner = (Spinner) findViewById(R.id.searchItem_spinner);
		searchItem_spinner.setOnItemSelectedListener(List_Avtivity_Main.this);

		searchItem_spinner1 = (Spinner) findViewById(R.id.searchItem_spinner1);
		searchItem_spinner1.setOnItemSelectedListener(List_Avtivity_Main.this);

		search_error_textView = (TextView) findViewById(R.id.search_error_textView);
		typeBar = -1;
		Log.d("Main", "Main : Activity Moving 2");
		if (typeBar < 0) {
			Log.d("Main", "Main : Web Update Started");
			usage_mode = GUEST_MODE;
			has_NEXT_VIEW = 0;
			webUpdate(First_run_webupdate); // first run
		}
		Log.d("Main ", "Hi !!! : Activity Crossed");

		Log.d("Spinner1", "position1 : " + searchItemcategory
				+ " & position2 : " + searchItemSubcategory);
	}

	/************** web Update Method ***************/
	private void webUpdate(int i) {
		Log.d("Main", "Main : Web Update Started " + i);
		typeBar = i;
		showDialog(typeBar);
		Log.d("Main", "Main : Web Update Finished " + typeBar);
	}

	/************** Override onCreate Dialog Method ***************/
	@Override
	protected Dialog onCreateDialog(int id) {
		Log.d("Main", "Main : Progress Dialog Started");

		progDialog = null;

		progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setMessage("Loading...");
		// progThread = new ProgressThread(handler);
		// progThread.start();
		Log.d("Main", "Main : Progress Dialog Finished");
		return progDialog;
	}

	/************** Override onPrepare Dialog Method ***************/
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		Log.d("Main", "Main : Progress Dialog Started 2");

		// progDialog = null;

		// progDialog = new ProgressDialog(this);
		// progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// progDialog.setMessage("Loading...");
		progThread = new ProgressThread(handler);
		progThread.start();
		Log.d("Main", "Main : Progress Dialog Finished 2");
	}

	/*
	 * Handler on the main (UI) thread that will receive messages from the
	 * second thread and update the progress.
	 */
	/************** Hnadler Class ***************/
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			/*
			 * Get the current value of the variable total from the message data
			 * and update the progress bar.
			 */
			Log.d("Main", "Main : Progress Dialog Handler Started");
			int total = msg.getData().getInt("total");
			// progDialog.setProgress(total);
			if (total == Spinner_Inflater_Handler) {
				Log.d("Main", "Main : Spinner Inflater Started");
				SpinnerInflater(); // will be called inside
			} else if (total == List_Inflater_Handler) {
				Log.d("Main", "Main : List Inflater Started");
				ListInflater(); // will be called inside
			} else if (total <= 0) {

				if (search_error_flag) {
					search_error_textView.setVisibility(View.VISIBLE);
				} else {
					search_error_textView.setVisibility(View.INVISIBLE);
				}
				dismissDialog(typeBar);
				Log.d("Main", "Main : Progress Dialog Handler Finisheded");
				// progThread.setState(ProgressThread.DONE);
			}
		}
	};

	/************** Override onCreate Options Menu Method ***************/
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		menu.clear();

		if (appState == null) {
			appState = ((MyAppMyState) getApplicationContext());
		}
		state = appState.getState();

		if (state.equalsIgnoreCase("LogIn")) {
			inflater.inflate(R.menu.guest_menu_blogin, menu);
		} else {
			if (appState.getType().equalsIgnoreCase("showroom")) {
				inflater.inflate(R.menu.showroom_menu_alogin, menu);
			} else {
				inflater.inflate(R.menu.guest_menu_alogin, menu);
			}
		}
		return true;
	}

	/************** Override on Menu Options Item Selected Method ***************/
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.Menu_Login) {

			login();

		} else if (item.getItemId() == R.id.Menu_Register) {

			Intent intent = new Intent(List_Avtivity_Main.this,
					Registration.class);
			startActivity(intent);

		} else if (item.getItemId() == R.id.Menu_Login_Showroom) {

			if (usage_mode != SHOWROOM_MODE) {
				usage_mode = SHOWROOM_MODE;
				selected_showroom_id = appState.getState();
				webUpdate(First_run_webupdate); // first run
			} else {
				usage_mode = GUEST_MODE;
				webUpdate(First_run_webupdate);
			}

		} else if (item.getItemId() == R.id.Menu_Logout) {

			appState.setState("LogIn");

		} else if (item.getItemId() == R.id.Menu_Add_Item) {

			Intent intent = new Intent(List_Avtivity_Main.this, AddItem.class);
			startActivity(intent);

		} else if (item.getItemId() == R.id.Showcase) {
			if (usage_mode != USER_MODE) {
				usage_mode = USER_MODE;
				webUpdate(First_run_webupdate); // first run
			} else {
				usage_mode = GUEST_MODE;
				webUpdate(First_run_webupdate); // first run
			}

		}
		return super.onOptionsItemSelected(item);
	}

	/*****************************************
	 * Search Item Type Method
	 * 
	 * @return
	 * @throws Exception
	 *****************************************/
	private int SearchItemType(int Request_Code) {
		int checksum = 0;
		try {
			JSONObject json = new JSONObject();
			String URI = null;
			String Check = null;

			if (Request_Code == Category_Request_Code) {
				json.put("type", "category");
			} else if (Request_Code == Sub_category_Request_Code) {
				json.put("type", "sub_category");
				json.put("cat_id", searchItemCat_id[searchItemcategory]);
			}

			if (usage_mode == SHOWROOM_MODE || check_showroom) {

				json.put("user_id", selected_showroom_id);
				if (appState.getState().equalsIgnoreCase(selected_showroom_id)) {
					json.put("type", "showroom");
				} else {
					json.put("type", "user");
				}
				/****************** url of the website ****************/
				URI = "AndroidShowroomCategory";
				Check = "showroomCategory";

			} else if (usage_mode == GUEST_MODE) {

				/****************** url of the website ****************/
				URI = "AndroidShowItemType";
				Check = "type_search";

			} else if (usage_mode == USER_MODE) {

				return checksum;

			}
			Log.d("JSON", json.toString());
			// paring data
			JSONArray jArray = appState.loadWebValue(json, URI, Check);
			JSONObject json_data = null;

			Log.d("JSON", "Returned from web service");

			if (!(jArray == null) && jArray.length() > 0) {

				Log.d("JSON", "Enterde into parsing service");
				if (Request_Code == Category_Request_Code) {

					searchItemCat_List = new String[jArray.length() + 1];
					searchItemCat_id = new int[jArray.length() + 1];
					searchItemCat_sub_id = new int[jArray.length() + 1];

					searchItemCat_List[0] = "Popular Items";
					searchItemCat_id[0] = 0;
					searchItemCat_sub_id[0] = 0;
					for (int i = 0; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);

						searchItemCat_List[i + 1] = json_data.getString("name");
						searchItemCat_id[i + 1] = json_data.getInt("id");
						searchItemCat_sub_id[i + 1] = 0;
					}
				} else if (Request_Code == Sub_category_Request_Code) {
					searchItemSubcat_List = new String[jArray.length() + 1];
					searchItemCat_sub_id = new int[jArray.length() + 1];

					searchItemSubcat_List[0] = "Popular Items";
					searchItemCat_sub_id[0] = 0;
					for (int i = 0; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);

						searchItemSubcat_List[i + 1] = json_data
								.getString("name");
						searchItemCat_sub_id[i + 1] = Integer
								.parseInt(json_data.getString("sub_id"));
					}
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

	/************** Type List Inflater Method ***************/
	private void SpinnerInflater() {
		// this counts how many Gallery's are on the UI
		mGalleryCount = 1;
		// this counts how many Gallery's have been initialized
		mGalleryInitializedCount = 0;

		if (typeBar == First_run_webupdate) {

			searchItemcategory = 0;

			ArrayList<String> lst1 = new ArrayList<String>();
			lst1.addAll(Arrays.asList(searchItemCat_List));

			adapterSearchItem = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, lst1);
			// Specify the layout to use when the list of choices appears
			adapterSearchItem
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			searchItem_spinner.setAdapter(adapterSearchItem);

		} else {

			searchItemSubcategory = 0;

			ArrayList<String> lst2 = new ArrayList<String>();
			lst2.addAll(Arrays.asList(searchItemSubcat_List));

			adapterSearchItem1 = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, lst2);
			// Specify the layout to use when the list of choices appears
			adapterSearchItem1
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			// Apply the adapter to the spinner
			searchItem_spinner1.setAdapter(adapterSearchItem1);
		}

		Log.d("Main", "Main : Spinner Inflater Finished");
	}

	/************** Search Item Method ***************/
	private int SearchItem(int i) {
		URI = null;
		check = null;
		int checksum = 0;
		try {
			json = new JSONObject();

			if (ENABLE_has_next_search_page) {

				json = search_Index_Class.json;
				check = search_Index_Class.Check;
				URI = search_Index_Class.provided_URI;

				usage_mode = search_Index_Class.user_mode;

				json.put("index", search_Index_Class.page_index + offset);
			} else {

				if (usage_mode == SHOWROOM_MODE || check_showroom) {

					json.put("u_name", selected_showroom_id);

					json.put("ignore", 0);
					/****************** url of the website ****************/
					URI = "AndroidShowroomItem";
					check_showroom = false;

				} else if (usage_mode == GUEST_MODE) {
					/****************** url of the website ****************/
					URI = "AndroidSearchItem";
					String u_name = new String();
					if (!appState.getState().equalsIgnoreCase("Login")) {
						u_name = appState.getState();
					}
					json.put("u_name", u_name);
					json.put("ignore", 1);

				} else if (usage_mode == USER_MODE) {
					/****************** url of the website ****************/
					String u_name = new String();
					if (!appState.getState().equalsIgnoreCase("Login")) {
						u_name = appState.getState();
					}
					json.put("u_name", u_name);
					json.put("ignore", 0);
					URI = "AndroidShowOwnProduct";
				}

				if (i == Top_item) {
					json.put("type", "top_item");

				} else if (i == Top_search) {
					json.put("type", "search");

					if (searchItemCat_id.length > 0)
						json.put("cat_id", searchItemCat_id[searchItemcategory]);
					else
						json.put("cat_id", 0);

					if (searchItemCat_sub_id.length > 0)
						json.put("sub_cat_id",
								searchItemCat_sub_id[searchItemSubcategory]);
					else
						json.put("sub_cat_id", 0);

					EditText Search = (EditText) findViewById(R.id.search);
					String search = Search.getText().toString();
					json.put("search", search);

				} else if (i == Top_category) {
					json.put("type", "category");
					if (searchItemCat_id.length > 0)
						json.put("cat_id", searchItemCat_id[searchItemcategory]);
					else
						json.put("cat_id", 0);

				} else if (i == Sub_category) {
					json.put("type", "sub_category");

					if (searchItemCat_id.length > 0)
						json.put("cat_id", searchItemCat_id[searchItemcategory]);
					else
						json.put("cat_id", 0);

					if (searchItemCat_sub_id.length > 0)
						json.put("sub_cat_id",
								searchItemCat_sub_id[searchItemSubcategory]);
					else
						json.put("sub_cat_id", 0);
				}
				json.put("index", 0);
				check = "SearchItem";

				if (usage_mode == USER_MODE)
					check = "show_own_product";
			}

			Log.d("JSON", json.toString());

			// paring data
			JSONArray jArray = appState.loadWebValue(json, URI, check);
			// JSONArray jArray = new JSONArray(result);

			JSONObject json_data = null;
			data = new Vector<RowData>();
			Log.d("JSON", "Returned from web service");
			if (!(jArray == null) && jArray.length() > 1) {
				json_data = jArray.getJSONObject(0);
				String string = json_data.getString("type");

				/*
				 * if (json_data.getString("next").compareToIgnoreCase("yes") ==
				 * 0) { has_next_search_page = true; } else {
				 * has_next_search_page = false; }
				 */

				if (string.compareToIgnoreCase("product") == 0) {

					prod_id = new int[jArray.length() - 1];
					for (i = 1; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);

						prod_id[i - 1] = json_data.getInt("id");
						String image = json_data.getString("image");
						if (!image.isEmpty()) {
							rd = new RowData(image,
									json_data.getString("name"),
									json_data.getString("price"));
						} else {
							rd = new RowData((i - 1) % 4,
									json_data.getString("name"),
									json_data.getString("price"));
						}
						data.add(rd);
					}
					checksum = 2;
					check_showroom = true;

				} else if (string.compareToIgnoreCase("showroom") == 0) {

					showroom_id = new String[jArray.length() - 1];
					for (i = 1; i < jArray.length(); i++) {
						json_data = jArray.getJSONObject(i);

						showroom_id[i - 1] = json_data.getString("id");
						String image = json_data.getString("image");
						if (!image.isEmpty()) {
							rd = new RowData(image,
									json_data.getString("name"),
									json_data.getString("description"));
						} else {
							rd = new RowData((i - 1) % 4,
									json_data.getString("name"),
									json_data.getString("description"));
						}
						data.add(rd);
					}
					checksum = 2;
					check_showroom = true;
				}
				if (ENABLE_has_next_search_page) {
					if (offset != 0)
						EDIT_has_next_search_page = true;
				} else {
					ADD_has_next_search_page = true;
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		ENABLE_has_next_search_page = false;
		return checksum;
	}

	/************** List Inflater Method ***************/
	private void ListInflater() {
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		CustomAdapter adapter = new CustomAdapter(this, R.layout.list,
				R.id.title, data);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
		Log.d("Main", "Main : List Inflater Finished");
	}

	/************** on List Item Click Method ***************/
	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {

		Log.d("Main", "Main : List Item selected !");
		if (check_showroom) {
			Log.d("Main", "Main : Showroom List Item selected !");

			selected_showroom_id = showroom_id[position];
			if (appState.getState().equalsIgnoreCase(selected_showroom_id)) {
				usage_mode = SHOWROOM_MODE;
			}
			webUpdate(First_run_webupdate);
		} else if (usage_mode == USER_MODE) {
			Log.d("Main", "Main : Product List Item selected !");
			Intent intent = new Intent(List_Avtivity_Main.this,
					Edit_update.class);
			intent.putExtra("prod_id", prod_id[position]);
			startActivity(intent);
		} else {
			Log.d("Main", "Main : Product List Item selected !");
			Intent intent = new Intent(List_Avtivity_Main.this, ShowItem.class);
			intent.putExtra("prod_id", prod_id[position]);
			startActivity(intent);
		}
	}

	/************** on Spinner List Item Select Method ***************/
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		int id1 = parent.getId();
		switch (id1) {
		case R.id.searchItem_spinner:
			if (mGalleryInitializedCount < mGalleryCount) {
				mGalleryInitializedCount++;
			} else {
				searchItemcategory = position;
				searchItemSubcategory = 0;
				Log.d("Spinner1", "position1 : " + searchItemcategory
						+ " & position2 : " + searchItemSubcategory);
				if (!(adapterSearchItem1 == null)) {
					adapterSearchItem1.clear();
				}
				if (searchItemCat_List[searchItemcategory]
						.equalsIgnoreCase("Showroom")) {
					webUpdate(Search_showroom_webupdate);
				} else {
					webUpdate(Search_category_webupdate);
				}
			}
			break;
		case R.id.searchItem_spinner1:
			if (mGalleryInitializedCount < mGalleryCount) {
				mGalleryInitializedCount++;
			} else {
				searchItemSubcategory = position;
				Log.d("Spinner2", "position1 : " + searchItemcategory
						+ " & position2 : " + searchItemSubcategory);
				webUpdate(Search_subcategory_webupdate);
			}
			break;
		}
		/*
		 * // On selecting a spinner item String item =
		 * parent.getItemAtPosition(position).toString();
		 * 
		 * // showing a toast on selecting an item Toast.makeText(
		 * parent.getContext(), "You have selected : " + item + " IN " +
		 * (position + 1) + "th position", Toast.LENGTH_LONG).show();
		 */
	}

	/************** on Spinner Nothing Selected Method ***************/
	public void onNothingSelected(AdapterView<?> parent) {
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		switch (view.getId()) {
		case R.id.next_search_page_textView:
			next_search_page();
			break;
		case R.id.prev_search_page_textView:
			prev_search_page();
			break;
		case R.id.search_imageButton:
			search();
			break;
		case R.id.reload_page_imageButton:
			reload();
			break;
		case R.id.login_imageButton:
			login();
			break;
		case R.id.go_prev_imageButton:
			go_prev_view();
			break;
		case R.id.go_next_imageButton:
			go_next_view();
			break;
		}
	}

	private void go_next_view() {
		if (has_NEXT_VIEW < appState.search_index) {
			ENABLE_has_next_search_page = true;
			has_NEXT_VIEW++;
			search_Index_Class = appState.get_index(has_NEXT_VIEW);
			offset = 0;
			webUpdate(search_Index_Class.Request_code_webupdate);
		}
	}

	private void go_prev_view() {
		if (has_NEXT_VIEW > 0) {
			ENABLE_has_next_search_page = true;
			has_NEXT_VIEW--;
			search_Index_Class = appState.get_index(has_NEXT_VIEW);
			offset = 0;
			webUpdate(search_Index_Class.Request_code_webupdate);
		}
	}

	private void login() {
		if (appState.getState().equalsIgnoreCase("login")) {
			Intent intent = new Intent(List_Avtivity_Main.this, Login.class);
			startActivity(intent);
		}

	}

	private void reload() {
		ENABLE_has_next_search_page = true;
		search_Index_Class = appState.get_index(has_NEXT_VIEW);
		offset = 0;
		webUpdate(search_Index_Class.Request_code_webupdate);

	}

	/************** Search Button Method ***************/
	private void next_search_page() {
		if (has_next_search_page) {
			ENABLE_has_next_search_page = true;
			search_Index_Class = appState.get_index(has_NEXT_VIEW);
			offset = 1;
			webUpdate(search_Index_Class.Request_code_webupdate);
		}
	}

	/************** Search Button Method ***************/
	private void prev_search_page() {
		if (page_index > 0) {
			ENABLE_has_next_search_page = true;
			search_Index_Class = appState.get_index(has_NEXT_VIEW);
			offset = -1;
			webUpdate(search_Index_Class.Request_code_webupdate);
		}
	}

	/************** Search Button Method ***************/
	private void search() {
		EditText Search = (EditText) findViewById(R.id.search);
		String search = Search.getText().toString();

		if (!search.isEmpty()) {
			webUpdate(Search_product_webupdate);
		}
	}

	/*
	 * Inner class that performs progress calculations on a second thread.
	 * Implement the thread by subclassing Thread and overriding its run()
	 * method. Also provide a setState(state) method to stop the thread
	 * gracefully.
	 */
	/************** Progress Threadr Class ***************/
	private class ProgressThread extends Thread {

		final static int DONE = 0;
		final static int RUNNING = 1;

		Handler mHandler;
		int mState;
		int total;
		int LOOP_TIMER;
		int checksum;

		ProgressThread(Handler h) {
			mHandler = h;
		}

		@Override
		public void run() {

			Message msg;
			Bundle b;
			checksum = 0;
			LOOP_TIMER = 0;

			mState = RUNNING;
			total = 100;
			while (mState == RUNNING) {
				if (typeBar == First_run_webupdate) {

					Log.d("Main", "Main : Thread Started 0");

					if (LOOP_TIMER == 0) {
						checksum = SearchItemType(Category_Request_Code);
						Log.d("First_run_webupdate", "Result in connection :"
								+ checksum);
						LOOP_TIMER++;
					} else if (LOOP_TIMER == 1) {

						ENABLE_has_next_search_page = false;
						checksum = SearchItem(Top_item);
						Log.d("First_run_webupdate", "Result in connection :"
								+ checksum);
						mState = DONE;
					}
				} else if (typeBar == Search_product_webupdate) {

					Log.d("Main", "Main : Thread Started 1");

					checksum = SearchItem(Top_search);
					Log.d("Search_product_webupdate", "Result in connection :"
							+ checksum);
					mState = DONE;

				} else if (typeBar == Search_category_webupdate) {

					Log.d("Main", "Main : Tread Started 2");

					if (LOOP_TIMER == 0) {
						checksum = SearchItemType(Sub_category_Request_Code);
						Log.d("Search_category_webupdate",
								"Result in connection :" + checksum);
						LOOP_TIMER++;
					} else if (LOOP_TIMER == 1) {
						checksum = SearchItem(Top_category);
						Log.d("Search_category_webupdate",
								"Result in connection :" + checksum);
						mState = DONE;
					}
				} else if (typeBar == Search_subcategory_webupdate) {

					checksum = SearchItem(Sub_category);
					mState = DONE;

				} else if (typeBar == Search_showroom_webupdate) {

					Log.d("Main", "Main : Tread Started 3");

					checksum = SearchItem(Top_category);
					Log.d("Search_showroom_webupdate", "Result in connection :"
							+ checksum);
					mState = DONE;
				}
				Log.d("Webupdate Thread", "Result in connection :" + checksum);
				if (checksum == 1) {

					total = Spinner_Inflater_Handler;
					msg = mHandler.obtainMessage();
					b = new Bundle();
					b.putInt("total", total);
					msg.setData(b);
					mHandler.sendMessage(msg);

				} else if (checksum == 2) {

					total = List_Inflater_Handler;
					msg = mHandler.obtainMessage();
					b = new Bundle();
					b.putInt("total", total);
					msg.setData(b);
					mHandler.sendMessage(msg);

					if (EDIT_has_next_search_page) {
						appState.set_index(has_NEXT_VIEW, offset);
					} else if (ADD_has_next_search_page) {
						has_NEXT_VIEW++;
						appState.create_session(json, URI, check, usage_mode,
								has_NEXT_VIEW, typeBar);
					}
					EDIT_has_next_search_page = false;
					ADD_has_next_search_page = false;

					if (typeBar == Search_showroom_webupdate) {
						check_showroom = true;
					} else {
						check_showroom = false;
					}
					search_error_flag = false;
				} else {
					search_error_flag = true;
					check_showroom = false;
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

	/************** Row Data Class ***************/
	private class RowData {
		protected int mId = -1;
		protected String mTitle = null;
		protected String mImage = null;
		protected String mDetail = null;

		RowData(int id, String title, String detail) {
			mId = id;
			mTitle = title;
			mDetail = detail;
			mImage = null;
		}

		RowData(String image, String title, String detail) {
			mImage = image;
			mTitle = title;
			mDetail = detail;
			mId = -1;
		}

		@Override
		public String toString() {
			return mId + " " + mTitle + " " + mDetail;
		}

		@SuppressWarnings("unused")
		public String toString1() {
			return mImage + " " + mTitle + " " + mDetail;
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
			ImageView i11 = null;
			RowData rowData = getItem(position);

			if (null == convertView) {
				convertView = mInflater.inflate(R.layout.list, null);
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}

			holder = (ViewHolder) convertView.getTag();
			title = holder.gettitle();
			title.setText(rowData.mTitle);
			detail = holder.getdetail();
			detail.setText(rowData.mDetail);

			i11 = holder.getImage();

			Log.d("Main", "Main : Before Gettin image correctly");
			if (rowData.mId != -1) {
				Log.d("Main", "Main : Gettin image correctly ");
				i11.setImageResource(imgid[rowData.mId]);
			} else {
				String URI = rowData.mImage;
				Log.d("Main", "Main : Gettin image correctly : " + URI);
				Drawable drawable = LoadImageFromWebOperations(URI);
				i11.setImageDrawable(drawable);
			}

			return convertView;
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
				InputStream is = conn.getInputStream();

				Bitmap bmImg = BitmapFactory.decodeStream(is);
				Drawable d = new BitmapDrawable(getResources(), bmImg);
				return d;
			} catch (IOException e) {
				// TODO Better error handling
				e.printStackTrace();
				return null;
			}

			/*
			 * try { Log.d("Main", "Main : Loading Image Started"); InputStream
			 * is = (InputStream) new URL(url).getContent(); Drawable d =
			 * Drawable.createFromStream(is, "src name"); Log.d("Main",
			 * "Main : Returning Image "); return d; } catch (Exception e) {
			 * System.out.println("Exc=" + e); return null; }
			 */
		}

		/************** View Holder Class ***************/
		private class ViewHolder {

			private final View mRow;
			private TextView title = null;
			private TextView detail = null;
			private ImageView i11 = null;

			/************** View Holder Method ***************/
			public ViewHolder(View row) {
				mRow = row;
			}

			/************** Title View Method ***************/
			public TextView gettitle() {
				if (null == title) {
					title = (TextView) mRow.findViewById(R.id.title);
				}
				return title;
			}

			/************** Text View Method ***************/
			public TextView getdetail() {
				if (null == detail) {
					detail = (TextView) mRow.findViewById(R.id.detail);
				}
				return detail;
			}

			/************** Image View Method ***************/
			public ImageView getImage() {
				if (null == i11) {
					i11 = (ImageView) mRow.findViewById(R.id.img);
				}
				return i11;
			}
		}
		/************** View Holder Class Ends ***************/
	}
	/************** Custom Adapter Class Ends ***************/
}
