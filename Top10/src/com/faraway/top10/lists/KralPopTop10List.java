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

public class KralPopTop10List extends AbstractMusicList {

	private static final String CACHE_FILE = "KralPop10List";
	private static final String LIST_NAME = "KralPOP";
	private static final String URL = "http://www.kralfm.com.tr/liste.asp?type=2";
	private static final String videoPrefix = "http://kral-p.mncdn.com/KralFM/";

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public KralPopTop10List(Context context) {
		super(context);
	}

	@Override
	public String getCacheFileName() {
		return CACHE_FILE;
	}

	/**
	 * URL(http://www.kralfm.com.tr/liste.asp)'deki html kodunun icindeki
	 * sarkilari bulan ve onlari ArrayList<Song> icerisine ekleyen fonksiyon. Bu
	 * tip fonksiyonlari kodlamak icin URL'deki sayfanin html kodlarini
	 * incelemek gerekiyor.
	 * 
	 * @param html
	 *            URL deki adresin html kodu
	 * @return sarki listesini ArrayList<Song> olarak dondurur.
	 */
	public ArrayList<Song> parse(String html) {
		ArrayList<Song> songList = new ArrayList<Song>();
		try {
			Document doc = Jsoup.parse(html);
			Elements elements = doc.getElementById("chartt_archieve_list")
					.getElementsByClass("noktanokta");

			for (int i = 0; i < 10; i++) {
				Song song = new Song();
				try {
					Element name = elements.get(i).child(1);
					String src = elements.get(i).child(5)
							.getElementsByClass("mtn2").get(0).attributes()
							.get("onclick");

					int fileStartIndex = src.indexOf("'") + 1;
					int fileEndIndex = src.lastIndexOf("'");

					String url = null;
					if (fileEndIndex > fileStartIndex) {
						url = src.substring(fileStartIndex, fileEndIndex);
					}

					String mixedData = name.html();
					// StringEscapeUtils.unescapeHtml fonksiyonu turkce karakter
					// problemini cozmek icin kullanildi.
					// Her zaman gerekmeyebilir.
					song.singer = getCapitilize(
							StringEscapeUtils.unescapeHtml(mixedData.substring(0,
									mixedData.indexOf("<br />"))), new Locale(
											"TR_tr"));
					song.name = getCapitilize(
							StringEscapeUtils.unescapeHtml(mixedData.substring(
									mixedData.indexOf("<b>") + "<b>".length(),
									mixedData.indexOf("</b>"))),
									new Locale("TR_tr"));
					song.mp4Url = null;
					if (url != null) {
						song.mp4Url = videoPrefix + url;
					}

					song.fileFullPath = context.getFilesDir()
							+ "/"
							+ new String(LIST_NAME + song.singer + song.name
									+ ".mp4").replaceAll("\\s", "");

					songList.add(song);
				} catch (Exception e) {
					Log.e(getClass().getName(), "Problem in parsing element " + i
							+ " isim: " + song.name);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
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
