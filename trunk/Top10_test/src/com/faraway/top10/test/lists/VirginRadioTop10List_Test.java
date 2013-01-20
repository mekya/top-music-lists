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
import com.faraway.top10.lists.VirginRadioTop10List;
import com.faraway.top10.types.Song;

public class VirginRadioTop10List_Test extends InstrumentationTestCase{

	VirginRadioTop10List top10List;
	@Override
	protected void setUp() throws Exception {
		top10List = new VirginRadioTop10List(getInstrumentation().getTargetContext());
		
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
	
	public void testRefreshSongList(){
		
		ArrayList<Song> list = top10List.refreshSongList();
		assertEquals(list.size(), 10);
		
		//test again
		list = top10List.refreshSongList();
		
		assertEquals(list.size(), 10);
	}

	
	
	
	public void testParse(){
		try {
			
			InputStream in = getInstrumentation().getContext().getAssets().open("VirginRadio.html");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in,Charset.forName("ISO-8859-9")));

			String content = new String();
			String line;
			while ((line = reader.readLine()) != null){
				content = content.concat(line);
			}
			in.close();
			ArrayList<Song> songList = top10List.parse(content);
			
			assertEquals(songList.size(), 10);

			assertEquals(songList.get(0).singer, "will.i.am");
			assertEquals(songList.get(0).name, "Scream & Shout (feat. Britney Spears)");
			assertNull(songList.get(0).mp3Url);
			assertNull(songList.get(0).fileFullPath);
			assertEquals(songList.get(0).youtubeURL, "http://www.youtube.com/embed/kYtGl1dX5qI?wmode=transparent");

			assertEquals(songList.get(1).singer, "Bruno Mars");
			assertEquals(songList.get(1).name, "Locked Out of Heaven");
			assertNull(songList.get(1).mp3Url);
			assertNull(songList.get(1).fileFullPath);
			assertEquals(songList.get(1).youtubeURL, "http://www.youtube.com/embed/e-fA-gBCkj0?wmode=transparent");
			
			assertEquals(songList.get(2).singer, "Lenka");
			assertEquals(songList.get(2).name, "Everything At Once");
			assertNull(songList.get(2).mp3Url);
			assertNull(songList.get(2).fileFullPath);
			assertEquals(songList.get(2).youtubeURL, "http://www.youtube.com/embed/Tfy5CBfjZ8s?wmode=transparent");
			
			assertEquals(songList.get(3).singer, "Pitbull");
			assertEquals(songList.get(3).name, "Feel This Moment (feat. Christina Aguilera)");
			assertNull(songList.get(3).mp3Url);
			assertNull(songList.get(3).fileFullPath);
			assertEquals(songList.get(3).youtubeURL, "http://www.youtube.com/embed/Oy8073O2Gqw?wmode=transparent");

			assertEquals(songList.get(4).singer, "Olly Murs");
			assertEquals(songList.get(4).name, "Troublemaker (feat. Flo Rida)");
			assertNull(songList.get(4).mp3Url);
			assertNull(songList.get(4).fileFullPath);
			assertEquals(songList.get(4).youtubeURL, "http://www.youtube.com/embed/4aQDOUbErNg?wmode=transparent");

			assertEquals(songList.get(5).singer, "Rihanna");
			assertEquals(songList.get(5).name, "Diamonds");
			assertNull(songList.get(5).mp3Url);
			assertNull(songList.get(5).fileFullPath);
			assertEquals(songList.get(5).youtubeURL, "http://www.youtube.com/embed/lWA2pjMjpBs?wmode=transparent");

			assertEquals(songList.get(6).singer, "Ke$ha");
			assertEquals(songList.get(6).name, "Die Young");
			assertNull(songList.get(6).mp3Url);
			assertNull(songList.get(6).fileFullPath);
			assertEquals(songList.get(6).youtubeURL, "http://www.youtube.com/embed/NOubzHCUt48?wmode=transparent");
			
			assertEquals(songList.get(7).singer, "Calvin Harris");
			assertEquals(songList.get(7).name, "Sweet Nothing (feat. Florence Welch)");
			assertNull(songList.get(7).mp3Url);
			assertNull(songList.get(7).fileFullPath);
			assertEquals(songList.get(7).youtubeURL, "http://www.youtube.com/embed/17ozSeGw-fY?wmode=transparent");
			
			assertEquals(songList.get(8).singer, "Loreen");
			assertEquals(songList.get(8).name, "My Heart Is Refusing Me");
			assertNull(songList.get(8).mp3Url);
			assertNull(songList.get(8).fileFullPath);
			assertEquals(songList.get(8).youtubeURL, "http://www.youtube.com/embed/xPOBdqHWios?wmode=transparent");
			
			assertEquals(songList.get(9).singer, "The Script");
			assertEquals(songList.get(9).name, "Hall of Fame (feat. will.i.am)");
			assertNull(songList.get(9).mp3Url);
			assertNull(songList.get(9).fileFullPath);
			assertEquals(songList.get(9).youtubeURL, "http://www.youtube.com/embed/mk48xRzuNvA?wmode=transparent");
			
//			assertEquals(songList.get(1).singer, "Sibel Can");
//			assertEquals(songList.get(1).name, "Bilmesin O Felek");
//			assertEquals(songList.get(1).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-ROA108208_DZLZPOXE6969.mp3");
//
//			assertEquals(songList.get(2).singer, "Sibel Can");
//			assertEquals(songList.get(2).name, "Bilmesin O Felek");
//			assertEquals(songList.get(2).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-BNU542642_NYFRPMPS0393.mp3");
//
//			assertEquals(songList.get(3).singer, "Mehmet Erdem");
//			assertEquals(songList.get(3).name, "Hakim Bey");
//			assertEquals(songList.get(3).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-KKH875975_WVSQKYKT4636.mp3");
//
//			assertEquals(songList.get(4).singer, "Sila");
//			assertEquals(songList.get(4).name, "Aslan Gibi");
//			assertEquals(songList.get(4).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-KOG885976_XZSNHTPG4736.mp3");
//
//			assertEquals(songList.get(5).singer, "Intizar");
//			assertEquals(songList.get(5).name, "Sen Yarim Idun");
//			assertEquals(songList.get(5).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-TJO219319_FUZONCMT7060.mp3");
//
//			assertEquals(songList.get(6).singer, "Tarkan");
//			assertEquals(songList.get(6).name, "Hatasiz Kul Olmaz");
//			assertEquals(songList.get(6).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-HFP764864_TQAQQTSU2525.mp3");
//
//			assertEquals(songList.get(7).singer, "Yildiz Tilbe");
//			assertEquals(songList.get(7).name, "Askimi Sakla");
//			assertEquals(songList.get(7).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-PXN097197_BIYUKPRI6858.mp3");
//
//			assertEquals(songList.get(8).singer, "Demet Akalin");
//			assertEquals(songList.get(8).name, "Turkan");
//			assertEquals(songList.get(8).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-FTA653754_RELJPLVR2414.mp3");
//
//			assertEquals(songList.get(9).singer, "Gokhan Tepe");
//			assertEquals(songList.get(9).name, "Tanrim Dert Vermesin");
//			assertEquals(songList.get(9).mp3Url, "http://www.kralfm.com.tr/images/haberler/11682-IPY774865_UAKBVQOU3625.mp3");

		} catch (IOException e) {
			fail(e.toString());
		}
	}




}
