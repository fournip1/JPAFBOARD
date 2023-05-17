/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paf.jpafboard;


import java.io.IOException;
import java.io.RandomAccessFile;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author lemerle
 */
public class MP3Utils {
    private String mp3Path;
    final Mp3File mp3File;

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public MP3Utils(String mp3Path) throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException {
        this.mp3Path = mp3Path;
        this.mp3File = new Mp3File(mp3Path);
    }
    
    public String getMp3Path() {
        return mp3Path;
    }

    public Map<String,String> getMp3Tags() {
    // public void getMp3Tags() {
        Map dataMap = new HashMap<String,String> ();
        dataMap.put("Title", "");
        dataMap.put("Artist", "");
        dataMap.put("Genres", "");
        if (mp3File.hasId3v1Tag()) {
        	ID3v1 id3v1Tag = mp3File.getId3v1Tag();
                if (id3v1Tag.getTitle() != null) {
                    dataMap.replace("Title", id3v1Tag.getTitle());
                }
                if (id3v1Tag.getArtist() != null) {
                    dataMap.replace("Artist", id3v1Tag.getArtist());
                }
                if (id3v1Tag.getGenreDescription() != null) {
                    dataMap.replace("Genres", id3v1Tag.getGenreDescription());
                }
        }     
        else if (mp3File.hasId3v2Tag()) {
        	ID3v2 id3v2Tag = mp3File.getId3v2Tag();
                if (id3v2Tag.getTitle() != null) {
                    dataMap.replace("Title", id3v2Tag.getTitle());
                }
                if (id3v2Tag.getArtist() != null) {
                    dataMap.replace("Artist", id3v2Tag.getArtist());
                }
                if (id3v2Tag.getGenreDescription() != null) {
                    dataMap.replace("Genres", id3v2Tag.getGenreDescription());
                }
//        	System.out.println("Track: " + id3v2Tag.getTrack());
//        	System.out.println("Artist: " + id3v2Tag.getArtist());
//        	System.out.println("Title: " + id3v2Tag.getTitle());
//        	System.out.println("Album: " + id3v2Tag.getAlbum());
//        	System.out.println("Year: " + id3v2Tag.getYear());
//        	System.out.println("Genre: " + id3v2Tag.getGenre() + " (" + id3v2Tag.getGenreDescription() + ")");        	
//                System.out.println("Comment: " + id3v2Tag.getComment());
        }
        else {
            System.out.println("No tags found");
        }                
        return dataMap;
    }
}
