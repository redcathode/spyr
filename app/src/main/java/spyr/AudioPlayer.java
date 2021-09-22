package spyr;

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent;

public class AudioPlayer {
    private final AudioPlayerComponent audioPlayerComponent;

    public void exit(int result) {
        // It is not allowed to call back into LibVLC from an event handling thread, so submit() is used
        audioPlayerComponent.mediaPlayer().submit(new Runnable() {
            @Override
            public void run() {
                audioPlayerComponent.mediaPlayer().release();
                System.exit(result);
            }
        });
    }

    public AudioPlayer() {
        audioPlayerComponent = new AudioPlayerComponent();
        audioPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void finished(MediaPlayer mediaPlayer) {
                //todo: implement this
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("Error with LibVLC");
            }

            @Override
            public void positionChanged(MediaPlayer player, final float newPosition) {
                System.out.println(Math.round(newPosition * 100) + "%");
            }
        });
    }

    public void start(String mrl) {
        audioPlayerComponent.mediaPlayer().media().play(mrl);
    }

}