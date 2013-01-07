package com.faraway.top10.test.lists;

import android.support.v4.view.ViewPager;
import android.test.ActivityInstrumentationTestCase2;

import com.faraway.top10.SongListActivity;

public class SongListActivity_Test extends ActivityInstrumentationTestCase2<SongListActivity> {

	public SongListActivity_Test() {
		super("com.faraway.top10", SongListActivity.class);
	}

	public void testIsOpened() {
		SongListActivity songListActivity = this.getActivity();
		ViewPager pagerView = (ViewPager) songListActivity.findViewById(com.faraway.top10.R.id.pager);
		
		assertNotNull(pagerView);
		assertTrue(pagerView.getChildCount() > 0);
	}


}
