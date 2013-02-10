package com.faraway.top10.test.lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.faraway.top10.lists.BBCRadio1Top10List;
import com.faraway.top10.lists.KralFMTop10List;
import com.faraway.top10.types.Song;

import android.test.InstrumentationTestCase;

public class BBCRadio1Top10List_Test extends InstrumentationTestCase{
	
	BBCRadio1Top10List top10List;
	
	protected void setUp() throws Exception {
		top10List = new BBCRadio1Top10List(getInstrumentation().getTargetContext());
		
		super.setUp();
	}

	protected void tearDown() throws Exception {
		top10List = null;
	};
	
	public void testParse(){
		try {
			
			InputStream in = getInstrumentation().getContext().getAssets().open("bbcradio1.htm");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(in,Charset.forName("ISO-8859-9")));

			String content = new String();
			String line;
			while ((line = reader.readLine()) != null){
				content = content.concat(line);
			}
			in.close();
			ArrayList<Song> list = top10List.parse(content);
			
			assertNotNull(list);
			assertEquals(list.size(), 10);
			

		} catch (IOException e) {
			fail(e.toString());
		}
	}

}
