package com.mona.music;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists playlists as JSON in SharedPreferences so they survive app restarts.
 */
public class PlaylistStorage {

    private static final String PREFS_NAME = "playlist_prefs";
    private static final String KEY_PLAYLISTS = "playlists_json";

    public static boolean hasData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.contains(KEY_PLAYLISTS);
    }

    public static void save(Context context, List<Playlist> playlists) {
        try {
            JSONArray array = new JSONArray();
            for (Playlist playlist : playlists) {
                JSONObject playlistJson = new JSONObject();
                playlistJson.put("name", playlist.getName());

                JSONArray songsJson = new JSONArray();
                for (Song song : playlist.getSongs()) {
                    JSONObject songJson = new JSONObject();
                    songJson.put("id", song.getId());
                    songJson.put("title", song.getTitle());
                    songJson.put("artist", song.getArtist());
                    songJson.put("path", song.getPath());
                    songJson.put("duration", song.getDuration());
                    songJson.put("albumId", song.getAlbumId());
                    songsJson.put(songJson);
                }
                playlistJson.put("songs", songsJson);
                array.put(playlistJson);
            }

            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_PLAYLISTS, array.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Playlist> load(Context context) {
        List<Playlist> playlists = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_PLAYLISTS, null);
        if (json == null) return playlists;

        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject playlistJson = array.getJSONObject(i);
                Playlist playlist = new Playlist(playlistJson.getString("name"));

                JSONArray songsJson = playlistJson.optJSONArray("songs");
                if (songsJson != null) {
                    for (int j = 0; j < songsJson.length(); j++) {
                        JSONObject songJson = songsJson.getJSONObject(j);
                        playlist.getSongs().add(new Song(
                                songJson.getLong("id"),
                                songJson.getString("title"),
                                songJson.getString("artist"),
                                songJson.getString("path"),
                                songJson.getLong("duration"),
                                songJson.getLong("albumId")));
                    }
                }
                playlists.add(playlist);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlists;
    }
}
