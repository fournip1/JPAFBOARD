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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author alcheikh
 */
public class TrackController implements Initializable {

    private MP3Util mp3Util;
    private Track track;
    private MusicLibrary library;

    private final static String DATABASE_URL = App.getURL();
    private ConnectionSource connectionSource;
    private Dao<Track, String> trackDao;
    private Dao<MusicLibrary, String> musiclibraryDao;
    private Dao<Genre, String> genreDao;

    private Map<String, String> item = new HashMap<>();

    @FXML
    private AnchorPane trackPane;
    @FXML
    private TableView<Track> trackTable;
    @FXML
    private TableColumn trackTitle, trackArtist, trackKeyWords;
    @FXML
    private Label keywordsLabel;

    private final ObservableList<Track> items = FXCollections.<Track>observableArrayList();
    private final Map dataMap = new HashMap<String, String>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        trackTable.getSelectionModel().cellSelectionEnabledProperty().set(true);
        trackTitle.setOnEditCommit(
                new EventHandler<CellEditEvent<Track, String>>() {
            @Override
            public void handle(CellEditEvent<Track, String> t) {
                dataMap.replace("Title", t.getNewValue());
            }
        }
        );
        trackArtist.setOnEditCommit(
                new EventHandler<CellEditEvent<Track, String>>() {
            @Override
            public void handle(CellEditEvent<Track, String> t) {
                dataMap.replace("Artist", t.getNewValue());
            }
        }
        );

        trackKeyWords.setOnEditCommit(
                new EventHandler<CellEditEvent<Track, String>>() {
            @Override
            public void handle(CellEditEvent<Track, String> t) {
                dataMap.replace("Genres", t.getNewValue());
                refreshKeyWordsLabel(t.getNewValue());
            }
        }
        );
        
        try {
            initializeConnexion();
        } catch(Exception e) {
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

    public void initTrack(Track editedTrack) {
        track = editedTrack;
        library = editedTrack.getLibrary();
        dataMap.put("Title", track.getTitle());
        dataMap.put("Artist", track.getArtist());
        dataMap.put("Genres", track.getGenresString());
        items.add(editedTrack);
        trackTable.getItems().addAll(items);
    }

    public void saveTrack() throws Exception {
        ArrayList<String> listGenres = library.convertGenresString(dataMap.get("Genres").toString());
        try {
            mp3Util = new MP3Util(track.getPath());
            if (mp3Util.isValid()) {
                track.setTitle(dataMap.get("Title").toString());
                track.setArtist(dataMap.get("Artist").toString());
                track.getGenres().removeAll(track.getGenres());
                for (String g : listGenres) {
                    Genre newGenre = new Genre(g, library);
                    library.addGenre(newGenre);
                    track.addGenre(newGenre);
                }
                track.setGenresAndKeywordsString();                
                trackDao.update(track);
                mp3Util.setMp3Tags(dataMap);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, track.getPath() + " has been corrupted or removed.");
                alert.show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // to refresh I use the App instance.
        library.cleanGenres();
        App.initializeButtonsAndLists();
        cancelEdition();
    }

    public void cancelEdition() {
        Stage stage = (Stage) trackPane.getScene().getWindow();
        stage.close();
    }

    public void refreshKeyWordsLabel(String stringKeyWords) {
        StringBuilder str = new StringBuilder();
        ArrayList<String> listGenres = library.convertGenresString(stringKeyWords);
        for (String g : listGenres) {
            str.append("<" + g + ">");
        }
        keywordsLabel.setText(str.toString());
    }
}
