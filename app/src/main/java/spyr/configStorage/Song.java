package spyr.configStorage;

public class Song {
    String name;
    String youtubeId;
    String author;
    int timesListenedTo;
    public Song(String name, String youtubeId, String author, int timesListenedTo) {
        this.timesListenedTo = timesListenedTo;
        this.youtubeId = youtubeId;
        this.name = name;
        this.author = author;
    }
    public String getYoutubeId() {
        return youtubeId;
    }
    public String getAuthor() {
        return author;
    }
}
