package com.faraway.top10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;

public class SongListActivity extends SherlockFragmentActivity{

	protected int playingSongIndex = -1;
	private ArrayList<Song> songList;
	private PlayerService player;
	private MenuItem refreshMenuItem;
	private MenuItem playMenuItem;
	private ArrayList<AbstractMusicList> musicList;
	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;

	/**
	 * Service connection
	 */
	private ServiceConnection serviceConnection = new ServiceConnection() {

		public void onServiceDisconnected(ComponentName name) {
			player = null;
		}

		public void onServiceConnected(ComponentName name, IBinder service) {
			player = (PlayerService)((PlayerService.PSBinder)service).getService();
			viewPager.setAdapter(viewPagerAdapter);
		}
	};

	BroadcastReceiver songStatusReceiver = new BroadcastReceiver(){

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(PlayerService.DOWNLOAD_STARTED))
			{
				setSupportProgressBarIndeterminateVisibility(true);
			}
			else if (action.equals(PlayerService.DOWNLOAD_FINISHED))
			{
				setSupportProgressBarIndeterminateVisibility(false);
			}	
			else if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED))
			{
				// stop the player if phone state has changed.
				player.stop();
			}
			else {
				int listIndex = intent.getExtras().getInt(PlayerService.LIST_INDEX);
				if (listIndex != -1) {

					ListView lv = getSongListView(listIndex);
					int childCount = lv.getChildCount();
					if (intent.getExtras().getInt(PlayerService.SONG_INDEX) == -1)
					{
						playMenuItem.setIcon(android.R.drawable.ic_media_play);
						for (int i = 0; i < childCount; i++) {
							lv.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.list_background));
						}
					}
					else {
						playMenuItem.setIcon(android.R.drawable.ic_media_pause);
						for (int i = 0; i < childCount; i++) {
							TextView songTextView = (TextView)lv.getChildAt(i).findViewById(R.id.song);
							String songName = intent.getExtras().getString(PlayerService.SONG_NAME);
							if (songName.equals(songTextView.getText().toString())) {
								lv.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.active_item_background));
							}
							else {
								lv.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.list_background));
							}
						}
					}

				}
			}
		};
	};



	public void onCreate(Bundle savedInstanceState) {
		setTheme(com.actionbarsherlock.R.style.Sherlock___Theme_DarkActionBar);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.viewpager);
		setSupportProgressBarIndeterminateVisibility(false);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		Intent intent = new Intent(this, PlayerService.class);
		startService(intent);
		bindService(intent, serviceConnection, Context.BIND_IMPORTANT);

		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayerService.PLAYING_SONG_CHANGED);
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.addAction(PlayerService.DOWNLOAD_STARTED);
		filter.addAction(PlayerService.DOWNLOAD_FINISHED);
		registerReceiver(songStatusReceiver, filter);
	}



	public ListView getSongListView(int index) {
		return ((SongListFragment)viewPagerAdapter.getItem(index)).getListView();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(songStatusReceiver);
		unbindService(serviceConnection);
		super.onDestroy();
	}

	private void stopPlayList() {
		new Thread(){
			@Override
			public void run() {
				player.stop();
			}}.start();

			ListView lv = getSongListView(player.getActiveListIndex());
			if (lv != null) {
				int childCount = lv.getChildCount();
				for (int i = 0; i < childCount; i++) {
					lv.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.list_background));			
				}
			}
	}

	private void playList() {
		int index = viewPager.getCurrentItem();
		ArrayList<Song> list = player.getSongList(index);
		String youtubeURL = list.get(0).youtubeURL;
		if (youtubeURL != null) {
			startActivity(new Intent(Intent.ACTION_VIEW, 
		              Uri.parse(youtubeURL)));
		}
		else {
			player.play(viewPager.getCurrentItem(), 0);					
		}
	}

	private void updateSongList(final int listIndex){
		if (songList != null && songList.size() >= 0) {
			final Song[] songs = new Song[songList.size()];
			for (int i = 0; i < songList.size(); i++) {
				songs[i]  = songList.get(i);
			}

			runOnUiThread(new Runnable() {
				public void run() {
					getSongListView(listIndex).setAdapter(new SongAdapter(getApplicationContext(), listIndex, songs));
					getSongListView(listIndex).setVisibility(View.VISIBLE);
					setSupportProgressBarIndeterminateVisibility(false);
					refreshMenuItem.setEnabled(true);					
				}
			});
		}
		else {
			runOnUiThread(new Runnable() {				
				public void run() {
					Toast.makeText(SongListActivity.this, getString(R.string.unable_to_get_list), Toast.LENGTH_LONG).show();
				}
			});
		}		
	}

	public void refreshList() 
	{
		new Thread(){
			public void run() {
				int currentIndex = viewPager.getCurrentItem();
				songList = player.refreshSongList(currentIndex);
				updateSongList(currentIndex);
			}
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		playMenuItem = menu.add(getString(R.string.play));
		if (player != null && player.isPlaying()) {
			playMenuItem.setIcon(android.R.drawable.ic_media_pause);
		}
		else {
			playMenuItem.setIcon(android.R.drawable.ic_media_play);
		}

		playMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		refreshMenuItem = menu.add(getString(R.string.refresh));
		refreshMenuItem.setIcon(android.R.drawable.ic_popup_sync);
		refreshMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		menu.add(getString(R.string.about))
		.setIcon(android.R.drawable.ic_dialog_info)
		.setTitle(getString(R.string.about))
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle().toString();
		if (title.equals(getString(R.string.play))) {
			if (player.isPlaying()) {
				stopPlayList();
				setSupportProgressBarIndeterminateVisibility(false);
				item.setIcon(android.R.drawable.ic_media_play);
			}
			else {
				playList();
				item.setIcon(android.R.drawable.ic_media_pause);
			}
		}
		else if (title.equals(getString(R.string.refresh))) {
			setSupportProgressBarIndeterminateVisibility(true);
			refreshList();
			item.setEnabled(false);
		}
		else if (title.equals(getString(R.string.about))) {
			LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
			final AlertDialog alerDialog = new AlertDialog.Builder(this)
			.setCancelable(true)
			.setView(li.inflate(R.layout.info, null))
			.setIcon(R.drawable.icon)
			.setTitle(R.string.app_name)
			.setNeutralButton(R.string.ok, new OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			}).create();
			alerDialog.show();
		}

		return super.onOptionsItemSelected(item);
	}


	public class ViewPagerAdapter extends FragmentStatePagerAdapter 
	{
		Map<Integer, SongListFragment> fragmentMap = new HashMap<Integer, SongListFragment>();

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}



		@Override
		public Fragment getItem(int index) 
		{
			if (fragmentMap.containsKey(index) == false) {
				fragmentMap.put(index, new SongListFragment(index));
			}
			return fragmentMap.get(index);
		}

		@Override
		public int getCount() {
			return player.getMusicLists().size();
		}	

		@Override
		public CharSequence getPageTitle(int position) {
			return player.getMusicLists().get(position).getMusicListName();
		}
	}

	public class SongListFragment extends Fragment {
		int listIndex;
		private ListView lv;
		private ProgressBar loadingBar;
		private ArrayList<Song> fragmentSongList;

		public SongListFragment(int index) {
			listIndex = index;
		}

		public ListView getListView(){
			return lv;
		}

		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.main, container, false);
			lv = (ListView)v.findViewById(R.id.songList);
			lv.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
					Song song = fragmentSongList.get(position);
					if (song.youtubeURL != null){
						startActivity(new Intent(Intent.ACTION_VIEW, 
					              Uri.parse(song.youtubeURL)));
					}
					else {
						player.play(listIndex, position);					
					}
				}
			});
			loadingBar = (ProgressBar) v.findViewById(R.id.loading);
			return v;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			new Thread(){
				public void run() {
					fragmentSongList = player.getSongList(listIndex);

					if (fragmentSongList != null && fragmentSongList.size() >= 0) {
						final Song[] songs = new Song[fragmentSongList.size()];
						for (int i = 0; i < fragmentSongList.size(); i++) {
							songs[i]  = fragmentSongList.get(i);
						}

						runOnUiThread(new Runnable() {
							public void run() {
								lv.setAdapter(new SongAdapter(getApplicationContext(), listIndex, songs));
								loadingBar.setVisibility(View.GONE);
								lv.setVisibility(View.VISIBLE);
								setSupportProgressBarIndeterminateVisibility(false);
								refreshMenuItem.setEnabled(true);					
							}
						});
					}
					else {
						runOnUiThread(new Runnable() {				
							public void run() {
								Toast.makeText(SongListActivity.this, getString(R.string.unable_to_get_list), Toast.LENGTH_LONG).show();
							}
						});
					}
				};
			}.start();
		}
	}

	private class SongAdapter extends ArrayAdapter<Song> {

		private Song[] songs;
		private int listIndex;

		public SongAdapter(Context context, int listIndex, Song[] objects) {
			super(context, R.layout.list, objects);
			this.songs = objects;		
			this.listIndex = listIndex;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//to increase performance view holder can be used.
			convertView = SongListActivity.this.getLayoutInflater().inflate(R.layout.list, null);
			if (player != null 
					&& player.isPlaying() == true 
					&& listIndex == player.getActiveListIndex()
					&& position == player.getPlayingSongIndex()) 
			{
				convertView.setBackgroundColor(getResources().getColor(R.color.active_item_background));
			}
			TextView singer = (TextView)convertView.findViewById(R.id.singer);
			singer.setText(songs[position].singer);
			TextView song = (TextView) convertView.findViewById(R.id.song);
			song.setText(songs[position].name);
			position++;
			TextView order_num = (TextView)convertView.findViewById(R.id.order_num);
			order_num.setText(String.valueOf(position));
			return convertView;
		}
	}
}
