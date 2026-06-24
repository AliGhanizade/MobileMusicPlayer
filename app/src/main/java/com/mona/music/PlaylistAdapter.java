package com.mona.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_PLAYLIST = 0;
    private static final int TYPE_ADD_BUTTON = 1;

    private List<Playlist> playlists;
    private OnPlaylistClickListener clickListener;

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
        void onAddPlaylistClick();
        void onPlaylistLongClick(Playlist playlist);
    }

    public PlaylistAdapter(List<Playlist> playlists, OnPlaylistClickListener clickListener) {
        this.playlists = playlists;
        this.clickListener = clickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == playlists.size()) {
            return TYPE_ADD_BUTTON;
        }
        return TYPE_PLAYLIST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_ADD_BUTTON) {
            View view = inflater.inflate(R.layout.item_playlist_add, parent, false);
            return new AddViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_playlist, parent, false);
            return new PlaylistViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PlaylistViewHolder) {
            Playlist playlist = playlists.get(position);
            PlaylistViewHolder playlistHolder = (PlaylistViewHolder) holder;
            playlistHolder.tvName.setText(playlist.getName());

            playlistHolder.itemView.setOnClickListener(v -> clickListener.onPlaylistClick(playlist));
            playlistHolder.itemView.setOnLongClickListener(v -> {
                clickListener.onPlaylistLongClick(playlist);
                return true;
            });
        } else if (holder instanceof AddViewHolder) {
            holder.itemView.setOnClickListener(v -> clickListener.onAddPlaylistClick());
        }
    }

    @Override
    public int getItemCount() {
        return playlists.size() + 1;
    }

    public void updateList(List<Playlist> newList) {
        this.playlists = newList;
        notifyDataSetChanged();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_playlist_name);
        }
    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
