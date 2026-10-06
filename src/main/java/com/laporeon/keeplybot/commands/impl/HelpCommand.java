package com.laporeon.keeplybot.commands.impl;

import com.laporeon.keeplybot.commands.SlashCommand;
import com.laporeon.keeplybot.helpers.LanguageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;

public class HelpCommand implements SlashCommand {
    private static final String COMMAND_NAME = "help";

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Learn how to use the bot");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;

        String description = LanguageManager.get(event.getUserLocale(), "help.embed.description");

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.DARK_GRAY)
                .setDescription(description);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
