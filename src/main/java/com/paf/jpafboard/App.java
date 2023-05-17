/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paf.jpafboard;
//
//import com.j256.ormlite.dao.Dao;
//import com.j256.ormlite.dao.DaoManager;
//import com.j256.ormlite.jdbc.JdbcConnectionSource;
//import com.j256.ormlite.support.ConnectionSource;
//import com.j256.ormlite.table.TableUtils;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.io.IOException;
import java.sql.SQLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;




/**
 *
 * @author lemerle
 */


public class App extends Application {

//        private final static String DIRECTORY = "/home/lemerle/Nextcloud/Reves_Party_Playlist";    
//    	// we are using sqlite database
    private final static String DATABASE_DIRECTORY = System.getProperty("user.home") + FileSystems.getDefault().getSeparator() + ".jpafboard";
    private final static String DATABASE_PATH = DATABASE_DIRECTORY + FileSystems.getDefault().getSeparator() + "pafboard.db";
    private final static String DATABASE_URL = "jdbc:sqlite:" + DATABASE_PATH;
    
    private ConnectionSource connectionSource;
    private Dao<MusicLibrary, String> musiclibraryDao;   
    private MusicLibrary library;
    
     @Override
	public void start(Stage stage) throws Exception {
                System.out.println(DATABASE_URL);
                
                // Create the database directory, if not exists
                if (!Files.isDirectory(Paths.get(DATABASE_DIRECTORY))) {
                    Files.createDirectory(Paths.get(DATABASE_DIRECTORY));
                }
                
                // Create the database if not exists
                if (!Files.isRegularFile(Paths.get(DATABASE_PATH))) {
                    createDatabase();                    
                }
		Parent root = FXMLLoader.load(getClass().getResource("directory.fxml"));
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				Platform.exit();
				System.exit(0);	
			}		
		});
		}	

	public static void main(String[] args) {
		launch(args);
	}
        
        public void createDatabase() throws Exception {
            connectionSource = null;
			// create our data-source for the database
        try {
            connectionSource = new JdbcConnectionSource(DATABASE_URL);                     
            // setup our database and DAOs
            TableUtils.createTableIfNotExists(connectionSource, MusicLibrary.class);
            TableUtils.createTableIfNotExists(connectionSource, Track.class);
            TableUtils.createTableIfNotExists(connectionSource, Genre.class);
            TableUtils.createTableIfNotExists(connectionSource, TrackGenre.class);
            musiclibraryDao = DaoManager.createDao(connectionSource, MusicLibrary.class);        
            // library = musiclibraryDao.queryForId(DIRECTORY);
            library = new MusicLibrary(System.getProperty("user.home"));
            musiclibraryDao.create(library);
        }
        finally {
        // destroy the data source which should close underlying connections
            if (connectionSource != null) {
                connectionSource.close();
                connectionSource = null;
            }
            
        }
        }
        
        public static String getURL() {
            return DATABASE_URL;
        }
}
