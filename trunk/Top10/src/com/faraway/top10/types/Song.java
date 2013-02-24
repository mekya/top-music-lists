package com.faraway.top10.types;

import java.io.Serializable;

import com.handmark.pulltorefresh.library.R.string;

public class Song implements Serializable{
	public String name;
	public String singer;
	public String mp3Url;
	public String mp4Url;
      
    public String fileFullPath;
	public String youtubeURL;
	
	@Override
	public boolean equals(Object o) {
		
		if(o != null && o instanceof Song)
		{
			Song val = (Song)o;
			if(this.toString().equals(val.toString()))
				return true;
		}
		 
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.name + this.singer ;
	}
	
	
	
	
	
}
