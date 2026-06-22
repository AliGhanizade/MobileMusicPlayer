package com.mona.music;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SongFragment extends Fragment {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> originalSongList = new ArrayList<>();
    private List<Song> filteredSongList = new ArrayList<>();

    private LinearLayout layoutPermission;
    private Button btnGrantPermission;
    private TabLayout tabLayout;
    private TextInputEditText searchInput;

    private int currentTabPosition = 0;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    showLayouts(true);
                    loadSongs();
                } else {
                    showLayouts(false);
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            });

    public SongFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycler_view_songs);
        layoutPermission = view.findViewById(R.id.layout_permission);
        btnGrantPermission = view.findViewById(R.id.btn_grant_permission);
        tabLayout = view.findViewById(R.id.tab_layout);
        searchInput = view.findViewById(R.id.text_input_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        btnGrantPermission.setOnClickListener(v -> checkPermissionAndLoad());

        if (searchInput != null) {
            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    applyFilter(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTabPosition = tab.getPosition();
                String query = searchInput != null ? searchInput.getText().toString() : "";
                applyFilter(query);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        checkPermissionAndLoad();
    }

    private void checkPermissionAndLoad() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? Manifest.permission.READ_MEDIA_AUDIO
                : Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            showLayouts(true);
            loadSongs();
        } else {
            showLayouts(false);
            requestPermissionLauncher.launch(permission);
        }
    }

    private void showLayouts(boolean hasPermission) {
        if (hasPermission) {
            recyclerView.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            layoutPermission.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            layoutPermission.setVisibility(View.VISIBLE);
        }
    }

    private void loadSongs() {
        originalSongList.clear();
        ContentResolver contentResolver = requireContext().getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND " + MediaStore.Audio.Media.DURATION + " > 30000";

        Cursor cursor = contentResolver.query(songUri, null, selection, null, MediaStore.Audio.Media.TITLE + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int dataColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            int albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                String thisArtist = cursor.getString(artistColumn);
                String thisPath = cursor.getString(dataColumn);
                long thisDuration = cursor.getLong(durationColumn);
                long thisAlbumId = cursor.getLong(albumIdColumn);

                originalSongList.add(new Song(thisId, thisTitle, thisArtist, thisPath, thisDuration, thisAlbumId));
            } while (cursor.moveToNext());
            cursor.close();
        }

        filteredSongList.clear();
        filteredSongList.addAll(originalSongList);

        songAdapter = new SongAdapter(filteredSongList, song -> {
            if (getActivity() instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) getActivity();

                int clickedIndex = filteredSongList.indexOf(song);

                mainActivity.setSongList(filteredSongList, clickedIndex);

                mainActivity.playSong(song);
            }
        }, song -> showAddToPlaylistDialog(song));
        recyclerView.setAdapter(songAdapter);

    }

    private void applyFilter(String query) {
        List<Song> tempFilteredList = new ArrayList<>();
        String lowerCaseQuery = query.toLowerCase().trim();

        for (Song song : originalSongList) {
            boolean matchesSearch = false;

            if (currentTabPosition == 0) {
                matchesSearch = song.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        song.getArtist().toLowerCase().contains(lowerCaseQuery) ;
            } else if (currentTabPosition == 1) {
                matchesSearch = song.getArtist().toLowerCase().contains(lowerCaseQuery);
            }

            if (matchesSearch) {
                tempFilteredList.add(song);
            }
        }

        if (songAdapter != null) {
            songAdapter.updateList(tempFilteredList);
        }
    }

    private void showAddToPlaylistDialog(Song song) {
        List<Playlist> playlists = PlaylistStorage.load(requireContext());

        String[] options = new String[playlists.size() + 1];
        for (int i = 0; i < playlists.size(); i++) {
            options[i] = playlists.get(i).getName();
        }
        options[playlists.size()] = "+ New Playlist";

        new AlertDialog.Builder(requireContext())
                .setTitle("Add \"" + song.getTitle() + "\" to...")
                .setItems(options, (dialog, which) -> {
                    if (which == playlists.size()) {
                        showNewPlaylistDialog(song);
                    } else {
                        addSongToPlaylist(playlists.get(which), song);
                    }
                })
                .show();
    }

    private void showNewPlaylistDialog(Song song) {
        final EditText input = new EditText(getContext());
        input.setHint("Enter playlist name...");

        new AlertDialog.Builder(requireContext())
                .setTitle("New Playlist")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        List<Playlist> playlists = PlaylistStorage.load(requireContext());
                        Playlist playlist = new Playlist(name);
                        playlist.addSong(song);
                        playlists.add(playlist);
                        PlaylistStorage.save(requireContext(), playlists);

                        Toast.makeText(getContext(), "Added to " + name, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addSongToPlaylist(Playlist playlist, Song song) {
        if (playlist.getSongs().contains(song)) {
            Toast.makeText(getContext(), "Already in " + playlist.getName(), Toast.LENGTH_SHORT).show();
            return;
        }

        playlist.addSong(song);
        List<Playlist> playlists = PlaylistStorage.load(requireContext());
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(playlist.getName())) {
                playlists.set(i, playlist);
                break;
            }
        }
        PlaylistStorage.save(requireContext(), playlists);

        Toast.makeText(getContext(), "Added to " + playlist.getName(), Toast.LENGTH_SHORT).show();
    }
}
