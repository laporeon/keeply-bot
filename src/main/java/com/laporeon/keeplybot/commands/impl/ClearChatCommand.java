package com.laporeon.keeplybot.commands.impl;

import com.laporeon.keeplybot.commands.SlashCommand;
import com.laporeon.keeplybot.helpers.LanguageManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.util.concurrent.atomic.AtomicInteger;

public class ClearChatCommand implements SlashCommand {
    private static final String COMMAND_NAME = "clear";
    private static final String AMOUNT_OPTION = "amount";
    private static final int DEFAULT_AMOUNT = 100;
    private static final int MAX_AMOUNT = 100;
    private static final ErrorHandler IGNORE_UNKNOWN_MESSAGE =
            new ErrorHandler().ignore(ErrorResponse.UNKNOWN_MESSAGE);

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, "Remove up to 100 recent messages from your DM.")
                       .setContexts(InteractionContextType.BOT_DM)
                       .addOptions(
                               new OptionData(
                                       OptionType.INTEGER,
                                       AMOUNT_OPTION,
                                       "Number of messages to delete (1-%d, default %d)".formatted(MAX_AMOUNT, DEFAULT_AMOUNT)
                               )
                                       .setRequiredRange(1, MAX_AMOUNT)
                       );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        int amount = event.getOption(AMOUNT_OPTION, DEFAULT_AMOUNT, OptionMapping::getAsInt);
        DiscordLocale locale = event.getUserLocale();
        event.deferReply(true).queue(hook -> clearMessages(event, hook, amount, locale));
    }

    private void clearMessages(SlashCommandInteractionEvent event, InteractionHook hook, int amount, DiscordLocale locale) {
        AtomicInteger deleted = new AtomicInteger();
        User self = event.getJDA().getSelfUser();

        event.getUser()
             .openPrivateChannel()
             .submit()
             .thenCompose(dm -> dm.getIterableHistory().forEachAsync(message -> {
                 if (!message.getAuthor().equals(self)) return true;

                 message.delete().queue(null, IGNORE_UNKNOWN_MESSAGE);
                 return deleted.incrementAndGet() < amount;
             }))
             .thenRun(() -> hook.editOriginal(summary(deleted.get(), locale)).queue())
             .exceptionally(failure -> {
                 String clearFailureMessage = LanguageManager.get(locale, "clear.failure.message");
                 hook.editOriginal(clearFailureMessage).queue();
                 return null;
             });
    }

    private static String summary(int deleted, DiscordLocale locale) {
        return deleted == 0
                ? LanguageManager.get(locale, "clear.empty.message")
                : LanguageManager.get(locale, "clear.success.message", deleted);
    }
}
