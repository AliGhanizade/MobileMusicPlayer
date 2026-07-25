package com.mona.music;

import android.content.ActivityNotFoundException;
import android.content.Intent;

import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private TextView tvCurrentTheme;
    private TextView tvCurrentNightMode;
    private LinearLayout layoutTheme;
    private LinearLayout layoutNightMode;
    private LinearLayout layoutEqualizer;

    public SettingsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvCurrentTheme = view.findViewById(R.id.tv_current_theme);
        tvCurrentNightMode = view.findViewById(R.id.tv_current_night_mode);
        layoutTheme = view.findViewById(R.id.layout_theme);
        layoutNightMode = view.findViewById(R.id.layout_night_mode);
        layoutEqualizer = view.findViewById(R.id.layout_equalizer);

        tvCurrentTheme.setText(ThemeHelper.getTheme(requireContext()));
        tvCurrentNightMode.setText(ThemeHelper.getNightMode(requireContext()));

        layoutTheme.setOnClickListener(v -> showThemeDialog());
        layoutNightMode.setOnClickListener(v -> showNightModeDialog());
        layoutEqualizer.setOnClickListener(v -> openSystemEqualizer());
    }

    private void showThemeDialog() {
        String[] themes = {ThemeHelper.THEME_DEFAULT, ThemeHelper.THEME_BLUE, ThemeHelper.THEME_PINK};
        String currentTheme = ThemeHelper.getTheme(requireContext());

        int checkedItem = 0;
        if (currentTheme.equals(ThemeHelper.THEME_BLUE)) checkedItem = 1;
        if (currentTheme.equals(ThemeHelper.THEME_PINK)) checkedItem = 2;

        new AlertDialog.Builder(requireContext())
                .setTitle("Choose Theme")
                .setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
                    ThemeHelper.setTheme(requireContext(), themes[which]);
                    dialog.dismiss();

                    requireActivity().recreate();
                })
                .show();
    }

    private void showNightModeDialog() {
        String[] modes = {ThemeHelper.NIGHT_SYSTEM, ThemeHelper.NIGHT_LIGHT, ThemeHelper.NIGHT_DARK};
        String currentMode = ThemeHelper.getNightMode(requireContext());

        int checkedItem = 0;
        if (currentMode.equals(ThemeHelper.NIGHT_LIGHT)) checkedItem = 1;
        if (currentMode.equals(ThemeHelper.NIGHT_DARK)) checkedItem = 2;

        new AlertDialog.Builder(requireContext())
                .setTitle("Night Mode")
                .setSingleChoiceItems(modes, checkedItem, (dialog, which) -> {
                    ThemeHelper.setNightMode(requireContext(), modes[which]);
                    tvCurrentNightMode.setText(modes[which]);
                    dialog.dismiss();

                    ThemeHelper.applyNightMode(requireContext());
                })
                .show();
    }

    private void openSystemEqualizer() {
        Intent intent = new Intent("android.media.action.DISPLAY_AUDIO_EFFECT_SETTINGS");

        if (getActivity() instanceof MainActivity) {
            MainActivity main = (MainActivity) getActivity();
            if (main.getMediaPlayer() != null) {
                intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, main.getMediaPlayer().getAudioSessionId());
            }
        }

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "No equalizer found on this device", Toast.LENGTH_SHORT).show();
        }
    }
}
