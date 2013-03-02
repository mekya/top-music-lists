package com.faraway.top10.lists;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;

public class DreamTvTop10List extends AbstractMusicList {

	private static final String CACHE_FILE = "DreamTvTop10List";
	private static final String LIST_NAME = "Dreamtv";
	private static final String URL = "http://www.dreamtv.com.tr/";

	public DreamTvTop10List(Context context) {
		super(context);
	}

	@Override
	public String getURL() {
		return URL;
	}

	@Override
	protected ArrayList<Song> parse(String content) {
		ArrayList<Song> songList = new ArrayList<Song>();
		try {
			
			Document doc = Jsoup.parse(content);

			Elements elements = doc.select("body").get(0)
					.getElementsByClass("dreamtop")
					.get(1).getElementsByClass("text");

			for (int i = 0; i<10; i++) {
				Song song = new Song();
				
				Element artistName = elements.get(i).select(".artist").first();
				Element songName = elements.get(i).select(".song").first();
				
				song.name = songName.text();
				song.singer = artistName.text();
				String youtubePageUrl = artistName.getElementsByTag("a").get(0).attr("abs:href");
				
				String youtubePage = getContentFromURL(youtubePageUrl);
				
				Document mp4Document = Jsoup.parse(youtubePage);
				
				String youtubeURL = mp4Document.getElementsByClass("ic-sag")
						.get(0).getElementsByTag("iframe").get(0).attr("src");
				
				song.youtubeURL = youtubeURL;
				songList.add(song);
			
			}

		} catch (Exception e) {
			Log.e(this.getClass().getName(), e.getMessage());
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
