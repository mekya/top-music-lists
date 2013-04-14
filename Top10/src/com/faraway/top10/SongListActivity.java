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
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.bugsense.trace.BugSenseHandler;
import com.faraway.top10.fragments.SongListFragment;
import com.faraway.top10.types.Song;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SongListActivity extends SherlockFragmentActivity{

	protected int playingSongIndex = -1;
	private static PlayerService player;
	private MenuItem playMenuItem;
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
			if (player.isPlaying()) {
				Song song = player.getPlayingSong();
				int index = player.getPlayingSongIndex();
				index++;
				getSupportActionBar().setSubtitle(index + "." + song.name + "-" + song.singer);
			}
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
				player.stop();
			}
			else {
				int listIndex = intent.getExtras().getInt(PlayerService.LIST_INDEX);
				if (listIndex != -1) {
					int songIndex = intent.getExtras().getInt(PlayerService.SONG_INDEX);
					if (songIndex == -1){
						playMenuItem.setIcon(android.R.drawable.ic_media_play);
						getSupportActionBar().setTitle(getString(R.string.app_name));
						getSupportActionBar().setSubtitle(null);

					}
					else {
						playMenuItem.setIcon(android.R.drawable.ic_media_pause);
						String songName = intent.getExtras().getString(PlayerService.SONG_NAME);
						String singer = intent.getExtras().getString(PlayerService.SINGER_NAME);
						String listName = intent.getExtras().getString(PlayerService.LIST_NAME);
						songIndex++;
						getSupportActionBar().setTitle(getString(R.string.app_name) + "-" + listName);
						getSupportActionBar().setSubtitle(songIndex + "." + songName + "-" + singer);

					}

				}
			}
		};
	};



	public void onCreate(Bundle savedInstanceState) {
		setTheme(com.actionbarsherlock.R.style.Sherlock___Theme_DarkActionBar);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		BugSenseHandler.initAndStartSession(this, "ad01e76a");
		setContentView(R.layout.viewpager);
		setSupportProgressBarIndeterminateVisibility(false);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

		

		IntentFilter filter = new IntentFilter();
		filter.addAction(PlayerService.PLAYING_SONG_CHANGED);
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.addAction(PlayerService.DOWNLOAD_STARTED);
		filter.addAction(PlayerService.DOWNLOAD_FINISHED);
		registerReceiver(songStatusReceiver, filter);
		
		
		ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		
		if (manager.getActiveNetworkInfo().isConnected() == false){
			Toast.makeText(this, getString(R.string.connectNetwork), Toast.LENGTH_SHORT).show();
			
		}
		
	}
	
	@Override
	protected void onStart() {
		Intent intent = new Intent(this, PlayerService.class);
		startService(intent);
		bindService(intent, serviceConnection, Context.BIND_IMPORTANT);
		super.onStart();
	}


	public PullToRefreshListView getSongListView(int index) {
		return ((SongListFragment)viewPagerAdapter.getItem(index)).getListView();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(songStatusReceiver);
		unbindService(serviceConnection);
		if (player.isPlaying() == false) {
			stopService(new Intent(this, PlayerService.class));
		}
		super.onDestroy();
	}

	private void stopPlayList() {
		new Thread(){
			@Override
			public void run() {
				player.stop();
			}}.start();

			PullToRefreshListView lv = getSongListView(player.getActiveListIndex());
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
		else if (list.get(0).mp4Url != null) {
			startActivity(new Intent(Intent.ACTION_VIEW, 
					Uri.parse(list.get(0).mp4Url)));
		}
		else {
			player.play(viewPager.getCurrentItem(), 0);					
		}
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
				getSupportActionBar().setSubtitle(null);
				getSupportActionBar().setTitle(getString(R.string.app_name));
			}
			else {
				playList();
				item.setIcon(android.R.drawable.ic_media_pause);
			}
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

	public PlayerService getPlayer(){
		return player;
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


}
