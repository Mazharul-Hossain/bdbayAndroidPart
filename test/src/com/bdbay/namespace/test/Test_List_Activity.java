package com.bdbay.namespace.test;

import android.graphics.Rect;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;

import com.bdbay.namespace.List_Avtivity_Main;
import com.bdbay.namespace.R;

public class Test_List_Activity extends
		ActivityInstrumentationTestCase2<List_Avtivity_Main> {

	private Button result;
	private View mainLayout;

	public Test_List_Activity() {
		super("com.bdbay.namespace", List_Avtivity_Main.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		List_Avtivity_Main mainActivity = getActivity();
		result = (Button) mainActivity.findViewById(R.id.searchButton);
		mainLayout = mainActivity.findViewById(R.id.mainLayout);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testAddButtonOnScreenfullWidth() {
		int fullWidth = mainLayout.getWidth();
		int[] mainLayoutLocation = new int[2];
		mainLayout.getLocationOnScreen(mainLayoutLocation);
		int[] viewLocation = new int[2];
		result.getLocationOnScreen(viewLocation);
		Rect outRect = new Rect();
		result.getDrawingRect(outRect);

		assertTrue("Add button off the right of the screen", fullWidth
				+ mainLayoutLocation[0] > outRect.width() + viewLocation[0]);
	}

	public void testAddButtonOnScreenfullHeight() {
		int fullHeight = mainLayout.getHeight();
		int[] mainLayoutLocation = new int[2];
		mainLayout.getLocationOnScreen(mainLayoutLocation);
		int[] viewLocation = new int[2];
		result.getLocationOnScreen(viewLocation);
		Rect outRect = new Rect();
		result.getDrawingRect(outRect);

		assertTrue("Add button off the bottom of the screen", fullHeight
				+ mainLayoutLocation[1] > outRect.height() + viewLocation[1]);
	}

}
