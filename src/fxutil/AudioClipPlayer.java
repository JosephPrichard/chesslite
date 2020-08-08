/*
 * An class that uses JavaFx's MediaPlayer and AudioClip classes to create a medium to preload and 
 * keep sound loaded sound effects used in the application
 * 8/2/20
 */
package fxutil;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 *
 * @author Joseph
 */
public class AudioClipPlayer {
    
    private final MediaPlayer mediaPlayer;
    private final AudioClip audioClip;
    private final int period; //in seconds
    private Timeline soundLoop;
    
    /**
     * Constructs an AudioClipPlayer with a period refresh of 15 
     * @param path, the path of the media for the player
     */
    public AudioClipPlayer(String path) {
        period = 15;
        mediaPlayer = new MediaPlayer(new Media(path));
        audioClip = new AudioClip(path);
    }
    
    /**
     * Constructs an AudioClipPlayer with an input period refresh and path
     * @param path, the path of the media for the player
     * @param periodIn, the period for the player
     */
    public AudioClipPlayer(String path, int periodIn) {
        period = periodIn;
        mediaPlayer = new MediaPlayer(new Media(path));
        audioClip = new AudioClip(path);
    }
    
    /**
     * Starts the sound loop, its purpose is to keep the sound locally in RAM to prevent the sound from
     * being unloaded
     */
    public void startLoop() {
        mediaPlayer.setMute(true);
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.stop();
        });
        soundLoop = new Timeline();
        soundLoop.getKeyFrames().add(new KeyFrame(Duration.seconds(period), e -> {
            mediaPlayer.play();
        }));
        soundLoop.setCycleCount(Timeline.INDEFINITE);
        soundLoop.play();
    }
    
    /**
     * Stops the sound loop;
     */
    public void stopLoop() {
        soundLoop.stop();
    }
    
    /**
     * Plays the audio clip.
     */
    public void play() {
        audioClip.play();
    }

}
