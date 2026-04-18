package com.mona.music;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private MaterialCardView miniPlayerContainer;
    private ImageButton btnMiniPlay;
    private TextView tvMiniTitle, tvMiniArtist;
    private ImageView imgMiniCover;

    private android.media.MediaPlayer mediaPlayer;
    private Song currentSong;
    private boolean isPlaying = false;

    private List<Song> songList = new ArrayList<>();
    private int currentSongIndex = -1;
    private boolean isShuffleOn = false;
    private boolean isRepeatOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        miniPlayerContainer = findViewById(R.id.mini_player_container);
        btnMiniPlay = findViewById(R.id.btn_mini_play);
        tvMiniTitle = findViewById(R.id.tv_mini_song_title);
        tvMiniArtist = findViewById(R.id.tv_mini_artist);
        imgMiniCover = findViewById(R.id.img_mini_cover);

        miniPlayerContainer.setOnClickListener(v -> {
            if (currentSong != null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.fragment_container, new PlayerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnMiniPlay.setOnClickListener(v -> togglePlayPause());

        bottomNavigationView.setSelectedItemId(R.id.nav_audio);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            androidx.fragment.app.Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_audio) selectedFragment = new SongFragment();
            else if (itemId == R.id.nav_settings) selectedFragment = new SettingsFragment();
            else if (itemId == R.id.nav_playlists) selectedFragment = new PlaylistFragment();

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

    }

    public void playSong(Song song) {
        try {
            currentSong = song;

            tvMiniTitle.setText(song.getTitle());
            tvMiniArtist.setText(song.getArtist());
            imgMiniCover.setImageURI(song.getAlbumArtUri());

            if (imgMiniCover.getDrawable() == null) {
                imgMiniCover.setImageResource(R.drawable.ic_music_note);
            }

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = new android.media.MediaPlayer();
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            isPlaying = true;
            btnMiniPlay.setImageResource(R.drawable.ic_pause);

            mediaPlayer.setOnCompletionListener(mp -> {
                if (isRepeatOn) {
                    mediaPlayer.start();
                } else {
                    nextSong();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (isPlaying) {
                mediaPlayer.pause();
                btnMiniPlay.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                btnMiniPlay.setImageResource(R.drawable.ic_pause);
            }
            isPlaying = !isPlaying;
        }
    }

    public void nextSong() {
        if (songList.isEmpty()) return;

        if (isShuffleOn) {
            currentSongIndex = new Random().nextInt(songList.size());
        } else {
            currentSongIndex = (currentSongIndex + 1) % songList.size();
        }
        playSong(songList.get(currentSongIndex));
    }

    public void previousSong() {
        if (songList.isEmpty()) return;

        currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
        playSong(songList.get(currentSongIndex));
    }

    public void toggleShuffle() {
        isShuffleOn = !isShuffleOn;
    }

    public void toggleRepeat() {
        isRepeatOn = !isRepeatOn;
    }

    public void setSongList(List<Song> songs, int startIndex) {
        this.songList = songs;
        this.currentSongIndex = startIndex;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public android.media.MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public boolean isAudioPlaying() {
        return isPlaying;
    }

    public boolean isShuffleOn() {
        return isShuffleOn;
    }

    public boolean isRepeatOn() {
        return isRepeatOn;
    }
}