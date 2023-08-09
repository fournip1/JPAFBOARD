/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.paf.jpafboard;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.ForeignCollectionField;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Objects;
import static java.util.stream.Collectors.toCollection;

/**
 * A given Track object contains the infos related to a given track (artist,
 * name...) and has a many2many relation with Genre objects. Warning! In the
 * pafboard Genre means "keyword". Hence, the music library should be configured
 * so that the usual genre tag contains a coma separated value list of genre.
 */
@DatabaseTable(tableName = "tracks")
public class Track {
    // for QueryBuilder to be able to find the fields

    public static final String PATH_FIELD_NAME = "path";
    public static final String TITLE_FIELD_NAME = "title";
    public static final String ARTIST_FIELD_NAME = "artist";
    public static final String LIBRARY_ID_FIELD_NAME = "directory";

    @DatabaseField(columnName = PATH_FIELD_NAME, id = true, canBeNull = false)
    private String path;

    @DatabaseField(columnName = TITLE_FIELD_NAME, canBeNull = true)
    private String title;

    @DatabaseField(columnName = ARTIST_FIELD_NAME, canBeNull = true)
    private String artist;

    @DatabaseField(foreign = true, foreignAutoRefresh = false, columnName = LIBRARY_ID_FIELD_NAME)
    MusicLibrary library;

    @ForeignCollectionField
    private ForeignCollection<TrackGenre> trackgenres;
    
    private String genresString;
    
    private String keywordString;
    

    Track() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

    public Track(String path, String name, String artist, MusicLibrary library) {
        this.path = path;
        this.title = name;
        this.artist = artist;
        this.library = library;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public ForeignCollection<TrackGenre> getGenres() {
        return trackgenres;
    }

    public void addGenre(Genre genre) {
        TrackGenre newTrackGenre = new TrackGenre(this, genre);
        if (!trackgenres.contains(newTrackGenre)) {
            trackgenres.add(newTrackGenre);
        }
    }

    public ArrayList<Genre> getArrayGenres() {
        return trackgenres.stream()
                .map(t -> t.getGenre())
                .collect(toCollection(ArrayList::new));
    }
    

    public String getKeywordString() {        
        return keywordString;
    }

    public void setGenresAndKeywordsString() {
        String genresStr;
        StringBuilder str = new StringBuilder();
        if (!getArrayGenres().isEmpty()) {
            getArrayGenres().forEach((g) -> {
                str.append(g.getLabel());
                str.append(", ");
            });
            str.deleteCharAt(str.length() - 2);
        }
        genresStr = str.toString();
        if (!genresStr.equals("")) {
            this.genresString = genresStr.substring(0,genresStr.length()-1);
        } else {
            this.genresString = genresStr;
        }
        
        StringBuilder keywordStr = new StringBuilder(" " + artist + " " + title);
        getArrayGenres().forEach((g) -> {
            keywordStr.append(" ");
            keywordStr.append(g.getLabel());
        });
        this.keywordString = Normalizer.normalize(keywordStr.toString(),Normalizer.Form.NFKD).replaceAll("\\p{M}", "").toLowerCase().replaceAll("\\p{M}", "");
    }
    
    public String getGenresString() {
        return genresString;
    }

    public MusicLibrary getLibrary() {
        return library;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.path);
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
        final Track other = (Track) obj;
        return Objects.equals(this.path, other.path);
    }

    @Override
    public String toString() {
        return title + " (" + getGenresString() + ")";
    }
}
