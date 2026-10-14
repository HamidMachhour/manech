package com.manech.manechapp.helper;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public final class LocaleHelper {
    private static final String PREFERENCES = "manech_language_settings";
    private static final String LANGUAGE_KEY = "language";
    public static final String ENGLISH = "en";
    public static final String ARABIC = "ar";

    private LocaleHelper() {
    }

    public static String getLanguage(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .getString(LANGUAGE_KEY, ENGLISH);
    }

    public static void setLanguage(Context context, String language) {
        if (!ENGLISH.equals(language) && !ARABIC.equals(language)) {
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(LANGUAGE_KEY, language)
                .apply();
    }

    public static Context wrap(Context context) {
        Locale locale = Locale.forLanguageTag(getLanguage(context));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }
}
