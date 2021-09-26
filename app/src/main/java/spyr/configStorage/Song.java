package spyr.configStorage;

public class Song {
    String name;
    String youtubeId;
    int timesListenedTo;
    public Song(String songName, String songYtId, int timesListenedToSong) {
        timesListenedTo = timesListenedToSong;
        youtubeId = songYtId;
        name = songName;
    }
    public String getYoutubeId() {
        return youtubeId;
    }
}
