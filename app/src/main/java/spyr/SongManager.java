package spyr;


import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import spyr.configStorage.ConfigManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
            id = url.substring(url.indexOf("youtube.com") + 20, url.indexOf("youtube.com") + 31);
        } else if (url.contains("youtu.be")) {
            id = url.substring(url.indexOf("youtu.be") + 9, url.indexOf("youtu.be") + 20);
        } else {
            id = "";
        }
        return id;
    }
    private String getOdesliUrl(String query) throws UnsupportedEncodingException {
        return "https://api.song.link/v1-alpha.1/links?url=" + URLEncoder.encode(query, "UTF-8");
    }
    public void addSongLinkFromOdesli(String url) throws IOException {
        URL songUrl = new URL(getOdesliUrl(url));
        System.out.println(songUrl.toString());
        HttpURLConnection connection = (HttpURLConnection) songUrl.openConnection();
        connection.setRequestProperty("accept", "application/json");
        InputStream responseStream = connection.getInputStream();
        String jsonResult = CharStreams.toString(new InputStreamReader(responseStream, Charsets.UTF_8));
        addExistingSong(jsonResult.substring(jsonResult.indexOf("YOUTUBE_VIDEO::") + 15, jsonResult.indexOf("YOUTUBE_VIDEO::") + 26));
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
        Format formatByItag = video.findFormatByItag(251);
        String vidUrl;
        if (formatByItag != null) {
            System.out.println("opus: " + formatByItag.url());
            vidUrl = formatByItag.url();
        } else {
            formatByItag = video.bestAudioFormat();
            if (formatByItag != null) {
                vidUrl = formatByItag.url();
            } else {
                //just let vlc handle it lol
                vidUrl = String.format("https://www.youtube.com/watch?v=" + videoId);
                System.out.println(vidUrl);
            }
        }

        String vidTitle = video.details().title();
        songURLList.add(vidUrl);
        songTitleList.add(vidTitle);
        songDescList.add(video.details().description());
        configManager.addSongToJson(vidTitle, videoId);
        System.out.println("added song " + songTitleList.get(songTitleList.size() - 1));
    }
    public void removeSong(int index) {
        songURLList.remove(index);
        songTitleList.remove(index);
        songDescList.remove(index);
        App.mainWindow.refreshJList();
        if (App.mainWindow.getPlayingIndex() == index) {
            App.audioPlayer.stop();
        }
    }
    public void addExistingSong(String videoId) {
        YoutubeDownloader downloader = new YoutubeDownloader();
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = downloader.getVideoInfo(request);
        VideoInfo video = response.data();
        // get opus track for song
        // TODO: fallback to other audio tracks if this specific itag is unavailable, and actually make AudioQuality do something
        Format formatByItag = video.findFormatByItag(251);
        String vidUrl;
        if (formatByItag != null) {
            System.out.println("opus: " + formatByItag.url());
            vidUrl = formatByItag.url();
        } else {
            formatByItag = video.bestAudioFormat();
            if (formatByItag != null) {
                vidUrl = formatByItag.url();
            } else {
                //just let vlc handle it lol
                vidUrl = String.format("https://www.youtube.com/watch?v=" + videoId);
                System.out.println(vidUrl);
            }
        }
        String vidTitle = video.details().title();
        songURLList.add(vidUrl);
        songTitleList.add(vidTitle);
        songDescList.add(video.details().description());
        configManager.addSongToJson(vidTitle, videoId);
    }

    public static boolean isYoutubeURL(String query) {
        return query.contains("youtube.com") || query.contains("youtu.be");
    }
}
