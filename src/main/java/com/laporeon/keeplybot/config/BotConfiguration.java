package com.laporeon.keeplybot.config;

import com.laporeon.keeplybot.commands.SlashCommand;
import com.laporeon.keeplybot.commands.impl.ClearChatCommand;
import com.laporeon.keeplybot.commands.impl.HelpCommand;
import com.laporeon.keeplybot.commands.impl.PingCommand;
import com.laporeon.keeplybot.exceptions.DiscordException;
import com.laporeon.keeplybot.listeners.ButtonListener;
import com.laporeon.keeplybot.listeners.CommandListener;
import com.laporeon.keeplybot.listeners.ReactionListener;
import com.laporeon.keeplybot.listeners.ReadyEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction;
import net.dv8tion.jda.api.interactions.commands.localization.ResourceBundleLocalizationFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

public class BotConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BotConfiguration.class);
    private static final String GUILD_NOT_FOUND_MESSAGE = "Development guild '%s' was not found. " +
            "Check DISCORD_GUILD_ID configuration and try again.";
    private static final String BUNDLE_NAME = "messages";
    private static final LocalizationFunction LOCALIZATION = ResourceBundleLocalizationFunction
            .fromBundles(BUNDLE_NAME, DiscordLocale.PORTUGUESE_BRAZILIAN)
            .build();
    private static final List<SlashCommand> COMMANDS = List.of(
            new PingCommand(),
            new HelpCommand(),
            new ClearChatCommand()
    );

    public static void start() throws InterruptedException {
        EnvironmentConfiguration.validate();

        JDA jda = JDABuilder.createDefault(EnvironmentConfiguration.token())
                  .addEventListeners(
                          new ReadyEventListener(),
                          new CommandListener(COMMANDS),
                          new ReactionListener(),
                          new ButtonListener()
                          )
                  .build()
                  .awaitReady();

        try {
            registerCommands(jda);
        } catch (RuntimeException ex) {
            log.error("failed to register commands, shutting down jda | error={} | timestamp={}", ex.getMessage(), Instant.now());
            jda.shutdownNow();
            throw ex;
        }
    }

    private static void registerCommands(JDA jda) {
        List<CommandData> data = COMMANDS.stream()
                                             .map(SlashCommand::getCommandData)
                                             .map(d -> d.setLocalizationFunction(LOCALIZATION))
                                             .toList();

        String profile = EnvironmentConfiguration.profile().toUpperCase(Locale.ROOT);

        if (EnvironmentConfiguration.isDev()) {
            registerDevelopmentCommands(jda, data, profile);
            return;
        }

        jda.updateCommands().addCommands(data).queue(
                registered -> log.info(
                        "{} global commands registered | active_profile={} | timestamp={}",
                        registered.size(), profile, Instant.now()),
                error -> log.error("failed to register global commands", error)
        );
    }

    private static void registerDevelopmentCommands(JDA jda, List<CommandData> data, String profile) {
        String guildId = EnvironmentConfiguration.guildId().get();
        Guild guild = jda.getGuildById(guildId);

        if (guild == null) {
            throw new DiscordException(GUILD_NOT_FOUND_MESSAGE.formatted(guildId));
        }

        guild.updateCommands().addCommands(data).queue(
                cmds -> log.info(
                        "{} commands registered in development mode | guild_id={} | guild_name={} | timestamp={}",
                        cmds.size(), guild.getId(), guild.getName(), Instant.now()),
                error -> log.error("Failed to register development commands in guildId {}", guildId, error)
        );
    }
}
