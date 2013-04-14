package com.faraway.top10.lists;

import java.util.ArrayList;
import java.util.Locale;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;

public class PowerHitsTop10 extends AbstractMusicList {

	private static final String CACHE_FILE = "PowerHitsTop10List";
	private static final String LIST_NAME = "PowerHits";
	private static final String URL = "http://www.powerfm.com.tr/muzik-listeleri";



	public PowerHitsTop10(Context context) {
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
		try {
			
			Document doc = Jsoup.parse(content);

			//block equi block-1 top-60
			Element listElements = doc.getElementsByClass("block-content").get(0);

			Elements info = listElements.getElementsByClass("info");
			Elements mp3List = listElements.getElementsByClass("play");

			for (int i = 0; i < info.size(); i++) {
				Song song = new Song();
				song.singer = info.get(i).getElementsByClass("name").get(0).text();
				song.singer = getCapitilize(song.singer, Locale.ENGLISH);

				song.name = info.get(i).getElementsByClass("desc").get(0).text();
				song.name = getCapitilize(song.name, Locale.ENGLISH);

				String mp3Url = mp3List.get(i).attr("rel");
				if (mp3Url != null) {
					song.mp3Url = mp3Url.replaceAll(" ", "%20");
				}
				song.fileFullPath = context.getFilesDir() + "/" + new String(LIST_NAME + song.singer + song.name + ".mp3").replaceAll("\\s", "");
				songList.add(song);			


			}
		}
		catch (Exception e) {

		}
		return songList;	
	}



	@Override
	public String getURL() {
		return URL;
	}

}
