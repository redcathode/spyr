package spyr;


import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import spyr.configStorage.ConfigManager;

import java.util.ArrayList;


public class SongManager {
    public int playingIndex = 0;
    public ConfigManager configManager;
    public ArrayList<String> songTitleList = new ArrayList<>();
    public ArrayList<String> songURLList = new ArrayList<>();
    public ArrayList<String> songDescList = new ArrayList<>();
    public AudioQuality quality;

    public SongManager(AudioQuality audioQuality) {
        quality = audioQuality;
        configManager = new ConfigManager();
    }
    static YoutubeDownloader downloader = new YoutubeDownloader();
    public String getSongUrl(int index) {
        playingIndex = index;
        return songURLList.get(index);
    }
    public String getNextSongUrl() {
        if (playingIndex + 1 > songURLList.size()) {
            playingIndex = 0;
        } else {
            playingIndex++;
        }
        return songURLList.get(playingIndex);
    }
    private String getVideoId(String url) {
        String id;

        if (url.contains("youtube.com")) {
            id = url.substring(32);
        } else {
            // todo: do this properly
            id = "";
        }
        return id;
    }
    public void addSongFromURL(String query) {
        // might wanna do all of this asynchronously but whatever
        System.out.println(getVideoId(query));
        YoutubeDownloader downloader = new YoutubeDownloader();
        String videoId = getVideoId(query);
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = downloader.getVideoInfo(request);
        VideoInfo video = response.data();

        // get opus track for song
        // TODO: fallback to other audio tracks if this specific itag is unavailable, and actually make AudioQuality do something
        Format formatOpusByItag = video.findFormatByItag(251);
        if (formatOpusByItag != null) {
            System.out.println("opus: " + formatOpusByItag.url());
        }
        String vidTitle = video.details().title();
        songURLList.add(formatOpusByItag.url());
        songTitleList.add(vidTitle);
        songDescList.add(video.details().description());
        configManager.addSongToJson(vidTitle, videoId);
        System.out.println("added song " + songTitleList.get(songTitleList.size() - 1));
    }

    public static boolean isYoutubeURL(String query) {
        return query.contains("youtube.com") || query.contains("youtu.be");
    }
}
