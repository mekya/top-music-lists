package com.faraway.top10.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.faraway.top10.PlayerService;
import com.faraway.top10.R;
import com.faraway.top10.types.Song;

public class SongAdapter extends ArrayAdapter<Song> {

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
		
		convertView = LayoutInflater.from(getContext()).inflate(R.layout.list, null);
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
