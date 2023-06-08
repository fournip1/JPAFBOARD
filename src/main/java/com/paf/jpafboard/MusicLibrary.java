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
import java.util.List;
import java.util.Map;
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

    public boolean addTrack(Track newTrack) {
            if (!tracks.contains(newTrack)) {
                tracks.add(newTrack);
                return true;
            } else {
                return false;
            }
        }

    public ArrayList<Genre> getArrayGenres() {
        return genres.stream()
                .sorted(Comparator.comparing(Genre::getLabel))
                .collect(toCollection(ArrayList::new));
    }

    public ArrayList<Track> getArrayTracks() {
        ArrayList<Track> aTracks = tracks.stream()
                .sorted(Comparator.comparing(Track::getTitle))
                .collect(toCollection(ArrayList::new));
        aTracks.forEach((t) -> {
            t.setGenresString();
        });
        return aTracks;
    }

    public ArrayList<Track> getEmptyGenreArrayTracks() {
        ArrayList<Track> aTracks = tracks.stream()
                .filter(track -> track.getGenres().isEmpty())
                .sorted(Comparator.comparing(Track::getTitle))
                .collect(toCollection(ArrayList::new));
        aTracks.forEach((t) -> {
            t.setGenresString();
        });
        return aTracks;
    }

    public HashSet<Track> getSetTracks() {
        return new HashSet<>(tracks);
    }

    public HashSet<Genre> getSetGenres() {
        return new HashSet<>(genres);
    }

    public void cleanGenres() {
        genres.forEach((g) -> {
            if (g.getTracks().isEmpty()) {
                genres.remove(g);
                System.out.println("removed: " + g.getLabel());
            }
        });
    }

    @Override
    public String toString() {
        return directory;
    }

    /**
     * **************************************************************************
     * Methods to handle genre strings.                                         *
     * *************************************************************************
     */    
    
    public ArrayList<String> convertGenresString(String genresString) {
        String[] genresDraftList = genresString.split(",");
        ArrayList<String> genresFinalList = new ArrayList<>();
        for (String g:genresDraftList) {
            if (!g.trim().isEmpty())
                genresFinalList.add(g.trim().substring(0, 1).toUpperCase() + g.trim().substring(1).toLowerCase());
        }
        return genresFinalList;
    }
}
