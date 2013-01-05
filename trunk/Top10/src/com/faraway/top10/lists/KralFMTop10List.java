package com.faraway.top10.lists;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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

public class KralFMTop10List extends AbstractMusicList {
	
	private static final String CACHE_FILE = "KralTop10List";
	private static final String LIST_NAME = "KralFM";
	private static final String URL = "http://www.kralfm.com.tr/liste.asp";
 
	/**
	 * Constructor 
	 * @param context
	 */
	public KralFMTop10List(Context context) {
		super(context);
	}


	@Override
	public String getCacheFileName() {
		return CACHE_FILE;
	}
	
	
	/**
	 * URL(http://www.kralfm.com.tr/liste.asp)'deki html kodunun icindeki sarkilari
	 * bulan ve onlari ArrayList<Song> icerisine ekleyen fonksiyon. Bu tip fonksiyonlari kodlamak
	 * icin URL'deki sayfanin html kodlarini incelemek gerekiyor.
	 * @param html URL deki adresin html kodu
	 * @return sarki listesini ArrayList<Song> olarak dondurur.
	 */
	public ArrayList<Song> parse(String html) {
		ArrayList<Song> songList = new ArrayList<Song>();
		Document doc = Jsoup.parse(html);
		Elements elements = doc.getElementById("chartt_archieve_list").getElementsByClass("noktanokta");

		for (int i = 0; i < elements.size(); i++) 
		{
			Song song = new Song();
			try {
				Element name = elements.get(i).child(1);
				String src = elements.get(i).child(5).getElementsByTag("embed").get(0).attributes().get("src");
				int startIndex = src.indexOf("file=") + "file=".length();
				int endIndex = src.indexOf(".mp3") + ".mp3".length();

				String url = null;
				if (endIndex > startIndex) {
					url = src.substring(startIndex, endIndex);
				}

				String mixedData = name.html();
				//StringEscapeUtils.unescapeHtml fonksiyonu turkce karakter problemini cozmek icin kullanildi.
				//Her zaman gerekmeyebilir. 
				song.singer = getCapitilize(StringEscapeUtils.unescapeHtml(mixedData.substring(0, mixedData.indexOf("<br />"))), new Locale("TR_tr"));
				song.name = getCapitilize(StringEscapeUtils.unescapeHtml(mixedData.substring(mixedData.indexOf("<b>")+"<b>".length(), mixedData.indexOf("</b>"))), new Locale("TR_tr"));
				song.mp3Url = null;
				if (url != null) {
					song.mp3Url = url;
				}
				
				song.fileFullPath = context.getFilesDir() + "/" + new String(LIST_NAME + song.singer + song.name + ".mp3").replaceAll("\\s", "");

				songList.add(song);
			}
			catch (Exception e) {
				Log.e(getClass().getName(), "Problem in parsing element " + i  + " isim: " + song.name);
			}
		}

		return songList;	
	}

	@Override
	public String getMusicListName() {
		return LIST_NAME;
	}

	@Override
	public String getURL() {
		return URL;
	}

}
