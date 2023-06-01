/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.paf.jpafboard;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import static java.util.stream.Collectors.toCollection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author alcheikh
 */
public class GenreController implements Initializable {

    private Genre genre;
    private MusicLibrary library;

    private final static String DATABASE_URL = App.getURL();
    private ConnectionSource connectionSource;
    private Dao<Track, String> trackDao;
    private Dao<MusicLibrary, String> musiclibraryDao;
    private Dao<Genre, String> genreDao;
    private ArrayList<String> genresLabels;

    @FXML
    private AnchorPane genrePane;
    @FXML
    private TextField genreText;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            initializeConnexion();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeConnexion() throws Exception {
        connectionSource = null;
        // create our data-source for the database
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);
            // setup our database and DAOs
            musiclibraryDao = DaoManager.createDao(connectionSource, MusicLibrary.class);
            genreDao = DaoManager.createDao(connectionSource, Genre.class);
            trackDao = DaoManager.createDao(connectionSource, Track.class);
        } finally {
            // destroy the data source which should close underlying connections
            if (connectionSource != null) {
                connectionSource.close();
                connectionSource = null;
            }
        }
    }

    public void initGenre(Genre editedGenre) throws Exception {
        genre = editedGenre;
        library = editedGenre.getLibrary();
        musiclibraryDao.refresh(library);
        genreText.setText(genre.getLabel());
        genresLabels = library.getGenres().stream()
                .map(Genre::getLabel)
                .collect(toCollection(ArrayList::new));
    }

    public void saveGenre() throws Exception {
        String newLabel = genreText.getText();
        if (!newLabel.equals(genre.getLabel())) {
            // we first select the tracks to be modified
            ArrayList<Track> mTracks = genre.getArrayTracks();
            // this to update mp3 tags
            MP3Util mp3Util;
            Map dataMap = new HashMap<String, String>();
            // initializing the map
            dataMap.put("Title", "");
            dataMap.put("Artist", "");
            dataMap.put("Genres", "");

            // We check wether the genre already exists
            if (!genresLabels.contains(newLabel)) {
                // then we remove the TrackGenre 
                genre.getTracks().removeAll(genre.getTracks());
                genreDao.updateId(genre, newLabel);
                for (Track t : mTracks) {
                    // setting new data
                    t.addGenre(genre);
                    t.setGenresString();
                    dataMap.replace("Title", t.getTitle());
                    dataMap.replace("Artist", t.getArtist());
                    dataMap.replace("Genres", t.getGenresString());
                    mp3Util = new MP3Util(t.getPath());
                    if (mp3Util.isValid()) {
                        mp3Util.setMp3Tags(dataMap);
                    } else {
                        Alert alert = new Alert(AlertType.WARNING, t.getPath() + " has been corrupted or removed.");
                        alert.show();
                    }
                }
            } // if genre already exists, we propose to merge.
            else {
                Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "This keyword already exists. Merge anyway?");
                confirmation.showAndWait()
                        .filter(r -> r.equals(ButtonType.CANCEL))
                        .ifPresent(r -> {
                            return;
                        });
                // then we remove the TrackGenre 
                genre.getTracks().removeAll(genre.getTracks());
                // we remove the genre
                library.getGenres().remove(genre);
                Genre keptGenre = genreDao.queryForId(newLabel);
                // and we assign the new genre to each modified track
                for (Track t : mTracks) {
                    // setting new data
                    t.addGenre(keptGenre);
                    t.setGenresString();
                    dataMap.replace("Title", t.getTitle());
                    dataMap.replace("Artist", t.getArtist());
                    dataMap.replace("Genres", t.getGenresString());
                    mp3Util = new MP3Util(t.getPath());
                    if (mp3Util.isValid()) {
                        mp3Util.setMp3Tags(dataMap);
                    } else {
                    Alert alert = new Alert(AlertType.WARNING, t.getPath() + " has been corrupted or removed.");
                    alert.show();
                    }
                }
            }
        }
        // to refresh I use the App instance.
        App.initializeConnexion();
        cancelEdition();
    }

    public void cancelEdition() {
        Stage stage = (Stage) genrePane.getScene().getWindow();
        stage.close();
    }
}
