package spyr;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

public class AudioPlayer {
    private final AudioPlayerComponent audioPlayerComponent;
    public long totalTimeMs;
    public long currentTimeMs;
    public int currentPercentage;

    public void exit() {
        // It is not allowed to call back into LibVLC from an event handling thread, so submit() is used
        audioPlayerComponent.mediaPlayer().submit(new Runnable() {
            @Override
            public void run() {
                audioPlayerComponent.mediaPlayer().release();
            }
        });
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
                        System.out.println("finished song but this needs to be implemented");
                        mediaPlayer.media().play(App.mainWindow.getNextSongUrl());
                    }
                });

            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("Error with LibVLC");
            }

            @Override
            public void positionChanged(MediaPlayer player, final float newPosition) {
                currentPercentage = Math.round(newPosition * 100);
                System.out.println(currentPercentage + "%");
            }
        });
    }

    public void start(String mrl) {
        audioPlayerComponent.mediaPlayer().media().play(mrl);
    }
    public void play() {
        audioPlayerComponent.mediaPlayer().controls().play();
    }
    public void pause() {
        audioPlayerComponent.mediaPlayer().controls().pause();
    }

}