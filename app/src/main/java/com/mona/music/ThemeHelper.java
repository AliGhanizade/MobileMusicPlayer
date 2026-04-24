package com.mona.music;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "selected_theme";

    public static final String THEME_DEFAULT = "Default";
    public static final String THEME_BLUE = "Blue";
    public static final String THEME_PINK = "Pink";

    // Save selected theme
    public static void setTheme(Context context, String theme) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    // Get current theme
    public static String getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_THEME, THEME_DEFAULT);
    }

    // Apply theme to Activity
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
}
