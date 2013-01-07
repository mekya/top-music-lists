package com.faraway.top10.test.lists;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.test.InstrumentationTestCase;

import com.faraway.top10.lists.KralFMTop10List;
import com.faraway.top10.lists.PowerHitsTop10;
import com.faraway.top10.types.Song;

public class PowerTurkTop10List_Test extends InstrumentationTestCase{

	PowerHitsTop10 top10List;
	@Override
	protected void setUp() throws Exception {
		top10List = new PowerHitsTop10(getInstrumentation().getTargetContext());
		
		super.setUp();
	}

	protected void tearDown() throws Exception {
		top10List = null;
	};

	//check to see cache file name is assigned
	public void testGetCacheFileName(){
		assertNotNull(top10List.getCacheFileName());
	}

	// check to see music list name is assigned
	public void testGetMusicListName(){
		assertNotNull(top10List.getMusicListName());
	}

//	public void testGetSongList()
//	{
//		//delete cache file if exists
//		File f = new File(getInstrumentation().getTargetContext().getFilesDir().toString() + "/" + top10List.getCacheFileName());
//		if (f.exists()) {
//			f.delete();
//		}
//		
//		long time1 = System.currentTimeMillis();
//		ArrayList<Song> list = top10List.getSongList();
//		assertEquals(list.size(), 10);
//		long time2 = System.currentTimeMillis();
//		ArrayList<Song> list2 = top10List.getSongList();
//		assertEquals(list2.size(), 10);
//		long time3 = System.currentTimeMillis(); 
//		
//		assertEquals(list, list2);
//		
//		//at least we should have 1 second performance
//		assertTrue(((time2-time1)-1000) > (time3-time2));
//		
//	}
	
	
//	public void testRefreshSongList(){
//		ArrayList<Song> list1 = top10List.getSongList();
//		ArrayList<Song> list = top10List.refreshSongList();
//		
//		assertEquals(list.size(), 10);
//		
//		assertSame(list, list1);
//		
//	}
	
	
	public void testParse(){
		
	}





}
