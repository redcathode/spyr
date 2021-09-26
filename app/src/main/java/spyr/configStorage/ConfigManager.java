package spyr.configStorage;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import spyr.App;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ConfigManager {
    ArrayList<Song> songJsonList;
    Type songJsonListType = new TypeToken<ArrayList<Song>>(){}.getType();
    public static String createDefaultConfig() {
        return "{'isDarkMode':false}";
    }
    public static String createTestSongsConfig() {
        return "[]";
    }
    public ConfigManager() {
        Gson gson = new Gson();
        try {
            songJsonList = gson.fromJson(new JsonReader(new FileReader(App.songsJson)), songJsonListType);
            System.out.println(songJsonList.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void addSongToJson(String songName, String songUrl) {
        songJsonList.add(new Song(
                songName,
                songUrl,
                1
        ));  // TODO: count number of songs instead of adding every song and get rid of next line
        Gson gsonTest = new Gson();
        System.out.println("Current song JSON: " + gsonTest.toJson(songJsonList));
    }
    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(songJsonList);
    }

}
