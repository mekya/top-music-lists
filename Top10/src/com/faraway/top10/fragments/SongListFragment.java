package com.faraway.top10.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.faraway.top10.PlayerService;
import com.faraway.top10.R;
import com.faraway.top10.SongListActivity;
import com.faraway.top10.adapters.SongAdapter;
import com.faraway.top10.types.Song;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SongListFragment extends Fragment {
	private static final String LIST_INDEX = "LIST_INDEX";
	private int listIndex = -1;
	private PullToRefreshListView lv;
	private ProgressBar loadingBar;
	private ArrayList<Song> fragmentSongList;
	private PlayerService player;
	private Thread songFetcher;

	public SongListFragment(){
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(LIST_INDEX, listIndex);
	}

	public SongListFragment(int index) {
		listIndex = index;
	}

	public PullToRefreshListView getListView(){
		return lv;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.main, container, false);
		lv = (PullToRefreshListView)v.findViewById(R.id.songList);



		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				Song song = fragmentSongList.get(position-1);
				if (song.youtubeURL != null){
					player.stop();
					startActivity(new Intent(Intent.ACTION_VIEW, 
							Uri.parse(song.youtubeURL)));
				}
				else if (song.mp4Url != null) {
					player.stop();
					startActivity(new Intent(Intent.ACTION_VIEW, 
							Uri.parse(song.mp4Url)));
				}
				else {
					player.play(listIndex, position-1);					
				}
			}
		});

		lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {

				new Thread() {
					public void run() {
						fragmentSongList = player.refreshSongList(listIndex);

						final Song[] songs = new Song[fragmentSongList.size()];
						for (int i = 0; i < fragmentSongList.size(); i++) {
							songs[i]  = fragmentSongList.get(i);
						}

						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								lv.setAdapter(new SongAdapter(getActivity(), listIndex, songs));
								lv.onRefreshComplete();
							}
						});

					};
				}.start();



			}
		});
		loadingBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null && this.listIndex == -1) {
			if (savedInstanceState.containsKey(LIST_INDEX)) {
				listIndex = savedInstanceState.getInt(LIST_INDEX);
			}
		}
		player = ((SongListActivity)getActivity()).getPlayer();
		if (player == null) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			player = ((SongListActivity)getActivity()).getPlayer();
		}

		if (songFetcher == null || songFetcher.isAlive() == false) {

			songFetcher = new Thread(){
				public void run() {

					if (player != null) {
						fragmentSongList = player.getSongList(listIndex);

						if (fragmentSongList != null && fragmentSongList.size() >= 0) {
							final Song[] songs = new Song[fragmentSongList.size()];
							for (int i = 0; i < fragmentSongList.size(); i++) {
								songs[i]  = fragmentSongList.get(i);
							}


							if (getActivity() != null) {
								getActivity().runOnUiThread(new Runnable() {
									public void run() {
										lv.setAdapter(new SongAdapter(getActivity(), listIndex, songs));
										loadingBar.setVisibility(View.GONE);
										lv.setVisibility(View.VISIBLE);
									}
								});
							}
						}
						else {
							if (getActivity()!= null) {
								getActivity().runOnUiThread(new Runnable() {				
									public void run() {
										Toast.makeText(getActivity(), getString(R.string.unable_to_get_list), Toast.LENGTH_LONG).show();
									}
								});
							}
						}
					}
				};
			};
			songFetcher.start();
		}
	}
}

