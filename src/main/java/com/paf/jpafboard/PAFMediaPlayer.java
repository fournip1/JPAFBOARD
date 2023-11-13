/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paf.jpafboard;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.control.Alert;
import javafx.scene.media.MediaException;

/**
 *
 * @author lemerle
 */
public class PAFMediaPlayer {

    public final static double FADING_DURATION = 3000.0;

    private Track currentSong, nextSong;
    private ArrayList<Track> previousSongs = new ArrayList<>();
    private MediaPlayer mediaPlayer, tempMediaPlayer;
    private Media media;
    private boolean fading, looping;
    private double volume = 0.0;

    public PAFMediaPlayer(Track currentSong, boolean fading, boolean looping, double volume) {
        this.currentSong = currentSong;
        this.fading = fading;
        this.looping = looping;
        this.volume = volume;
        playCurrent();
    }

    private void playCurrent() {
        try {
            if (!Files.isRegularFile(Path.of(currentSong.getPath()))) {
                Alert alert = new Alert(Alert.AlertType.WARNING, currentSong + " has been removed.");
                alert.show();
                return;
            }
            media = new Media(new File(currentSong.getPath()).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            // Is the repeat CheckBox selected?
            if (looping) {
                // mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setOnEndOfMedia(() -> {
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                });
            } else {
                mediaPlayer.setOnEndOfMedia(() -> {
                    stop();
                });
            }

            if (fading) {
                // System.out.println("Fading in!");
                mediaPlayer.setVolume(0);
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(FADING_DURATION),
                                new KeyValue(mediaPlayer.volumeProperty(), volume)));
                timeline.play();
            } else {
                mediaPlayer.setVolume(volume);
            }
            mediaPlayer.play();
        } catch (MediaException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "I cannot play " + currentSong + "\nPlease check the file!");
            alert.show();
            e.printStackTrace();
        }
    }

    public void playPrevious() {
        if (!previousSongs.isEmpty() && mediaPlayer != null) {
            setCurrentSong(previousSongs.get(0), false);
            previousSongs.remove(0);
        }
    }

    public void playNext() {
        if (mediaPlayer != null) {
            if (nextSong != null) {
                setCurrentSong(nextSong, true);
                nextSong = null;
            } else {
                stop();
            }
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            // we fade out if the option is selected
            if (fading && getTotalDuration() - getCurrentTime() > FADING_DURATION) {
                // System.out.println("Fading out now!");
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.millis(FADING_DURATION),
                                new KeyValue(mediaPlayer.volumeProperty(), 0)));
                timeline.setOnFinished(eh
                        -> {
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.stop();
                });
                timeline.play();
            } else {
                mediaPlayer.seek(Duration.ZERO);
                mediaPlayer.stop();
            }
        }
    }

    public void setVolume(double volume) {
        this.volume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    // here time is given as a number between 0 and 1
    public void setTime(double time) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.millis(time * getTotalDuration()));
        }
    }

    public void setCurrentSong(Track currentSong, boolean addToStack) {
        if (this.currentSong != null && addToStack) {
            previousSongs.add(0, this.currentSong);
        }
        this.currentSong = currentSong;
        if (mediaPlayer != null) {
            // we fading is selected
            if (fading && (getTotalDuration() - getCurrentTime()) > FADING_DURATION) {
                tempMediaPlayer = mediaPlayer;
                stopTempMedia();
            } else {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
        }
        playCurrent();
    }

    private void stopTempMedia() {
        // System.out.println("Fading out now!");
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(FADING_DURATION),
                        new KeyValue(tempMediaPlayer.volumeProperty(), 0)));
        timeline.setOnFinished(eh
                -> {
            if (tempMediaPlayer != null) {
                tempMediaPlayer.stop();
                tempMediaPlayer.dispose();
                tempMediaPlayer = null;
            }
        });
        timeline.play();
    }

    public void setNextSong(Track nextSong) {
        this.nextSong = nextSong;
    }

    public void setFading(boolean fading) {
        this.fading = fading;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
        if (mediaPlayer != null) {
            if (looping) {
                // mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                mediaPlayer.setOnEndOfMedia(() -> {
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                });
            } else {
                mediaPlayer.setOnEndOfMedia(() -> {
                    stop();
                });
            }
        }
    }

    public double getTotalDuration() {
        if (mediaPlayer == null) {
            return 0.0;
        } else {
            return mediaPlayer.getTotalDuration().toMillis();
        }
    }

    public double getCurrentTime() {
        if (mediaPlayer == null) {
            return 0.0;
        } else {
            return mediaPlayer.getCurrentTime().toMillis();
        }
    }

    public Track getPreviousSong() {
        if (!previousSongs.isEmpty()) {
            return previousSongs.get(0);
        } else {
            return null;
        }
    }

    public Track getNextSong() {
        return this.nextSong;
    }

    public Track getCurrentSong() {
        return currentSong;
    }

    public Status getStatus() {
        if (mediaPlayer == null) {
            return MediaPlayer.Status.UNKNOWN;
        } else {
            return mediaPlayer.getStatus();
        }
    }
}