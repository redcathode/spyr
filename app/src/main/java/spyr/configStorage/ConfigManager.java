package spyr.configStorage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import spyr.App;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ConfigManager {
    ArrayList<Song> songJsonList;
    AppConfig appConfig;
    Type songJsonListType = new TypeToken<ArrayList<Song>>(){}.getType();
    public static String createDefaultConfig() {
        return "{\"isDarkMode\":false}";
    }
    public static String createTestSongsConfig() {
        return "[]";
    }
    static class SongComparator implements Comparator<Song> {
        public int compare(Song o1, Song o2) {
            return Integer.compare(o2.timesListenedTo, o1.timesListenedTo);
        }
    }
    public ConfigManager() {
        Gson gson = new Gson();
        try {
            songJsonList = gson.fromJson(new JsonReader(new FileReader(App.songsJson)), songJsonListType);
            appConfig = gson.fromJson(new JsonReader(new FileReader(App.configJson)), AppConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void removeSongFromJson(int index) {
        songJsonList.remove(index);
    }
    public void addSongToJson(String songName, String songId) {
        if (!songJsonList.isEmpty()) {
            boolean foundSong = false;
            for (Song song : songJsonList) {
                if (song.youtubeId.equals(songId)) {
                    song.timesListenedTo++;
                    songJsonList.sort(new SongComparator());
                    foundSong = true;
                }
            }
            // could this be replaced with a break statement or something?
            if (!foundSong){
                songJsonList.add(new Song(
                        songName,
                        songId,
                        1
                ));
            }
        } else {
            songJsonList.add(new Song(
                    songName,
                    songId,
                    1
            ));
        }
        App.mainWindow.refreshJList();
        Gson gsonTest = new Gson();
        System.out.println("Current song JSON: " + gsonTest.toJson(songJsonList));
    }
    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(songJsonList);
    }
    public String getTitle(int index) {
        return songJsonList.get(index).name;
    }
    public int getTimesListenedTo(int index) {
        return songJsonList.get(index).timesListenedTo;
    }
    public String getYoutubeId(int index) {
        return songJsonList.get(index).youtubeId;
    }
    public int getNumSongs() {
        return songJsonList.size();
    }
    public boolean getIsDarkMode() {
        return appConfig.isDarkMode;
    }
    public void setIsDarkMode(boolean isDarkMode) {
        appConfig.isDarkMode = isDarkMode;
    }
    public String getAppConfigJson() {
        Gson gson = new Gson();
        return gson.toJson(appConfig);
    }
}
