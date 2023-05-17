/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paf.jpafboard;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Objects;

/**
 *
 * @author lemerle
 * This class contains the many2many between the tracks and the genres
 */

@DatabaseTable(tableName = "tracksgenres")
public class TrackGenre {
	public final static String TRACK_ID_FIELD_NAME = "path";
	public final static String GENRE_ID_FIELD_NAME = "genre";    
        
//       Actually ormlite does not allow to have a set of fields as id. Therefore, I have to generate an id, which is not meaningfull.
        @DatabaseField(generatedId = true)
        private int id;
        
        // This is a foreign object which just stores the id from the User object in this table.
	@DatabaseField(foreign = true,  foreignAutoRefresh = true, columnName = TRACK_ID_FIELD_NAME)
	Track track;

	// This is a foreign object which just stores the id from the Post object in this table.
	@DatabaseField(foreign = true,  foreignAutoRefresh = true, columnName = GENRE_ID_FIELD_NAME)
	Genre genre;

	TrackGenre() {
		// for ormlite
	}
        
	public TrackGenre(Track track, Genre genre) {
		this.track = track;
		this.genre = genre;
	}
        
        public int getId() {
		return id;
	}

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

        
        
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + Objects.hashCode(this.track);
        hash = 31 * hash + Objects.hashCode(this.genre);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TrackGenre other = (TrackGenre) obj;
        if (!Objects.equals(this.track, other.track)) {
            return false;
        }
        return Objects.equals(this.genre, other.genre);
    }        
}
