package com.laporeon.keeplybot.listeners;

import com.laporeon.keeplybot.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandListener extends ListenerAdapter {
    private final List<SlashCommand> slashCommands;

    public CommandListener(List<SlashCommand> slashCommands) {
        this.slashCommands = slashCommands;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        slashCommands.stream()
                .filter(command -> command.getName().equals(event.getName()))
                .findFirst()
                .ifPresent(command -> command.execute(event));
    }
}
