package com.mona.music;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";
    private static final String KEY_NIGHT_MODE = "night_mode";

    public static final String THEME_DEFAULT = "Default";
    public static final String THEME_BLUE = "Blue";
    public static final String THEME_PINK = "Pink";

    public static final String NIGHT_SYSTEM = "System";
    public static final String NIGHT_LIGHT = "Light";
    public static final String NIGHT_DARK = "Dark";

    // Save selected color theme
    public static void setTheme(Context context, String theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    // Get current color theme
    public static String getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_THEME, THEME_DEFAULT);
    }

    // Apply color theme to Activity
    public static void applyTheme(Context context) {
        String theme = getTheme(context);

        if (theme.equals(THEME_BLUE)) {
            context.setTheme(R.style.Theme_MusicPlayer_Blue);
        } else if (theme.equals(THEME_PINK)) {
            context.setTheme(R.style.Theme_MusicPlayer_Pink);
        } else {
            context.setTheme(R.style.Theme_MusicPlayer);
        }
    }

    // Save selected night mode
    public static void setNightMode(Context context, String mode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_NIGHT_MODE, mode).apply();
    }

    // Get current night mode
    public static String getNightMode(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_NIGHT_MODE, NIGHT_SYSTEM);
    }

    // Apply night mode globally (auto-recreates activities)
    public static void applyNightMode(Context context) {
        String mode = getNightMode(context);

        int nightMode;
        if (mode.equals(NIGHT_DARK)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_YES;
        } else if (mode.equals(NIGHT_LIGHT)) {
            nightMode = AppCompatDelegate.MODE_NIGHT_NO;
        } else {
            nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }

        if (AppCompatDelegate.getDefaultNightMode() != nightMode) {
            AppCompatDelegate.setDefaultNightMode(nightMode);
        }
    }
}
