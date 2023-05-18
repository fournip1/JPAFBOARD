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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Comparator.comparing;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
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
    private ConnectionSource connectionSource;
    private Dao<MusicLibrary, String> musiclibraryDao;
    private File selectedDirectory;
    private String songPath;
    
    // This object will be used to manage mp3 tags
    private MP3Utils mp3Utils;
    
    // To manage the music player
    private MediaPlayer mediaPlayer;
    private Media media;    
    private Timer timer;
    private TimerTask task;    
    
    // Only for searching purpose
    private ArrayList<Track> aTracks;
    private Set<Track> hTracks;
    
    // Retrieving the objects from the view
    @FXML
    private AnchorPane directoryPane;    
    @FXML
    private ListView genresList, tracksList;     
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
    
    /****************************************************************************
     * Methods to initialize.                                                   *
     ***************************************************************************/
    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        tracksList.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent click) {
        if (click.getClickCount() == 2) {
            // tracks.remove(tracksList.getSelectionModel().getSelectedItem());
            // tracksList.getItems().setAll(tracks);
           // System.out.println("Classe de l'objet sélectionné:" + Track.class);
           //use this to do whatever you want to. Open Link etc.
           playMedia();
        }
        }
        });

        // intializing seach mode values. I tried to do that in the fxml file without success.
        String[] searchModes = {"Or","And"};
        searchModeChoice.getItems().setAll(searchModes);
        searchModeChoice.setValue("Or");
        
        // setting two listeners
        volumeSlider.valueProperty().addListener(
                (o) -> {setVolume();});
        searchModeChoice.valueProperty().addListener(
                (o) -> {searchKeyWords();});
        
           try
           {
               initializeConnexion();
           }
            catch (Exception e) {
        // do something appropriate with the exception, *at least*:
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
        // library = musiclibraryDao.queryForId(DIRECTORY);
        library = musiclibraryDao.queryForAll().get(0);
        directoryLabel.setText(library.toString());
        genresList.getItems().setAll(library.getArrayGenres());
        
        // Initializing the tracks ArrayList
        aTracks = library.getArrayTracks();
        tracksList.getItems().setAll(aTracks);
        }
        finally {
        // destroy the data source which should close underlying connections
            if (connectionSource != null) {
                connectionSource.close();
                connectionSource = null;
            }
        }
    }    

    /****************************************************************************
     * Methods to handle the music.                                             *
     ***************************************************************************/
    public void playMedia() {
        Object selectedTrack = tracksList.getSelectionModel().getSelectedItem();
        if (selectedTrack!=null) {            
            media = new Media(((Track) selectedTrack).getPath());
            trackInfoLabel.setText("loaded: "+((Track) selectedTrack).getTitle() + " " + ((Track) selectedTrack).getGenresString());
            if (mediaPlayer!=null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                timer.cancel();
                mediaPlayer = null;
            }        
            mediaPlayer = new MediaPlayer(media);        
            mediaPlayer.setVolume(volumeSlider.getValue()*0.01);        
            mediaPlayer.play();    
            beginTimer();
        }      
    }

    
    public void playpauseMedia() {
        // library.setDirectory("/home/lemerle");
        if (mediaPlayer!=null) {
            if (mediaPlayer.getStatus()==MediaPlayer.Status.READY || mediaPlayer.getStatus()==MediaPlayer.Status.PAUSED || mediaPlayer.getStatus()==MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
                beginTimer();          
            } else if (mediaPlayer.getStatus()==MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                timer.cancel();
            }
        } else {
            if (media!=null) {
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

    public void setVolume() {
        if (mediaPlayer!=null) {
            mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
        }        
    }

    public void seekTime() {
        songProgressBar.setProgress(timeSlider.getValue()*0.01);
        if (mediaPlayer!=null) {
            mediaPlayer.seek(Duration.millis(timeSlider.getValue()*mediaPlayer.getTotalDuration().toMillis()*0.01));
        }  
        
    }
    
    
    public void stopMedia() {
        songProgressBar.setProgress(0);
        if (mediaPlayer!= null) {            
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.stop();
        }
    }

    public void beginTimer() {
        if (timer == null) {
            timer = new Timer();
        }   else {
            timer.purge();
            timer = null;
            timer = new Timer();
        }
        task = new TimerTask() {
                @Override
                public void run() {
                    double current = mediaPlayer.getCurrentTime().toSeconds();                
                    songProgressBar.setProgress(mediaPlayer.getCurrentTime().toSeconds()/ mediaPlayer.getTotalDuration().toSeconds());                
                    if (current>=mediaPlayer.getTotalDuration().toSeconds()) {
                        stopMedia();                        
                    }
                }
            };
        
        timer.scheduleAtFixedRate(task, 0, 1000);
    }   


    /****************************************************************************
     * Methods to handle the track list.                                        *
     ***************************************************************************/    
    public void selectTracksByGenre() {
        Object selectedGenre = genresList.getSelectionModel().getSelectedItem();
        if (searchField.getText().equals("")) {
            if (selectedGenre!=null) {
                tracksList.getItems().setAll(((Genre) selectedGenre).getArrayTracks());
            } else {
                tracksList.getItems().setAll(library.getArrayTracks());
            }
        } else {
            if (selectedGenre!=null) {
                tracksList.getItems().setAll( hTracks.stream()                        
                        .filter(t -> ((Track) t).getArrayGenres().contains((Genre) selectedGenre))
                        .map(t -> (Track) t)
                        .collect(toCollection(ArrayList::new)));
                // .filter((Track t) -> t.getArrayGenres().contains((Genre selectedGenre)))
                
            }
        }
    }

    public void searchKeyWords() {        
        Set<Genre> hGenres = new HashSet<>();
        ArrayList<Track> fTracks;   
        String[] keyWords = searchField.getText().split(" ");
        if (searchModeChoice.getValue().equals("Or")) {
            hTracks = new HashSet<>();
            for (String str:keyWords) {
                aTracks.stream()
                        .filter(t -> t.getKeyWordString().toLowerCase().contains(str.toLowerCase()))
                        .forEach(hTracks::add);
            }
            fTracks = hTracks.stream()
                    .sorted(comparing(Track::getTitle))
                    .collect(toCollection(ArrayList::new));            
        } else {
            fTracks = aTracks;
            for (String str:keyWords) {
                fTracks = fTracks.stream()
                        .filter(t -> t.getKeyWordString().toLowerCase().contains(str.toLowerCase()))
                        .collect(toCollection(ArrayList::new));
            }
        }
        
        tracksList.getItems().setAll(fTracks);
        fTracks.stream()
                .map(t -> t.getArrayGenres())
                .forEach(hGenres::addAll);        
        
        // Debugging purpose
        // System.out.println("Nombre de genres:" + hGenres.size());              
        genresList.getItems().setAll(hGenres.stream()
                    .sorted(comparing(Genre::getLabel))
                    .collect(toCollection(ArrayList::new)));
    }


    /****************************************************************************
     * Methods to manage the library.                                           *
     ***************************************************************************/    
    public void chooseDirectory() throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (Files.isDirectory(Paths.get(directoryLabel.getText()))) {
            directoryChooser.setInitialDirectory(new File(directoryLabel.getText()));
        }
        else {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }
            selectedDirectory = directoryChooser.showDialog(directoryPane.getScene().getWindow());
        if ( selectedDirectory!=null) {
            directoryLabel.setText(selectedDirectory.toString());
        }
    }
    
    public void refreshLibrary() throws Exception {
        if (!directoryLabel.getText().equals(library.getDirectory())) {
                musiclibraryDao.updateId(library, directoryLabel.getText());        
        }
                musiclibraryDao.refresh(library);
                List<Path> pathList = new ArrayList<>();
                Dao<Track, String> trackDao;
                String[] listGenres;
                
                try (Stream<Path> stream = Files.walk(Paths.get(library.getDirectory()))) {
                      // Do something with the stream.
                pathList = stream.map(Path::normalize)
                  .filter(Files::isRegularFile)
                  .filter(path -> path.getFileName().toString().endsWith(".mp3"))
                  .collect(Collectors.toList());

                // print size for debug     
                System.out.println("Nombre de fichiers: " + pathList.size());
                  connectionSource = new JdbcConnectionSource(DATABASE_URL);                     
                  trackDao = DaoManager.createDao(connectionSource, Track.class);
                  TableUtils.clearTable(connectionSource, TrackGenre.class);
                  TableUtils.clearTable(connectionSource, Genre.class);
                  TableUtils.clearTable(connectionSource, Track.class);
                  for (Path path : pathList) {
                        songPath=path.toFile().toURI().toString();
                        System.out.println("Fichier examine:" + path.toString());
                        mp3Utils = new MP3Utils(path.toString());
                        Map mp3Tags = mp3Utils.getMp3Tags();
                        Track newTrack = new Track(songPath,mp3Tags.get("Title").toString(),mp3Tags.get("Artist").toString(), library);
                        library.addTrack(newTrack);
                        trackDao.refresh(newTrack);

  //                      Track newTrack = new Track(songPath,mp3Tags.get("Title").toString(),mp3Tags.get("Artist").toString(), library);
  //                      trackDao.create(newTrack);
  //                      trackDao.refresh(newTrack);
                        listGenres = mp3Tags.get("Genres").toString().split(", ");

                            for (String g : listGenres) {
                                Genre newGenre = new Genre(g,library);
                                library.addGenre(newGenre);
                                newTrack.addGenre(newGenre);
                            }                                           
                        }                  
                  genresList.getItems().setAll(library.getArrayGenres());                  
                  // This initializes the tracks ArrayList
                  aTracks = library.getArrayTracks();                  
                  tracksList.getItems().setAll(aTracks);
                    } finally {
			// destroy the data source which should close underlying connections
			if (connectionSource != null) {
				connectionSource.close();
                                connectionSource = null;
			}
		}                
        }     
   
}
