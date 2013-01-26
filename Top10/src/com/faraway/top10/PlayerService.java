package com.faraway.top10;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.faraway.top10.lists.KralFMTop10List;
import com.faraway.top10.lists.PowerHitsTop10;
import com.faraway.top10.lists.RockFMTop10;
import com.faraway.top10.lists.VirginRadioTop10List;
import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;

public class PlayerService extends Service implements OnCompletionListener, OnPreparedListener{



	private MediaPlayer mediaPlayer = null;
	private int playingSongIndex = -1;
	public 	static final String SONG_INDEX = "Player.SONG_INDEX";
	private static final int NOTIFICATION_ID = 1;
	public static final String PLAYING_SONG_CHANGED = "PlayerService.PLAYING_SONG_CHANGED";


	private Handler handler = new Handler();
	private PSBinder iBinder = new PSBinder();
	private boolean mediaPlayerIsActive = false;
	private MediaPlayer tempMediaPlayer;
	public static final String SONG_NAME = "SONG_NAME";
	public static final String LIST_INDEX = "Player.LIST_INDEX";
	public static final String DOWNLOAD_STARTED = "PlayerService.DOWNLOAD_STARTED";
	public static final String DOWNLOAD_FINISHED = "PlayerService.DOWNLOAD_FINISHED";
	public static final String SINGER_NAME = "PlayerService.SINGER_NAME";
	private static final String UPDATE_LISTS = "PlayerService.UPDATE_SONGS";


	private ArrayList<AbstractMusicList> musicLists = new ArrayList<AbstractMusicList>();
	private int activeMusicList = 0;
	private Thread downloadThread;

	public class PSBinder extends Binder {
		public PlayerService getService(){
			return PlayerService.this;
		}
	}
	@Override
	public void onCreate() {		
		musicLists.add(new PowerHitsTop10(getApplicationContext()));
		musicLists.add(new RockFMTop10(getApplicationContext()));
		musicLists.add(new VirginRadioTop10List(getApplicationContext()));
		musicLists.add(new KralFMTop10List(getApplicationContext()));
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Calendar timeOff = Calendar.getInstance();
		int days = Calendar.MONDAY + (7 - timeOff.get(Calendar.DAY_OF_WEEK)); // how many days until Sunday
		timeOff.add(Calendar.DATE, days);
		timeOff.set(Calendar.HOUR, 8);
		timeOff.set(Calendar.MINUTE, 0);
		timeOff.set(Calendar.SECOND, 0);
		
		int interval = 1000 * 60  * 60 * 24 * 7;
		Intent i = new Intent(getApplicationContext(), PlayerService.class);
		i.setAction(UPDATE_LISTS);
		PendingIntent pid = PendingIntent.getService(getApplicationContext(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC, timeOff.getTimeInMillis(), interval, pid);
		super.onCreate();
	}
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		if (action != null && action.equals(UPDATE_LISTS)) {
			
			new Thread() {
				public void run() {
					int size = musicLists.size();
					for (int i = 0; i < size; i++) {
						ArrayList<Song> oldList = musicLists.get(i).getSongList();
						ArrayList<Song> newList = musicLists.get(i).refreshSongList();
						if (isSongListEquals(oldList, newList) == false) {
							Intent intent = new Intent(PlayerService.this, SongListActivity.class);
							PendingIntent pendingIntent = PendingIntent.getActivity(PlayerService.this, 0, intent, 0);

							Notification notification = new Notification(R.drawable.icon, getString(R.string.app_name), System.currentTimeMillis());
							notification.setLatestEventInfo(getApplicationContext(), getString(R.string.lists_updated), "", pendingIntent);

							NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
							manager.notify(0, notification);
							break;
						}
					}
				};
			}.start();
			
		}
		return super.onStartCommand(intent, flags, startId);
	}

	public boolean isSongListEquals(ArrayList<Song> list1, ArrayList<Song> list2){
		if (list1.size() != list2.size()) {
			return false;
		}
		for(int i = 0; i < list1.size(); i++) {
			if(!list1.get(i).equals(list2.get(i)))
				return false;
		}
		
		return true;
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
	
	

	public Song getPlayingSong(){
		Song song = null;
		if (this.playingSongIndex > -1) {
			song = getActiveList().getSongList().get(this.playingSongIndex);
		}
		return song;
	}

	/**
	 * Prepares mediaPlayer object when mp3 file has been downloaded
	 * @param songIndex
	 * @param song
	 */
	private void prepareMediaPlayer(int songIndex, Song song){
		mediaPlayer = new MediaPlayer();
		try {

			mediaPlayer.setDataSource(song.fileFullPath);
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setOnCompletionListener(this);

		//	this.playingSongIndex = songIndex;
			notify(song.singer, song.name, getString(R.string.loading));
			//send broadcast with playingSongIndex and songName 
			Intent i = new Intent(PlayerService.PLAYING_SONG_CHANGED);
			i.putExtra(SONG_INDEX, this.playingSongIndex);
			i.putExtra(LIST_INDEX, activeMusicList);
			i.putExtra(SONG_NAME , song.name);
			i.putExtra(SINGER_NAME, song.singer);
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

	private void prepare(final int songIndex) 
	{
		if (getActiveList().getSongList().size() > songIndex) {

			final Song song = getActiveList().getSongList().get(songIndex);
			this.playingSongIndex = songIndex;
			try {
				/*
				 * below "if" is for songs that doesnt have mp3 url.
				 * It skips that song to next one
				 */
				if (song == null) {
					if (song.mp3Url == null) {
						Toast.makeText(getApplicationContext(), 
								getString(R.string.problem_in_song), Toast.LENGTH_LONG).show();
						int nextSong = songIndex + 1;
						prepare(nextSong);
						return;
					}
				}
				File f = new File(song.fileFullPath);
				mediaPlayerIsActive = true;
				if (f.exists()) {
					prepareMediaPlayer(songIndex, song);
				}
				else {
					
					Intent i = new Intent(PlayerService.PLAYING_SONG_CHANGED);
					i.putExtra(SONG_INDEX, songIndex);
					i.putExtra(LIST_INDEX, activeMusicList);
					i.putExtra(SONG_NAME , song.name);
					i.putExtra(SINGER_NAME, song.singer);
					sendBroadcast(i);

					sendBroadcast(new Intent(PlayerService.DOWNLOAD_STARTED));
					
					downloadThread = new Thread() {

						public void run() {
							getActiveList().downloadFile(song.mp3Url, song.fileFullPath);
							sendBroadcast(new Intent(PlayerService.DOWNLOAD_FINISHED));
							if (playingSongIndex != -1) {
								prepareMediaPlayer(songIndex, song);
							}

						};
					};
					downloadThread.start();
				}

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalStateException e) {
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
