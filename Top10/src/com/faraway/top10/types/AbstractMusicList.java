package com.faraway.top10.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import android.content.Context;


public abstract class AbstractMusicList {

	protected Context context;
	protected ArrayList<Song> songList = null;

	public AbstractMusicList(Context context){
		this.context = context;
	}


	private ArrayList<Song> readSongList()
	{
		File cacheFile = new File(context.getFilesDir().toString() + "/" + getCacheFileName());

		ArrayList<Song> list = null;
		if (cacheFile.exists()) {
			FileInputStream fileInputStream;
			try {
				fileInputStream = context.openFileInput(getCacheFileName());
				ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
				list = ((ArrayList<Song>)inputStream.readObject());
				inputStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return list;
	}


	public ArrayList<Song> getSongList() 
	{
		if (songList == null) 
		{
			songList = readSongList();

			if (songList == null) {
				songList = refreshSongList();
			}
		}
		return songList;
	}

	protected void writeToFile(Object object) throws IOException {
		//Sarkilar serialization yontemi ile dosyaya yaziliyor
		FileOutputStream fileOutputStream;
		fileOutputStream = context.openFileOutput(getCacheFileName(), Context.MODE_PRIVATE);
		ObjectOutputStream outputStream = new ObjectOutputStream(fileOutputStream);
		outputStream.writeObject(object);
		outputStream.close();
	}


	public ArrayList<Song> refreshSongList() {
		deleteFiles();
		try {
			URL url = new URL(getURL());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setReadTimeout(10000);
			urlConnection.setConnectTimeout(15000);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);			
			urlConnection.connect();

			InputStream in = urlConnection.getInputStream(); //getAssets().open("kralfmtop10.htm");

			BufferedReader reader = new BufferedReader(new InputStreamReader(in,Charset.forName("ISO-8859-9")));

			String content = new String();
			String line;
			while ((line = reader.readLine()) != null){
				content = content.concat(line);
			}
			//Indirilen html dosyasi parse edilerek icindeki sarkilar bulunuyor.
			songList = parse(content);
			in.close();

			writeToFile(songList);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return songList;
	}

	public String getCapitilize(String str, Locale locale) 
	{
		str = str.toLowerCase(locale);
		str = StringUtils.capitaliseAllWords(str);
		return str;
	}

	public void downloadFile(String address, String fileName){
		try {
			URL url = new URL(address);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setReadTimeout(10000);
			urlConnection.setConnectTimeout(15000);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);			
			urlConnection.connect();

			InputStream in = urlConnection.getInputStream(); //getAssets().open("kralfmtop10.htm");

			File f = new File(fileName);
			FileOutputStream fileOutputStream = new FileOutputStream(f);

			byte[] buffer = new byte[1024];
			int readCount;
			while( (readCount = in.read(buffer, 0, buffer.length)) > 0) {
				fileOutputStream.write(buffer, 0, readCount);
			}
			fileOutputStream.close();
			f.setReadable(true, false);
			in.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteFiles()
	{
		ArrayList<Song> list = readSongList();
		if (list != null) {
			for (int i = 0; i < list.size(); i++){
				String fullPath = list.get(i).fileFullPath;
				if (fullPath != null) {
					File f = new File(fullPath);
					if (f.exists()) {
						f.delete();
					}
				}
			}
		}
	}

	public abstract String getURL();

	protected abstract ArrayList<Song> parse(String content);

	public abstract String getCacheFileName();

	public abstract String getMusicListName();

	protected String getContentFromURL(String urlString) {
		String content = new String();

		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setReadTimeout(10000);
			urlConnection.setConnectTimeout(15000);
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);			
			urlConnection.connect();

			InputStream in = urlConnection.getInputStream(); //getAssets().open("kralfmtop10.htm");

			BufferedReader reader = new BufferedReader(new InputStreamReader(in,Charset.forName("ISO-8859-9")));


			String line;
			while ((line = reader.readLine()) != null){
				content = content.concat(line);
			}
			//Indirilen html dosyasi parse edilerek icindeki sarkilar bulunuyor.
			//songList = parse(content);
			in.close();


		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

}
