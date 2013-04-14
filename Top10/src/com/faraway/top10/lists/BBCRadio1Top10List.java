package com.faraway.top10.lists;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;

import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;

public class BBCRadio1Top10List extends AbstractMusicList {

	private static final String CACHE_FILE = "BBCRadio1Top10List";
	private static final String LIST_NAME = "BBC Radio1";
	private static final String URL = "http://www.bbc.co.uk/radio1/chart/singles";

	public BBCRadio1Top10List(Context context) {
		super(context);
	}

	@Override
	public String getURL() {
		return URL;
	}

	@Override
	public ArrayList<Song> parse(String content) {

		int positionStart, positionEnd;
		ArrayList<Song> songList = new ArrayList<Song>();
		try {
			String xmlUrl;
			for (int i = 0; i < 10; i++) 
			{
				int position = content.indexOf("\""+ i +"\":");

				positionStart = content.indexOf("http:", position);

				positionEnd = content.indexOf("\"", positionStart+1);

				int diff = positionStart - position;

				Song song;

				if (diff == 6) {
					xmlUrl = content.substring(positionStart, positionEnd);
					String contentXml = getContentFromURL(xmlUrl);
					Document doc = Jsoup.parse(contentXml);

					Elements elements = doc.getElementsByTag("title");
					String title = elements.get(0).text();
					song = new Song();

					int splitter = title.indexOf("-");
					song.singer = title.substring(0, splitter).trim();
					song.name = title.substring(splitter+1);
					elements = doc.getElementsByTag("connection");
					song.mp3Url = elements.get(0).attr("href").trim();
					song.fileFullPath = context.getFilesDir() + "/" + new String(LIST_NAME + song.singer + song.name + ".mp3").replaceAll("\\s", "");

					songList.add(song);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return songList;
	}

	@Override
	public String getCacheFileName() {
		return CACHE_FILE;
	}

	@Override
	public String getMusicListName() {
		return LIST_NAME;
	}

}
