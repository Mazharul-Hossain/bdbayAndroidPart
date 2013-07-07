package com.bdbay.namespace;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

public class Dialog_Photo extends Activity {

	Bitmap bm;

	MyAppMyState appState;

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;

	/************** Override onCreate Method ***************/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_photo);

		appState = (MyAppMyState) this.getApplicationContext();

		Log.d("Dialog_Photo", "Class Dialog_Photo Starting from here");
	}

	/************** Override onButton Click Listener Method ***************/
	public void listener(View view) {
		Intent intent;
		switch (view.getId()) {
		case R.id.take_camera_photo_button:

			Log.d("Dialog_Photo", "take_camera_photo_button Starting from here");

			intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, PICK_FROM_CAMERA);
			break;
		case R.id.take_gallery_photo_button:

			Log.d("Dialog_Photo", "take_camera_photo_button Starting from here");

			intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"),
					PICK_FROM_FILE);
			break;
		}
	}

	/************** Override on Activity Return Result Method ***************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			Log.d("Dialog_Photo", "RESULT_OK Starting from here");
			if (requestCode == PICK_FROM_CAMERA) {

				Log.d("Dialog_Photo", "PICK_FROM_CAMERA Starting from here");

				bm = (Bitmap) data.getExtras().get("data");

				save_photo();
			} else if (requestCode == PICK_FROM_FILE) {

				Log.d("Dialog_Photo", "PICK_FROM_FILE Starting from here");

				Uri selectedImageUri = data.getData();

				String selectedImagePath;
				// MEDIA GALLERY
				selectedImagePath = getPath(selectedImageUri);

				/*
				 * String filemanagerstring; // OI FILE Manager
				 * filemanagerstring = selectedImageUri.getPath(); // DEBUG
				 * PURPOSE - you can delete this if you want if
				 * (selectedImagePath != null)
				 * System.out.println(selectedImagePath); else
				 * System.out.println("selectedImagePath is null"); if
				 * (filemanagerstring != null)
				 * System.out.println(filemanagerstring); else
				 * System.out.println("filemanagerstring is null");
				 */

				// NOW WE HAVE OUR WANTED STRING
				if (selectedImagePath != null) {
					Log.d("Dialog_Photo",
							"selectedImagePath Starting from here");
					bm = BitmapFactory.decodeFile(selectedImagePath);
					save_photo();
				} else
					Log.d("Dialog_Photo", "selectedImagePath failed here");
			}

		}
	}

	// UPDATED!
	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	protected void save_photo() {
		/*
		 * SharedPreferences image = getSharedPreferences("Image", 0);
		 * SharedPreferences.Editor editor = image.edit();
		 * editor.putString("key", "value");
		 */

		Intent data = new Intent();
		appState.setbitmapImage(bm);
		// Activity finished ok, return the data pathName
		setResult(Activity.RESULT_OK, data);
		Log.d("Dialog_Photo", "save_photo Finished from here");

		finish();
	}

}
