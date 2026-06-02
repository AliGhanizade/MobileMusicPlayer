package com.mona.music;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment {

    private TextInputEditText etSearch;
    private RecyclerView rvPlaylists;
    private PlaylistAdapter adapter;

    private List<Playlist> allPlaylists = new ArrayList<>();
    private List<Playlist> filteredPlaylists = new ArrayList<>();

    public PlaylistFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearch = view.findViewById(R.id.text_input_search);
        rvPlaylists = view.findViewById(R.id.rv_playlists);

        rvPlaylists.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Seed the default playlists only on the very first launch
        if (!PlaylistStorage.hasData(requireContext())) {
            List<Playlist> defaults = new ArrayList<>();
            defaults.add(new Playlist("Favorites"));
            defaults.add(new Playlist("Chill Beats"));
            PlaylistStorage.save(requireContext(), defaults);
        }

        allPlaylists.addAll(PlaylistStorage.load(requireContext()));
        filteredPlaylists.addAll(allPlaylists);

        adapter = new PlaylistAdapter(filteredPlaylists, new PlaylistAdapter.OnPlaylistClickListener() {
            @Override
            public void onPlaylistClick(Playlist playlist) {
                if (playlist.getSongs().isEmpty()) {
                    Toast.makeText(getContext(), "This playlist is empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (getActivity() instanceof MainActivity) {
                    MainActivity main = (MainActivity) getActivity();
                    main.setSongList(playlist.getSongs(), 0);
                    main.playSong(playlist.getSongs().get(0));
                }
            }

            @Override
            public void onAddPlaylistClick() {
                showCreatePlaylistDialog();
            }
        });
        rvPlaylists.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPlaylists(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterPlaylists(String text) {
        List<Playlist> tempFilteredList = new ArrayList<>();
        String query = text.toLowerCase().trim();

        for (Playlist playlist : allPlaylists) {
            if (playlist.getName().toLowerCase().contains(query)) {
                tempFilteredList.add(playlist);
            }
        }
        adapter.updateList(tempFilteredList);
    }

    private void showCreatePlaylistDialog() {
        final EditText input = new EditText(getContext());
        input.setHint("Enter playlist name...");

        new AlertDialog.Builder(requireContext())
                .setTitle("New Playlist")
                .setView(input)
                .setPositiveButton("Create", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    if (!name.isEmpty()) {
                        allPlaylists.add(new Playlist(name));
                        PlaylistStorage.save(requireContext(), allPlaylists);

                        filterPlaylists(etSearch.getText().toString());
                        Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
