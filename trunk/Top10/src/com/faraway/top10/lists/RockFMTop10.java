package com.faraway.top10.lists;

import java.util.ArrayList;
import java.util.Locale;

import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.faraway.top10.types.AbstractMusicList;
import com.faraway.top10.types.Song;


public class RockFMTop10 extends AbstractMusicList  {

        private static final String CACHE_FILE = "RockFMTop10List45";
        private static final String LIST_NAME = "RockFM";
        private static final String URL = "http://www.rockfm.com.tr/Top20.aspx";

        public RockFMTop10(Context context) {
                super(context);
        }
        
        @Override
        public String getCacheFileName() {
                return CACHE_FILE;
        }



        @Override
        public String getMusicListName() {
                return LIST_NAME;
        }

        
        
        public String replaceTurkishCharacters(String html )
    	{	/*	
    		String [] chars = { "Ä±","Å?","Ã¼","Ã§","Ã¶","Ä?","ÅŸ","Ã‡","Ä°","ÄŸ","Åž","Ã–","Ãœ","Ä±","Å?","Ã§","Ã¶","Ä?","ÅŸ","Ã‡","Ä°","ÄŸ","Åž","Ã–","Ãœ","Ã¼","ÄŸ"}; 
    		String [] trchars = {"ı","ş", "ü", "ç", "ö", "ğ", "ş", "Ç", "i", "ğ", "Ş", "Ö", "Ü", "ı", "ş", "ç", "ö", "ğ", "ş", "Ç", "i", "ğ", "Ş", "Ö", "Ü", "ü", "ğ"}; 
    		
    		for( int i = 0; i<27 ;i++)
    		{
    			html = html.replaceAll( chars[i] , trchars[i] );
    		}
    		*/
    		
        	html = html.replaceAll("Ä°","İ");
			html = html.replaceAll("Ä±","ı");
			html = html.replaceAll("Ãœ","Ü");
			html = html.replaceAll("Ã¼","ü");
			html = html.replaceAll("Åž","Ş");
			html = html.replaceAll("Å","ş");
			html = html.replaceAll("Ã‡","Ç");
			html = html.replaceAll("Ã§","ç");
			html = html.replaceAll("Äž","Ğ");
			html = html.replaceAll("Ä","ğ");
			html = html.replaceAll("Ã–","Ö");
			html = html.replaceAll("Ã¶","ö");
    		
    		return html;
    	}
        
        
        
        @Override
        protected ArrayList<Song> parse(String content) {
                
                ArrayList<Song> songList = new ArrayList<Song>();
                content = replaceTurkishCharacters(content);
                Document doc = Jsoup.parse(content);

                
                Element listElements = doc.getElementsByClass("box20").get(0);

                Elements mp3List = listElements.getElementsByTag("a");

                //String mp3html= mp3List.html();
                
                //String encoded = URLEncoder.encode(mp3html, "UTF-8");
                
                
                for (int i = 0; i < 10; i++) {
                        
                        Song song = new Song();
                        Elements esinger = mp3List.get(i).getElementsByClass("h3");
                        String strsinger = esinger.html();
                        
                        Elements ename = mp3List.get(i).getElementsByTag("strong");
                        String strname = ename.html();
                        
                        
                        song.singer = getCapitilize(StringEscapeUtils.unescapeHtml(strsinger.substring(strsinger.indexOf(">")+1, strsinger.indexOf("</span>"))), new java.util.Locale("TR_tr"));
                        song.name =   getCapitilize(StringEscapeUtils.unescapeHtml(strname.substring(strsinger.indexOf(">")-1, strname.indexOf("</span>"))), new java.util.Locale("TR_tr"));
                        /*
                                song.singer = mp3List.get(i).getElementsByClass("h3").text();
                                song.singer = getCapitilize(song.singer, Locale.ENGLISH);
                                
                                song.name = mp3List.get(i).getElementsByTag("strong").text();
                                song.name = getCapitilize(song.name, Locale.ENGLISH);
                        
                        */
                        
                                String mp3Url = mp3List.get(i).attr("href");
                                
                                if (mp3Url != null) {
                                        song.youtubeURL = mp3Url;
                                }
                                
                                song.fileFullPath = null;
                                songList.add(song);                     

                }
                
                return songList;        
                }
        

        @Override
        public String getURL() {
                return URL;
        }

        
        
        
}