/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paf.jpafboard;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import static java.util.stream.Collectors.toCollection;
// import javafx.embed.swing.JFXPanel;

/**
 *
 * @author lemerle
 */

@DatabaseTable(tableName = "musiclibrary")
public class MusicLibrary {
    
        private final static String DIR_FIELD_NAME = "directory";

	@DatabaseField(columnName = DIR_FIELD_NAME, id = true, canBeNull = false)
	private String directory;
        
	@ForeignCollectionField
	private ForeignCollection<Track> tracks;

        @ForeignCollectionField
	private ForeignCollection<Genre> genres;
        
        MusicLibrary() {
		// all persisted classes must define a no-arg constructor with at least package visibility
	}
        
        
        public MusicLibrary(String directory) {
            this.directory = directory;
        }

        public String getDirectory() {
		return directory;
	}        

        public void setDirectory(String directory) {
		this.directory = directory;
	}        

	public ForeignCollection<Track> getTracks() {
		return tracks;
	}        

	public ForeignCollection<Genre> getGenres() {
		return genres;
	}
        
        public void addGenre(Genre genre) {
            if (!genres.contains(genre)) {
                genres.add(genre);
            }
        }
        
        public void addTrack(Track track) {
            if (!tracks.contains(track)) {
                tracks.add(track);
            }
        }
        
        public ArrayList<Genre> getArrayGenres() {
            return genres.stream()
                         .sorted(Comparator.comparing(Genre::getLabel))
                         .collect(toCollection(ArrayList::new));
        }

        public ArrayList<Track> getArrayTracks() {
            return tracks.stream()
                          .sorted(Comparator.comparing(Track::getTitle))
                          .collect(toCollection(ArrayList::new));
        }
        
        public HashSet<Track> getSetTracks()  {
            return new HashSet<>(tracks);
        }

        public HashSet<Genre> getSetGenres()  {
            return new HashSet<>(genres);
        }
        
    @Override
    public String toString() {
        return directory;
    }


}
