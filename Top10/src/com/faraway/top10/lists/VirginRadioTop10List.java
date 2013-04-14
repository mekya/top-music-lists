package com.faraway.top10.lists;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;

public class VirginRadioTop10List extends AbstractMusicList {

	private static final String CACHE_FILE = "com.faraway.top10.lists.VirginRadioTop10List";
	private static final String LIST_NAME = "VirginRadio";
	private static final String URL = "http://www.virginradioturkiye.com/song/6859/drz6r2ul/chart30/a/b";
	private static final String BASE_URL = "http://www.virginradioturkiye.com/ajax";


	public VirginRadioTop10List(Context context) {
		super(context);
	}

	@Override
	public String getURL() {
		return URL;
	}

	@Override
	public ArrayList<Song> parse(String content) {
		ArrayList<Song> songList = new ArrayList<Song>();

		try {
			Document doc = Jsoup.parse(content);

			//block equi block-1 top-60
			Element listContainer = doc.getElementById("chartsListContainer");
			Elements infoElements = listContainer.getElementsByClass("info");

			Element info;
			for (int i = 0; i < 10; i++) { // there are 30 songs in list we fetch top 10
				info = infoElements.get(i).getElementsByClass("clearfix").get(0);

				String songName = info.getElementsByTag("strong").get(0).text(); //songElement.text();
				String songAndSinger = info.text();
				String singer = songAndSinger.substring(songAndSinger.indexOf(songName) + songName.length());

				Song song = new Song();
				song.name = songName.trim(); 
				song.singer = singer.trim();

				String dataUrl = BASE_URL + infoElements.get(i).getElementsByTag("a").get(0).attr("data-url");

				song.youtubeURL = fetchYouTubeVideoUrl(dataUrl);

				songList.add(song);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return songList;
	}

	public String fetchYouTubeVideoUrl(String urlString) {

		String content = getContentFromURL(urlString);
		Document doc = Jsoup.parse(content);
		String songYoutubeUrl = doc.getElementsByTag("iframe").get(0).attr("src");
		System.out.println(songYoutubeUrl);
		return songYoutubeUrl;
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
