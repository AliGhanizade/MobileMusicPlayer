package com.mona.music;

import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.Locale;

public class PlayerFragment extends Fragment {

    private ImageButton btnClose, btnShuffle, btnPrev, btnNext, btnRepeat;
    private ImageView imgCover;
    private TextView tvTitle, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;
    private FloatingActionButton btnPlay;

    private MainActivity mainActivity;
    private Song currentSong;
    private Handler handler = new Handler();
    private Runnable updateSeekBarRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        btnClose = view.findViewById(R.id.btn_close_player);
        btnShuffle = view.findViewById(R.id.btn_player_shuffle);
        btnPrev = view.findViewById(R.id.btn_player_prev);
        btnPlay = view.findViewById(R.id.btn_player_play);
        btnNext = view.findViewById(R.id.btn_player_next);
        btnRepeat = view.findViewById(R.id.btn_player_repeat);

        imgCover = view.findViewById(R.id.img_player_cover);
        tvTitle = view.findViewById(R.id.tv_player_title);
        tvArtist = view.findViewById(R.id.tv_player_artist);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        seekBar = view.findViewById(R.id.seek_bar_player);

        if (mainActivity != null && mainActivity.getCurrentSong() != null) {
            updateSongDetails(mainActivity.getCurrentSong());
            startSeekBarUpdate();
        }

        btnClose.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        btnPlay.setOnClickListener(v -> {
            if (mainActivity != null) {
                mainActivity.togglePlayPause();
                updatePlayButtonIcon();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (mainActivity != null) mainActivity.nextSong();
        });

        btnPrev.setOnClickListener(v -> {
            if (mainActivity != null) mainActivity.previousSong();
        });

        btnShuffle.setOnClickListener(v -> {
            if (mainActivity != null) {
                mainActivity.toggleShuffle();
                updateShuffleRepeatStatus();
            }
        });

        btnRepeat.setOnClickListener(v -> {
            if (mainActivity != null) {
                mainActivity.toggleRepeat();
                updateShuffleRepeatStatus();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mainActivity != null && mainActivity.getMediaPlayer() != null) {
                    mainActivity.getMediaPlayer().seekTo(progress);
                }
                tvCurrentTime.setText(formatTime(progress));
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateSongDetails(Song song) {
        currentSong = song;
        tvTitle.setText(song.getTitle());
        tvArtist.setText(song.getArtist());
        imgCover.setImageURI(song.getAlbumArtUri());

        if (imgCover.getDrawable() == null) {
            imgCover.setImageResource(R.drawable.ic_music_note);
        }

        updatePlayButtonIcon();
        updateShuffleRepeatStatus();

        if (mainActivity.getMediaPlayer() != null) {
            seekBar.setMax(mainActivity.getMediaPlayer().getDuration());
            tvTotalTime.setText(song.getFormattedDuration());
        }
    }

    private void updatePlayButtonIcon() {
        if (mainActivity != null) {
            if (mainActivity.isAudioPlaying()) {
                btnPlay.setImageResource(R.drawable.ic_pause);
            } else {
                btnPlay.setImageResource(R.drawable.ic_play);
            }
        }
    }

    private void updateShuffleRepeatStatus() {
        if (mainActivity == null || getContext() == null) return;

        int activeColor = resolveThemeColor(android.R.attr.colorPrimary);
        int inactiveColor = resolveThemeColor(com.google.android.material.R.attr.colorOnSurfaceVariant);

        btnShuffle.setColorFilter(mainActivity.isShuffleOn() ? activeColor : inactiveColor);
        btnRepeat.setColorFilter(mainActivity.isRepeatOn() ? activeColor : inactiveColor);
    }

    private int resolveThemeColor(int attrResId) {
        TypedValue typedValue = new TypedValue();
        if (requireContext().getTheme().resolveAttribute(attrResId, typedValue, true)) {
            return typedValue.data;
        }
        return 0;
    }

    private void startSeekBarUpdate() {
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if (mainActivity != null && mainActivity.getMediaPlayer() != null) {
                    if (mainActivity.getCurrentSong() != null &&
                            (currentSong == null || mainActivity.getCurrentSong().getId() != currentSong.getId())) {
                        updateSongDetails(mainActivity.getCurrentSong());
                    }

                    if (mainActivity.isAudioPlaying()) {
                        int currentPosition = mainActivity.getMediaPlayer().getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        tvCurrentTime.setText(formatTime(currentPosition));
                    }
                    mainActivity.updateMiniPlayerProgress();
                    updatePlayButtonIcon();
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updateSeekBarRunnable);
    }

    private String formatTime(int milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateSeekBarRunnable);
    }
}