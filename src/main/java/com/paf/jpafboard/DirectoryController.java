/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.paf.jpafboard;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Comparator.comparing;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toCollection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author lemerle
 */
public class DirectoryController implements Initializable {

    // Most important declaration
    private MusicLibrary library;

    // for DB connection purpose
    // and modifying the music library
    private final static String DATABASE_URL = App.getURL();
    private final static String EMPTYNESS = "(empty)";
    private ConnectionSource connectionSource;
    private Dao<MusicLibrary, String> musiclibraryDao;
    private Dao<Genre, String> genreDao;
    private Dao<Track, String> trackDao;
    private File selectedDirectory;

    // To manage the music player
    private MediaPlayer mediaPlayer;
    private Media media;
    private Timer timer;
    private TimerTask task;

    // Only for searching purpose
    private ArrayList<Track> aTracks;
    private ArrayList<Track> fTracks;
    private Set<Track> hTracks = new HashSet<>();

    // Retrieving the objects from the view
    @FXML
    private AnchorPane directoryPane;
    @FXML
//    private ListView genresList, tracksList;
    private ListView tracksList;
    @FXML
    private GridPane genresGrid;
    @FXML
    private Label directoryLabel, trackInfoLabel;
    @FXML
    private Slider volumeSlider, timeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private TextField searchField;
    @FXML
    private ChoiceBox searchModeChoice;

    // Setting the grid objects
    private final int NB_COLUMNS = 5;
    private final double BUTTON_MAX_HEIGHT = 48;
    private ArrayList<Button> genresButtons = new ArrayList<>();

    private final ContextMenu trackContextMenu = new ContextMenu();
    private final ContextMenu genreContextMenu = new ContextMenu();

    private final MenuItem menuEditTrack = new MenuItem("Edit");
    private final MenuItem menuEditGenre = new MenuItem("Edit");
    private final MenuItem menuRemoveTrack = new MenuItem("Remove");
    private final MenuItem menuRemoveGenre = new MenuItem("Remove");

    /**
     * **************************************************************************
     * Methods to initialize. *
     * *************************************************************************
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // We want the media player to be started when an item in the track list is double clicked
        tracksList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent click) {
                if (click.getClickCount() == 2) {
                    playMedia();
                }
            }
        });

        // We want to handle as well drag and drop events to add automatically new tracks
        tracksList.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                mouseDragOver(event);
            }
        });

        tracksList.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                mouseDragDropped(event);
            }
        });

        tracksList.setOnDragExited(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent event) {
                tracksList.getStyleClass().clear();
                tracksList.setStyle(null);
                // tracksList.getStyleClass().add("list-view");
            }
        });

        // intializing seach mode values. I tried to do that in the fxml file without success.
        String[] searchModes = {"Or", "And"};
        searchModeChoice.getItems().setAll(searchModes);
        searchModeChoice.setValue("Or");

        // setting two listeners
        volumeSlider.valueProperty().addListener(
                (o) -> {
                    setVolume();
                });
        searchModeChoice.valueProperty().addListener(
                (o) -> {
                    searchKeyWords();
                });

        // setting the context menu
        trackContextMenu.getItems().addAll(menuEditTrack, menuRemoveTrack);
        genreContextMenu.getItems().addAll(menuEditGenre, menuRemoveGenre);

        tracksList.setContextMenu(trackContextMenu);

        menuRemoveTrack.setOnAction((a) -> {
            try {
                removeTrack();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        menuEditTrack.setOnAction((e) -> {
            try {
                editTrack();
            } catch (Exception t) {
                t.printStackTrace();
            }
        });

        menuRemoveGenre.setOnAction((a) -> {
            try {
                removeGenre();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        menuEditGenre.setOnAction((e) -> {
            try {
                editGenre();
            } catch (Exception t) {
                t.printStackTrace();
            }
        });

        try {
            initializeConnexion();
        } catch (Exception e) {
            // do something appropriate with the exception, *at least*:
            e.printStackTrace();
        }
    }

    public void initializeConnexion() throws Exception {
        connectionSource = null;
        // create our data-source for the database
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);
            // setup our database and DAOs
            musiclibraryDao = DaoManager.createDao(connectionSource, MusicLibrary.class);
            genreDao = DaoManager.createDao(connectionSource, Genre.class);
            trackDao = DaoManager.createDao(connectionSource, Track.class);
            // library = musiclibraryDao.queryForId(DIRECTORY);
            library = musiclibraryDao.queryForAll().get(0);
            if (!Files.isDirectory(Path.of(library.getDirectory()))) {
                musiclibraryDao.updateId(library, System.getProperty("user.home"));
            }

            directoryLabel.setText(library.toString());

            // Initializing the tracks list and the buttons
            initializeButtonsAndLists();
        } finally {
            // destroy the data source which should close underlying connections
            if (connectionSource != null) {
                connectionSource.close();
                connectionSource = null;
            }
        }
    }

    /**
     * **************************************************************************
     * Methods to handle the music. *
     * *************************************************************************
     */
    private void playMedia() {
        Object selectedTrack = tracksList.getSelectionModel().getSelectedItem();
        if (selectedTrack != null) {
            if (!Files.isRegularFile(Path.of(((Track) selectedTrack).getPath()))) {
                Alert alert = new Alert(AlertType.WARNING, ((Track) selectedTrack).getPath() + " has been removed.");
                alert.show();
                return;
            }
            try {
                media = new Media(new File(((Track) selectedTrack).getPath()).toURI().toString());
                trackInfoLabel.setText("loaded: " + ((Track) selectedTrack).toString());
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.dispose();
                    timer.cancel();
                    mediaPlayer = null;
                }
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                mediaPlayer.play();
                beginTimer();
            } catch (MediaException e) {
                Alert alert = new Alert(AlertType.WARNING, "I cannot play " + ((Track) selectedTrack).getPath() + "\nPlease check the file!");
                alert.show();
                e.printStackTrace();
            }
        }
    }

    public void playpauseMedia() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.READY || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED || mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
                beginTimer();
            } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                timer.cancel();
            }
        } else {
            if (media != null) {
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            } else {
                Object selectedTrack = tracksList.getSelectionModel().getSelectedItem();
                if (selectedTrack != null) {
                    playMedia();
                } else {
                    tracksList.getSelectionModel().selectFirst();
                    playMedia();
                }
            }

        }
    }

    private void setVolume() {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
        }
    }

    public void seekTime() {
        songProgressBar.setProgress(timeSlider.getValue() * 0.01);
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.millis(timeSlider.getValue() * mediaPlayer.getTotalDuration().toMillis() * 0.01));
        }

    }

    public void stopMedia() {
        songProgressBar.setProgress(0);
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.stop();
        }
    }

    private void beginTimer() {
        if (timer == null) {
            timer = new Timer();
        } else {
            timer.purge();
            timer = null;
            timer = new Timer();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                double current = mediaPlayer.getCurrentTime().toSeconds();
                songProgressBar.setProgress(mediaPlayer.getCurrentTime().toSeconds() / mediaPlayer.getTotalDuration().toSeconds());
                if (current >= mediaPlayer.getTotalDuration().toSeconds()) {
                    stopMedia();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    /**
     * **************************************************************************
     * Methods to handle the track list. *
     * *************************************************************************
     */
    public void selectTracksByGenre(String genreLabel) throws SQLException {
        if (genreLabel.equals(EMPTYNESS)) {
            tracksList.getItems().setAll(library.getEmptyGenreArrayTracks());
        } else {
            Genre selectedGenre = genreDao.queryForId(genreLabel);
            if (searchField.getText().equals("")) {
                if (selectedGenre != null) {
                    tracksList.getItems().setAll(selectedGenre.getArrayTracks());
                } else {
                    tracksList.getItems().setAll(library.getArrayTracks());
                }
            } else {
                if (selectedGenre != null) {
                    tracksList.getItems().setAll(fTracks.stream()
                            .filter(t -> ((Track) t).getArrayGenres().contains((Genre) selectedGenre))
                            .map(t -> (Track) t)
                            .collect(toCollection(ArrayList::new)));
                }
            }
        }
    }

    public void searchKeyWords() {
        Set<Genre> hGenres = new HashSet<>();
        String[] keyWords = searchField.getText().split(" ");
        if (searchModeChoice.getValue().equals("Or")) {
            hTracks.removeAll(hTracks);
            for (String str : keyWords) {
                aTracks.stream()
                        .filter(t -> t.getKeyWordString().toLowerCase().contains(str.toLowerCase()))
                        .forEach(hTracks::add);
            }
            fTracks = hTracks.stream()
                    .sorted(comparing(Track::getTitle))
                    .collect(toCollection(ArrayList::new));
        } else {
            fTracks = aTracks;
            for (String str : keyWords) {
                fTracks = fTracks.stream()
                        .filter(t -> t.getKeyWordString().toLowerCase().contains(str.toLowerCase()))
                        .collect(toCollection(ArrayList::new));
            }
        }

        tracksList.getItems().setAll(fTracks);
        fTracks.stream()
                .map(t -> t.getArrayGenres())
                .forEach(hGenres::addAll);
        populateGenres(hGenres.stream()
                .sorted(comparing(Genre::getLabel))
                .collect(toCollection(ArrayList::new)));
    }

    private void populateGenres(ArrayList<Genre> cGenres) {
        genresGrid.getChildren().removeAll(genresButtons);
        genresButtons.removeAll(genresButtons);
        double buttonHeight = Math.floor(genresGrid.getPrefHeight() / Math.ceil(cGenres.size() / NB_COLUMNS));
        double buttonWidth = Math.floor(genresGrid.getPrefWidth() / NB_COLUMNS);
        // System.out.println("taille boutton: " + buttonHeight);
        for (int i = 0; i < cGenres.size(); i++) {
            Button nButton = new Button(cGenres.get(i).getLabel());
            nButton.setId("keywordsButton");
            nButton.setPrefWidth(buttonWidth);
            nButton.setPrefHeight(buttonHeight);
            nButton.setMaxHeight(BUTTON_MAX_HEIGHT);
            nButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    try {
                        selectTracksByGenre(nButton.getText());
                    } catch (SQLException s) {
                        s.printStackTrace();
                    }
                }
            });

            nButton.setContextMenu(genreContextMenu);
            genresButtons.add(nButton);
            genresGrid.add(genresButtons.get(i), i % NB_COLUMNS, i / NB_COLUMNS, 1, 1);
        }
        // Ajout d'un bouton supplementaire pour les genres vides
        Button nButton = new Button(EMPTYNESS);
        nButton.setId("keywordsButton");
        nButton.setMinWidth(buttonWidth);
        nButton.setPrefHeight(buttonHeight);
        nButton.setMaxHeight(BUTTON_MAX_HEIGHT);
        nButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    selectTracksByGenre(nButton.getText());
                } catch (SQLException s) {
                    s.printStackTrace();
                }
            }
        });

        nButton.setContextMenu(genreContextMenu);
        genresButtons.add(nButton);
        int i = cGenres.size();
        genresGrid.add(genresButtons.get(i), i % NB_COLUMNS, i / NB_COLUMNS, 1, 1);
    }

    /**
     * **************************************************************************
     * Methods to manage the library. *
     * *************************************************************************
     */
    public void chooseDirectory() throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (Files.isDirectory(Path.of(directoryLabel.getText()))) {
            directoryChooser.setInitialDirectory(new File(directoryLabel.getText()));
        } else {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
        selectedDirectory = directoryChooser.showDialog(directoryPane.getScene().getWindow());
        if (selectedDirectory != null) {
            directoryLabel.setText(selectedDirectory.toString());
        }
    }

    public void refreshLibrary() throws Exception {
        if (!directoryLabel.getText().equals(library.getDirectory())) {
            musiclibraryDao.updateId(library, directoryLabel.getText());
        }

        // We ask for confirmation before running the refresh
        Alert alert = new Alert(AlertType.CONFIRMATION, "This may take about one minute.\nAre you sure?");
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == null || option.get() == ButtonType.CANCEL) {
            return;
        }

        musiclibraryDao.refresh(library);
        List<Path> pathList = new ArrayList<>();
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);
            TableUtils.clearTable(connectionSource, TrackGenre.class);
            TableUtils.clearTable(connectionSource, Genre.class);
            TableUtils.clearTable(connectionSource, Track.class);

            // in case the music directory has been removed by accident.
            if (!Files.isDirectory(Path.of(library.getDirectory()))) {
                Alert alertDirectory = new Alert(AlertType.WARNING, library.getDirectory() + " has been removed.");
                alertDirectory.show();
                initializeConnexion();
                return;
            }
            pathList = Files.walk(Path.of(library.getDirectory())).map(Path::normalize)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".mp3"))
                    .collect(Collectors.toList());

            for (Path path : pathList) {
                addTrack(path.toString());
            }
            initializeButtonsAndLists();
        } finally {
            // destroy the data source which should close underlying connections
            if (connectionSource != null) {
                connectionSource.close();
                connectionSource = null;
            }
        }
    }

    /**
     * **************************************************************************
     * Methods to edit or remove tracks and genres. *
     * *************************************************************************
     */
    private void addTrack(String trackPath) throws Exception {
        MP3Util mp3Util = new MP3Util(trackPath);
        if (mp3Util.isValid()) {
            Map mp3Tags = mp3Util.getMp3Tags();
            Track newTrack = new Track(trackPath, mp3Tags.get("Title").toString(), mp3Tags.get("Artist").toString(), library);
            if (library.addTrack(newTrack)) {
                trackDao.refresh(newTrack);
                ArrayList<String> listGenres = library.convertGenresString(mp3Tags.get("Genres").toString());
                for (String g : listGenres) {
                    Genre newGenre = new Genre(g, library);
                    library.addGenre(newGenre);
                    newTrack.addGenre(newGenre);
                }
            }
        }
    }

    private void editTrack() throws Exception {
        Object selectedObject = tracksList.getSelectionModel().getSelectedItem();
        if (selectedObject != null) {
            Track selectedTrack = (Track) selectedObject;
            selectedTrack.setGenresString();
            // We now need to open a new window and pass the track information to it
            Stage editTrackStage = new Stage();
            editTrackStage.initModality(Modality.NONE);
            editTrackStage.initOwner(directoryPane.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("track.fxml"));
            Scene scene = new Scene(loader.load());
            editTrackStage.setScene(scene);
            TrackController controller = loader.getController();
            controller.initTrack(selectedTrack);
            editTrackStage.show();
        }
    }

    private void editGenre() throws Exception {
        String genreLabel = "";
        for (Button b : genresButtons) {
            if (b.isFocused()) {
                genreLabel = b.getText();
            }
        }
        Genre selectedGenre = genreDao.queryForId(genreLabel);
        if (selectedGenre != null) {
            // We now need to open a new window and pass the genre information to it
            Stage editGenreStage = new Stage();
            editGenreStage.initModality(Modality.NONE);
            editGenreStage.initOwner(directoryPane.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("genre.fxml"));
            Scene scene = new Scene(loader.load());
            editGenreStage.setScene(scene);
            GenreController controller = loader.getController();
            controller.initGenre(selectedGenre);
            editGenreStage.show();
        } else {
            return;
        }
    }

    private void removeTrack() throws Exception {
        Object selectedTrack = tracksList.getSelectionModel().getSelectedItem();
        if (selectedTrack != null) {
            // First, database operations
            // As far as I have understood cascade deleting is not implemented in ormlite
            // So we have to delete the tracksgenres before deleting the track
            // As a reminder, tracksgenres is an intermediate class only used to create a many2many relation
            // between tracks and genres.

            // We ask for confirmation before deleting the genre        
            Alert alert = new Alert(AlertType.CONFIRMATION, "You are about to delete the track " + ((Track) selectedTrack).getPath() + ".\n Are you sure?");
            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == null || option.get() == ButtonType.CANCEL) {
                return;
            }

            ((Track) selectedTrack).getGenres().removeAll(((Track) selectedTrack).getGenres());
            library.getTracks().remove((Track) selectedTrack);
            library.cleanGenres();

            // populate the buttons and the track lists
            initializeButtonsAndLists();

            // Next, file deletion
            Files.deleteIfExists(Path.of(((Track) selectedTrack).getPath()));
        }
    }

    private void removeGenre() throws Exception {
        String genreLabel = "";
        MP3Util mp3Util;
        ArrayList<Track> modifiedTracks;
        Map dataMap = new HashMap<String, String>();
        dataMap.put("Title", "");
        dataMap.put("Artist", "");
        dataMap.put("Genres", "");
        for (Button b : genresButtons) {
            if (b.isFocused()) {
                genreLabel = b.getText();
            }
        }

        // We ask for confirmation before deleting the genre        
        Alert alert = new Alert(AlertType.CONFIRMATION, "You are about to delete the keyword " + genreLabel + ".\n Are you sure?");
        Optional<ButtonType> option = alert.showAndWait();
        if (option.get() == null || option.get() == ButtonType.CANCEL) {
            return;
        }

        Genre selectedGenre = genreDao.queryForId(genreLabel);
        if (selectedGenre != null) {
            //let's first record the tracks on which we should modify the tags
            modifiedTracks = selectedGenre.getArrayTracks();
            // on elimine les genres des tracks en question            
            selectedGenre.getTracks().removeAll(selectedGenre.getTracks());
            library.getGenres().remove(selectedGenre);
            // Now let's modify the tags            
            for (Track t : modifiedTracks) {
                t.setGenresString();
                dataMap.replace("Title", t.getTitle());
                dataMap.replace("Artist", t.getArtist());
                dataMap.replace("Genres", t.getGenresString());
                mp3Util = new MP3Util(t.getPath());
                if (mp3Util.isValid()) {
                    mp3Util.setMp3Tags(dataMap);
                }
            }
        }
        initializeButtonsAndLists();
    }

    public void initializeButtonsAndLists() {
        // populate the buttons and the tracks list
        populateGenres(library.getArrayGenres());
        aTracks = library.getArrayTracks();
        tracksList.getItems().setAll(aTracks);
    }

    /**
     * **************************************************************************
     * Methods to drag and drop new files. *
     * *************************************************************************
     */
    private void mouseDragDropped(final DragEvent e) {
        final Dragboard db = e.getDragboard();
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".mp3");
        boolean success = false;
        if (db.hasFiles() && isAccepted) {
            success = true;
            // Only get the first file from the list
            final File file = db.getFiles().get(0);
            String trackPath = library.getDirectory() + FileSystems.getDefault().getSeparator() + file.getName();

            try {
                if (!Files.isRegularFile(Path.of(trackPath))) {
                    Files.copy(Path.of(file.getAbsolutePath()), Path.of(trackPath));
                    addTrack(trackPath);
                    initializeButtonsAndLists();
                } else {
                    Alert alert = new Alert(AlertType.ERROR, trackPath + " already exists.");
                    alert.show();
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
            // addTrack(file.toPath().toString());
        }
        e.setDropCompleted(success);
        e.consume();
    }

    private void mouseDragOver(final DragEvent e) {
        final Dragboard db = e.getDragboard();
        final boolean isAccepted = db.getFiles().get(0).getName().toLowerCase().endsWith(".mp3");

        if (db.hasFiles() && isAccepted) {
            tracksList.setStyle("-fx-border-color: red;"
                    + "-fx-border-width: 5;"
                    + "-fx-background-color: #C6C6C6;"
                    + "-fx-border-style: solid;");
            e.acceptTransferModes(TransferMode.COPY);
        } else {
            e.consume();
        }
    }

}
