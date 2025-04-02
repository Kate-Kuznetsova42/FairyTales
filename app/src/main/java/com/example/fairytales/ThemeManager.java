package com.example.fairytales;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeManager {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "theme";
    private static final String KEY_TEXT_SIZE = "text_size";

    // Доступные темы
    public static final String THEME_LIGHT = "Theme.Light";
    public static final String THEME_SEPIA = "Theme.Sepia";
    public static final String THEME_BLUE = "Theme.Blue";
    public static final String THEME_GREEN = "Theme.Green";

    // Доступные размеры текста
    public static final String TEXT_SIZE_25 = "SizeText25";
    public static final String TEXT_SIZE_18 = "SizeText18";
    public static final String TEXT_SIZE_16 = "SizeText16";
    public static final String TEXT_SIZE_DEF_14 = "SizeTextDef14";
    public static void applyTheme (Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, "Theme.Light");
        String textSize = prefs.getString(KEY_TEXT_SIZE, "SizeTextDef14");

//         Применяем тему
        switch (theme){
            case THEME_LIGHT:
                context.setTheme(R.style.Theme_Light);
                break;
            case THEME_BLUE:
                context.setTheme(R.style.Theme_Blue);
                break;
            case THEME_GREEN:
                context.setTheme(R.style.Theme_Green);
                break;
            case THEME_SEPIA:
                context.setTheme(R.style.Theme_Sepia);
                break;

        }

        // Применяем размер текста
        switch (textSize){
            case TEXT_SIZE_DEF_14:
                context.setTheme(R.style.SizeTextDef14);
                break;
            case TEXT_SIZE_16:
                context.setTheme(R.style.SizeText16);
                break;
            case TEXT_SIZE_18:
                context.setTheme(R.style.SizeText18);
                break;
            case TEXT_SIZE_25:
                context.setTheme(R.style.SizeText25);
                break;
        }
    }
    public static void saveTheme(Context context, String theme){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_THEME, theme).apply();
    }
    public static void saveTextSize(Context context, String textSize){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TEXT_SIZE, textSize).apply();
    }
}
