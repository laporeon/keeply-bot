package com.laporeon.keeplybot.helpers;

import net.dv8tion.jda.api.interactions.DiscordLocale;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static final String BUNDLE_NAME = "messages";

    public static String get(DiscordLocale discordLocale, String key, Object ...args) {
        Locale locale = discordLocale == DiscordLocale.PORTUGUESE_BRAZILIAN
                ? Locale.of("pt", "BR")
                : Locale.ENGLISH;

        ResourceBundle properties = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        String property = properties.getString(key);

        return args.length > 0 ? String.format(property, args) : property;
    }
}
