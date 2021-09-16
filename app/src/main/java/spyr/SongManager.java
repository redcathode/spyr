package spyr;

import java.io.File;

public class SongManager {
    public String[] songList;
    public String[] songDescList;
    public File[] songFileList;
    public AudioQuality quality;
    public SongManager(AudioQuality audioQuality) {
        quality = audioQuality;
    }
    public String getSongNameFromIndex(int index) {
        return songList[index];
    }

    public void addSongFromURL(String query) {
        System.out.println("This is unimplemented, but would add song: " + query);
    }

    public static boolean isYoutubeURL(String query) {
        return query.contains("youtube.com") || query.contains("youtu.be");

    }
}
