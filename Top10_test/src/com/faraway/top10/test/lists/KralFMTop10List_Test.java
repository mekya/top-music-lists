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
import com.faraway.top10.types.Song;

public class KralFMTop10List_Test extends InstrumentationTestCase{

	KralFMTop10List top10List;
	@Override
	protected void setUp() throws Exception {
		top10List = new KralFMTop10List(getInstrumentation().getTargetContext());
		
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

	public void testGetSongList()
	{
		//delete cache file if exists
		File f = new File(getInstrumentation().getTargetContext().getFilesDir().toString() + "/" + top10List.getCacheFileName());
		if (f.exists()) {
			f.delete();
		}
		
		long time1 = System.currentTimeMillis();
		ArrayList<Song> list = top10List.getSongList();
		assertEquals(list.size(), 10);
		long time2 = System.currentTimeMillis();
		ArrayList<Song> list2 = top10List.getSongList();
		assertEquals(list2.size(), 10);
		long time3 = System.currentTimeMillis(); 
		
		assertEquals(list, list2);
		
		//at least we should have 1 second performance
		assertTrue(((time2-time1)-1000) > (time3-time2));
		
	}
	
	
	public void testRefreshSongList(){
		ArrayList<Song> list1 = top10List.getSongList();
		ArrayList<Song> list = top10List.refreshSongList();
		
		assertEquals(list.size(), 10);
		
		assertSame(list, list1);
		
		
	}
	
	
	public void testParse(){
		try {
			
			InputStream in = getInstrumentation().getContext().getAssets().open("kralfmtop10.htm");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in,Charset.forName("ISO-8859-9")));

			String content = new String();
			String line;
			while ((line = reader.readLine()) != null){
				content = content.concat(line);
			}
			in.close();
			ArrayList<Song> songList = top10List.parse(content);
			
			assertEquals(songList.size(), 10);
			
			assertEquals(songList.get(0).singer, "Zara");
			assertEquals(songList.get(0).name, "Dilenci");
			assertEquals(songList.get(0).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-TUW219319_FFHUBKSV7060.mp3");

			assertEquals(songList.get(1).singer, "Sibel Can");
			assertEquals(songList.get(1).name, "Bilmesin O Felek");
			assertEquals(songList.get(1).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-ROA108208_DZLZPOXE6969.mp3");

			assertEquals(songList.get(2).singer, "Sibel Can");
			assertEquals(songList.get(2).name, "Bilmesin O Felek");
			assertEquals(songList.get(2).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-BNU542642_NYFRPMPS0393.mp3");

			assertEquals(songList.get(3).singer, "Mehmet Erdem");
			assertEquals(songList.get(3).name, "Hakim Bey");
			assertEquals(songList.get(3).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-KKH875975_WVSQKYKT4636.mp3");

			assertEquals(songList.get(4).singer, "Sila");
			assertEquals(songList.get(4).name, "Aslan Gibi");
			assertEquals(songList.get(4).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-KOG885976_XZSNHTPG4736.mp3");

			assertEquals(songList.get(5).singer, "Intizar");
			assertEquals(songList.get(5).name, "Sen Yarim Idun");
			assertEquals(songList.get(5).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-TJO219319_FUZONCMT7060.mp3");

			assertEquals(songList.get(6).singer, "Tarkan");
			assertEquals(songList.get(6).name, "Hatasiz Kul Olmaz");
			assertEquals(songList.get(6).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-HFP764864_TQAQQTSU2525.mp3");

			assertEquals(songList.get(7).singer, "Yildiz Tilbe");
			assertEquals(songList.get(7).name, "Askimi Sakla");
			assertEquals(songList.get(7).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-PXN097197_BIYUKPRI6858.mp3");

			assertEquals(songList.get(8).singer, "Demet Akalin");
			assertEquals(songList.get(8).name, "Turkan");
			assertEquals(songList.get(8).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-FTA653754_RELJPLVR2414.mp3");

			assertEquals(songList.get(9).singer, "Gokhan Tepe");
			assertEquals(songList.get(9).name, "Tanrim Dert Vermesin");
			assertEquals(songList.get(9).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-IPY774865_UAKBVQOU3625.mp3");

		} catch (IOException e) {
			fail(e.toString());
		}
	}





}
