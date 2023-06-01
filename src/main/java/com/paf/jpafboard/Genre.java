/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paf.jpafboard;

import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.dao.ForeignCollection;
import java.util.ArrayList;
import java.util.Objects;
import static java.util.stream.Collectors.toCollection;

/**
 *
 * @author lemerle
 */
@DatabaseTable(tableName = "genres")
public class Genre {

    public static final String LABEL_FIELD_NAME = "label";
    public final static String LIBRARY_ID_FIELD_NAME = "directory";

    @DatabaseField(columnName = LABEL_FIELD_NAME, id = true, canBeNull = false)
    private String label;

    @DatabaseField(foreign = true, foreignAutoRefresh = false, columnName = LIBRARY_ID_FIELD_NAME)
    MusicLibrary library;

    @ForeignCollectionField
    private ForeignCollection<TrackGenre> genretracks;

    Genre() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Genre(String label, MusicLibrary library) {
        this.label = label;
        this.library = library;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ForeignCollection<TrackGenre> getTracks() {
        return genretracks;
    }

    public ArrayList<Track> getArrayTracks() {
        ArrayList<Track> aTracks = genretracks.stream()
                .map(t -> t.getTrack())
                .collect(toCollection(ArrayList::new));
        aTracks.forEach((t) -> {
            t.setGenresString();
        });
        return aTracks;
    }

    public MusicLibrary getLibrary() {
        return library;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.label);
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
        final Genre other = (Genre) obj;
        return Objects.equals(this.label, other.label);
    }

    @Override
    public String toString() {
        return label;
    }
}
