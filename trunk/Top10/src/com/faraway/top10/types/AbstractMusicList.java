package com.faraway.top10.types;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import android.content.Context;


public abstract class AbstractMusicList {

	protected Context context;
	protected ArrayList<Song> songList = null;

	public AbstractMusicList(Context context){
		this.context = context;
	}

	public ArrayList<Song> getSongList() 
	{
		if (songList == null) 
		{
			File cacheFile = new File(context.getFilesDir().toString() + "/" + getCacheFileName());

			if (cacheFile.exists()) {
				FileInputStream fileInputStream;
				try {
					fileInputStream = context.openFileInput(getCacheFileName());
					ObjectInputStream inputStream = new ObjectInputStream(fileInputStream);
					songList = ((ArrayList<Song>)inputStream.readObject());
					inputStream.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			else {
				songList = refreshSongList();
			}
		}
		return songList;
	}


	public abstract String getCacheFileName();

	public abstract ArrayList<Song> refreshSongList();

	public abstract String getMusicListName();

}
