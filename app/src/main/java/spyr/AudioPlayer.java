package spyr;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

import javax.swing.*;

public class AudioPlayer {
    private final AudioPlayerComponent audioPlayerComponent;

    public void exit() {
        // It is not allowed to call back into LibVLC from an event handling thread, so submit() is used
        audioPlayerComponent.mediaPlayer().submit(new Runnable() {
            @Override
            public void run() {
                audioPlayerComponent.mediaPlayer().release();
            }
        });
    }
    public boolean isPlaying() {
        return audioPlayerComponent.mediaPlayer().status().isPlaying();
    }
    public void setPosition(float position) {
        audioPlayerComponent.mediaPlayer().controls().setPosition(position);
    }

    public AudioPlayer() {
        audioPlayerComponent = new AudioPlayerComponent();
        audioPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                mediaPlayer.submit(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.media().play(App.mainWindow.getNextSongUrl());
                    }
                });

            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("Error with LibVLC");
            }
        });
    }

    public void start(String mrl) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                App.mainWindow.setPlaying();
            }
        });
        audioPlayerComponent.mediaPlayer().media().play(mrl);
    }
    public void startLivestream(String mrl) {

    }
    public void play() {
        audioPlayerComponent.mediaPlayer().controls().play();
    }
    public void pause() {
        audioPlayerComponent.mediaPlayer().controls().pause();
    }
    public int getPercentage() {
        return Math.round(audioPlayerComponent.mediaPlayer().status().position() * 100);
    }
}