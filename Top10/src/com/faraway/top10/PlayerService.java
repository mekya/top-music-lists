package com.faraway.top10;

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
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.faraway.top10.lists.KralFMTop10List;
import com.faraway.top10.lists.PowerHitsTop10;
import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class PlayerService extends Service implements OnCompletionListener, OnPreparedListener{


	private MediaPlayer mediaPlayer = null;
	private int playingSongIndex = -1;
	public 	static final String SONG_INDEX = "Player.SONG_INDEX";
	private static final int NOTIFICATION_ID = 1;
	public static final String PLAYING_SONG_CHANGED = "PlayerService.PLAYING_SONG_CHANGED";
	

	private PSBinder iBinder = new PSBinder();
	private boolean mediaPlayerIsActive = false;
	private MediaPlayer tempMediaPlayer;
	public static final String SONG_NAME = "SONG_NAME";
	public static final String LIST_INDEX = "Player.LIST_INDEX";


	private ArrayList<AbstractMusicList> musicLists = new ArrayList<AbstractMusicList>();
	private int activeMusicList = 0;
	
	public class PSBinder extends Binder {
		public PlayerService getService(){
			return PlayerService.this;
		}
	}
	@Override
	public void onCreate() {		
		musicLists.add(new KralFMTop10List(getApplicationContext()));
//		new Thread() {
//			public void run() {
//				new PowerHitsTop10(getApplicationContext()).refreshSongList();
//			};
//			
//		}.start();
		musicLists.add(new PowerHitsTop10(getApplicationContext()));
		super.onCreate();
	}
	
	
	private AbstractMusicList getActiveList() {
		return this.musicLists.get(activeMusicList);
	}


	public ArrayList<Song> refreshSongList(int listIndex) 
	{
		return this.musicLists.get(listIndex).refreshSongList();		
	}

	/**
	 * Returns the song list of active music list.
	 * @param listIndex 
	 * @return
	 */
	public ArrayList<Song> getSongList(int listIndex) {
		ArrayList<Song> songList = null;
		if (musicLists.size() > listIndex) {
			songList = musicLists.get(listIndex).getSongList();		
		}
		return songList;
	}


	public void onPrepared(MediaPlayer mp) 
	{
		Song song = getActiveList().getSongList().get(this.playingSongIndex);
		notify(song.singer, song.name, null);
		mediaPlayer.start();
	}


	public void onCompletion(MediaPlayer mp) 
	{
		mediaPlayerIsActive = false;
		mp.release();
		mp = null;
		mediaPlayer = null;
		this.playingSongIndex++;
		prepare(this.playingSongIndex);
	}

	@SuppressWarnings("deprecation")
	private void notify(String singer, String song, String extra) {
		Intent intent = new Intent(this, SongListActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

		if (extra != null) {
			extra = " (" + extra +")";			
		}
		else {
			extra = "";
		}

		Notification notification = new Notification(R.drawable.icon, getString(R.string.app_name), System.currentTimeMillis());
		notification.setLatestEventInfo(getApplicationContext(), song, singer + extra, pendingIntent);

		startForeground(NOTIFICATION_ID, notification);
	}

	public String getPlayingSongName(){
		String name = null;
		if (this.playingSongIndex > -1) {
			name = getActiveList().getSongList().get(this.playingSongIndex).name;
		}
		return name;
	}

	private void prepare(int songIndex) 
	{
		if (getActiveList().getSongList().size() > songIndex) {
			mediaPlayer = new MediaPlayer();

			try {
				Song song = getActiveList().getSongList().get(songIndex);
				if (song == null || song.mp3Url == null) {

					Toast.makeText(getApplicationContext(), 
							getString(R.string.problem_in_song), Toast.LENGTH_LONG).show();
					songIndex++;
					prepare(songIndex);
					return;
				}
				mediaPlayer.setDataSource(URLDecoder.decode(song.mp3Url, "ISO-8859-1"));
				mediaPlayer.prepareAsync();
				mediaPlayer.setOnPreparedListener(this);
				mediaPlayer.setOnCompletionListener(this);
				mediaPlayerIsActive = true;

				this.playingSongIndex = songIndex;
				notify(song.singer, song.name, getString(R.string.loading));
				//send broadcast with playingSongIndex and songName 
				Intent i = new Intent(PlayerService.PLAYING_SONG_CHANGED);
				i.putExtra(SONG_INDEX, this.playingSongIndex);
				i.putExtra(LIST_INDEX, activeMusicList);
				i.putExtra(SONG_NAME , getActiveList().getSongList().get(this.playingSongIndex).name);
				sendBroadcast(i);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {			
			this.playingSongIndex = -1;
			stopForeground(true);
			//send broadcast with playingSongIndex = -1 if list is finished.
			Intent i = new Intent(PlayerService.PLAYING_SONG_CHANGED);
			i.putExtra(SONG_INDEX, this.playingSongIndex);	
			i.putExtra(LIST_INDEX, this.activeMusicList);
			sendBroadcast(i);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return iBinder;
	}

	public int getPlayingSongIndex() {
		return playingSongIndex;
	}

	public void stop() {
		this.playingSongIndex = -1;
		this.activeMusicList = -1;
		stopForeground(true);
		mediaPlayerIsActive = false;
		if (mediaPlayer != null) 
		{
			tempMediaPlayer = mediaPlayer;
			mediaPlayer = null;
			// sometimes "release" take so much time so let's do it in another thread.
			new Thread(){
				public void run() {
					tempMediaPlayer.release();
					tempMediaPlayer = null;
				};
			}.start();
		}
	}

	public void play(final int listIndex, final int position) {
		if (activeMusicList != listIndex) {
			//send broadcast if music in another list is going to be played
			this.playingSongIndex = -1;
			Intent i = new Intent(PlayerService.PLAYING_SONG_CHANGED);
			i.putExtra(SONG_INDEX, this.playingSongIndex);	
			i.putExtra(LIST_INDEX, activeMusicList);
			sendBroadcast(i);
		}
		stop();
		this.activeMusicList = listIndex;
		prepare(position);
	}

	public boolean isPlaying() {
		boolean isPlaying = false;
		if (mediaPlayerIsActive == true) 
		{
			isPlaying = true;
		}
		return isPlaying;
	}

	public ArrayList<AbstractMusicList> getMusicLists() {
		return musicLists;
	}


	public int getActiveListIndex() {
		return activeMusicList;
	}

}
