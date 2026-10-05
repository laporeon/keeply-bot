package com.laporeon.keeplybot.commands.impl;

import com.laporeon.keeplybot.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class PingCommand implements SlashCommand {
    private static final String COMMAND_NAME = "ping";

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Because ping never gets old");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) return;

        event.reply("PONG! \uD83C\uDFD3").setEphemeral(true).queue();
    }
}
