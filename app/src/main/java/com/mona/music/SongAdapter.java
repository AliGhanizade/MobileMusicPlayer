package com.mona.music;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private List<Song> songList;

    @Override
    public int getItemCount() {
        return songList.size();
    }

    final private OnSongClickListener songListener;

    public interface OnSongClickListener {
        void onSongClick(Song song);
    }

    public SongAdapter(List<Song> songList, OnSongClickListener listener) {
        this.songList = songList;
        this.songListener = listener;
    }

    public void updateList(List<Song> newList) {
        this.songList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist;
        ImageView imgCover, btnMore;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_song_title);
            tvArtist = itemView.findViewById(R.id.tv_song_artist);
            imgCover = itemView.findViewById(R.id.img_cover);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvTitle.setText(song.getTitle());
        String tvArtistFormat = song.getArtist() + " • " + song.getFormattedDuration();
        holder.tvArtist.setText(tvArtistFormat);
        holder.imgCover.setImageURI(song.getAlbumArtUri());

        // Check for imageCover
        if (holder.imgCover.getDrawable() == null) {
            holder.imgCover.setImageResource(R.drawable.ic_music_note);
        }
        holder.itemView.setOnClickListener(v -> songListener.onSongClick(song));
    }



}