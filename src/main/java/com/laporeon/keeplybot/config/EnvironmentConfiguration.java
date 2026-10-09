package com.laporeon.keeplybot.config;

import com.laporeon.keeplybot.exceptions.ConfigurationException;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public class EnvironmentConfiguration {
    private static final Dotenv ENV = Dotenv.configure().ignoreIfMissing().load();

    public static String token() {
        return require("DISCORD_TOKEN");
    }

    public static String profile() {
        return require("ACTIVE_PROFILE");
    }

    public static Optional<String> guildId() {
        return get("DISCORD_GUILD_ID");
    }

    public static boolean isDev() {
        return profile().equalsIgnoreCase("dev");
    }

    public static void validate() {
        token();
        profile();

        if (isDev() && guildId().isEmpty()) {
            throw new ConfigurationException("'DISCORD_GUILD_ID' must be set in development mode.");
        }
    }

    private static String require(String key) {
        return get(key)
                .filter(value -> !value.isBlank())
                .orElseThrow(() -> new ConfigurationException(
                        "Required environment variable '%s' is missing or blank.".formatted(key)
                ));
    }

    private static Optional<String> get(String key) {
        return Optional.ofNullable(System.getenv(key))
                       .filter(value -> !value.isBlank())
                       .or(() -> Optional.ofNullable(ENV.get(key)))
                       .filter(value -> !value.isBlank());
    }
}
