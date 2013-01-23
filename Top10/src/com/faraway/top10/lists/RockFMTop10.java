package com.faraway.top10.lists;

import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;


public class RockFMTop10 extends AbstractMusicList  {

	private static final String CACHE_FILE = "RockFMTop10List6";
	private static final String LIST_NAME = "RockFM";
	private static final String URL = "http://www.rockfm.com.tr/Top20.aspx";

	public RockFMTop10(Context context) {
		super(context);
	}
	
	@Override
	public String getCacheFileName() {
		return CACHE_FILE;
	}



	@Override
	public String getMusicListName() {
		return LIST_NAME;
	}

	
	@Override
	protected ArrayList<Song> parse(String content) {
		
		ArrayList<Song> songList = new ArrayList<Song>();
		Document doc = Jsoup.parse(content);

		
		Element listElements = doc.getElementsByClass("box20").get(0);

		Elements mp3List = listElements.getElementsByTag("a");

		String mp3html= mp3List.html();
		
		for (int i = 0; i < 10; i++) {
			
			Song song = new Song();
			
			 
			
				song.singer = mp3List.get(i).getElementsByClass("h3").text();
				song.singer = getCapitilize(song.singer, Locale.ENGLISH);
				
				song.name = mp3List.get(i).getElementsByTag("strong").text();
				song.name = getCapitilize(song.name, Locale.ENGLISH);
				
				String mp3Url = mp3List.get(i).attr("href");
				
				if (mp3Url != null) {
					song.youtubeURL = mp3Url;
				}
				
				song.fileFullPath = null;
				songList.add(song);			

		}
		
		return songList;	
		}
	

	@Override
	public String getURL() {
		return URL;
	}

	
	
	
}
