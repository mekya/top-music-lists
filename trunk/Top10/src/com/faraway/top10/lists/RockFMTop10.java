package com.faraway.top10.lists;

import java.util.ArrayList;
import java.util.Locale;

import java.net.URLEncoder;

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

	private static final String CACHE_FILE = "RockFMTop10List16";
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

		//String mp3html= mp3List.html();
		
		//String encoded = URLEncoder.encode(mp3html, "UTF-8");
		
		
		for (int i = 0; i < 10; i++) {
			
			Song song = new Song();
			//Elements esinger = mp3List.get(i).getElementsByClass("h3");
			//String strsinger = esinger.html();
			
			//Elements ename = mp3List.get(i).getElementsByTag("strong");
			//String strname = ename.html();
			
			
			//song.singer = getCapitilize(StringEscapeUtils.unescapeHtml(strsinger.substring(strsinger.indexOf(">")+1, strsinger.indexOf("</span>"))), new java.util.Locale("TR_tr"));
			//song.name =   getCapitilize(StringEscapeUtils.unescapeHtml(strname.substring(strsinger.indexOf(">")-1, strname.indexOf("</span>"))), new java.util.Locale("TR_tr"));
			
				song.singer = mp3List.get(i).getElementsByClass("h3").text();
				song.singer = getCapitilize(song.singer, Locale.GERMAN);
				
				song.name = mp3List.get(i).getElementsByTag("strong").text();
				song.name = getCapitilize(song.name, Locale.GERMAN);
				
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
