package com.laporeon.keeplybot.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public class EnvironmentConfiguration {
    private static final Dotenv ENV = Dotenv.configure().ignoreIfMissing().load();

    public static String token() {
        return require("DISCORD_TOKEN");
    }

    public static void validate() {
        token();
    }

    private static String require(String key) {
        return get(key)
                .orElseThrow(() -> new IllegalStateException(
                        "Required environment variable '" + key + "' is not configured."
                ));
    }

    private static Optional<String> get(String key) {
        return Optional.ofNullable(System.getenv(key))
                       .filter(value -> !value.isBlank())
                       .or(() -> Optional.ofNullable(ENV.get(key)))
                       .filter(value -> !value.isBlank());
    }
}
