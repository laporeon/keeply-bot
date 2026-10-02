package com.laporeon.keeplybot.config;

import com.laporeon.keeplybot.commands.SlashCommand;
import com.laporeon.keeplybot.commands.impl.PingCommand;
import com.laporeon.keeplybot.listeners.CommandListener;
import com.laporeon.keeplybot.listeners.ReadyEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

public class BotConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BotConfiguration.class);
    private static final List<SlashCommand> COMMANDS = List.of(
            new PingCommand()
    );

    public static void start() throws InterruptedException {
        EnvironmentConfiguration.validate();

        JDA jda = JDABuilder.createDefault(EnvironmentConfiguration.token())
                  .addEventListeners(new ReadyEventListener(), new CommandListener(COMMANDS))
                  .build()
                  .awaitReady();

        registerCommands(jda);
    }

    private static void registerCommands(JDA jda) {
        jda.getGuilds()
           .getFirst()
           .updateCommands()
           .addCommands(
                   COMMANDS.stream()
                           .map(SlashCommand::getCommandData)
                           .toList()
           )
           .queue();

        log.info(
                "{} slash commands registered | timestamp={}",
                COMMANDS.size(),
                Instant.now()
        );
    }
}
