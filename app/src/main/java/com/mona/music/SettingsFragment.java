package com.mona.music;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    private TextView tvCurrentTheme;
    private LinearLayout layoutTheme;
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
        layoutTheme = view.findViewById(R.id.layout_theme);
        layoutEqualizer = view.findViewById(R.id.layout_equalizer);

        String currentTheme = ThemeHelper.getTheme(requireContext());
        tvCurrentTheme.setText(currentTheme);
        layoutTheme.setOnClickListener(v -> showThemeDialog());

        layoutEqualizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Equalizer opens here", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showThemeDialog() {
        String[] themes = {"Default", "Blue", "Pink"};
        String currentTheme = ThemeHelper.getTheme(requireContext());

        int checkedItem = 0;
        if (currentTheme.equals("Blue")) checkedItem = 1;
        if (currentTheme.equals("Pink")) checkedItem = 2;

        new AlertDialog.Builder(requireContext())
                .setTitle("Choose Theme")
                .setSingleChoiceItems(themes, checkedItem, (dialog, which) -> {
                    String selectedTheme = "Green";
                    if (which == 1) selectedTheme = "Blue";
                    if (which == 2) selectedTheme = "Pink";

                    ThemeHelper.setTheme(requireContext(), selectedTheme);
                    dialog.dismiss();

                    requireActivity().recreate();
                })
                .show();
    }
}