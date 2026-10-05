package com.laporeon.keeplybot.commands.impl;

import com.laporeon.keeplybot.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.awt.*;

public class HelpCommand implements SlashCommand {
    private static final String COMMAND_NAME = "help";
    private static final String COMMAND_DESCRIPTION = """
        **How to use:**

        React to any message with ⭐ to receive it in your DMs.

        -# Make sure your Direct Messages are enabled for this server.
        """;

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

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.DARK_GRAY)
                .setDescription(COMMAND_DESCRIPTION);

        event.replyEmbeds(embed.build()).setEphemeral(true).queue();
    }
}
