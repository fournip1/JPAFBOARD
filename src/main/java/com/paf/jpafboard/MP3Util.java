/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paf.jpafboard;

import java.io.IOException;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Alert;

/**
 *
 * @author lemerle
 */
public class MP3Util {

    private String mp3Path;
    private Mp3File mp3File;
    private boolean valid = true;

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public MP3Util(String mp3Path) throws UnsupportedTagException, InvalidDataException, IOException, NotSupportedException {
        this.mp3Path = mp3Path;
        try {
            this.mp3File = new Mp3File(mp3Path);
        } catch (Exception e) {
            Alert alertPath = new Alert(Alert.AlertType.WARNING, mp3Path + " has been corrupted or removed.");
            alertPath.show();
            this.valid = false;
        }
    }

    public String getMp3Path() {
        return mp3Path;
    }

    public Map<String, String> getMp3Tags() {
        // public void getMp3Tags() {
        Map dataMap = new HashMap<String, String>();
        dataMap.put("Title", "");
        dataMap.put("Artist", "");
        dataMap.put("Genres", "");
        if (mp3File.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3File.getId3v1Tag();
            if (id3v1Tag.getTitle() != null) {
                dataMap.replace("Title", id3v1Tag.getTitle());
            }
            if (id3v1Tag.getArtist() != null) {
                dataMap.replace("Artist", id3v1Tag.getArtist());
            }
            if (id3v1Tag.getGenreDescription() != null) {
                dataMap.replace("Genres", id3v1Tag.getGenreDescription());
            }
        } else if (mp3File.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3File.getId3v2Tag();
            if (id3v2Tag.getTitle() != null) {
                dataMap.replace("Title", id3v2Tag.getTitle());
            }
            if (id3v2Tag.getArtist() != null) {
                dataMap.replace("Artist", id3v2Tag.getArtist());
            }
            if (id3v2Tag.getComment() != null) {
                dataMap.replace("Genres", id3v2Tag.getComment());
            }
        } else {
            System.out.println("No tags found");
        }
        return dataMap;
    }

    public void setMp3Tags(Map<String, String> dataMap) throws Exception {
        ID3v2 id3v2Tag;
        String mp3BackupFilePath = mp3Path.substring(0, mp3Path.length() - 4) + "_bck.mp3";
        if (mp3File.hasId3v2Tag()) {
            id3v2Tag = mp3File.getId3v2Tag();

        } else {
            if (mp3File.hasId3v1Tag()) {
                mp3File.removeId3v1Tag();
            }
            if (mp3File.hasCustomTag()) {
                mp3File.removeCustomTag();
            }
            id3v2Tag = new ID3v24Tag();
            mp3File.setId3v2Tag(id3v2Tag);
        }
        id3v2Tag.setTitle(dataMap.get("Title"));
        id3v2Tag.setArtist(dataMap.get("Artist"));
        id3v2Tag.setComment(dataMap.get("Genres"));
        // id3v2Tag.setComment("Updated with ze PAF board");
        mp3File.save(mp3BackupFilePath);
        Files.move(Path.of(mp3BackupFilePath), Path.of(mp3Path), REPLACE_EXISTING);
    }

    public boolean isValid() {
        return valid;
    }
}
