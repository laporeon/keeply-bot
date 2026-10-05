package com.laporeon.keeplybot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ReactionListener extends ListenerAdapter  {
    private static final Logger log = LoggerFactory.getLogger(ReactionListener.class);
    private static final String TARGET_EMOJI = "⭐";
    private static final int FAILURE_MESSAGE_TIMEOUT = 15;
    private static final String FAILURE_MESSAGE = """
        %s, %s couldn't send you a DM.

        Please enable direct messages from this server and try again.

        -# NOTE: This message will be deleted in %d seconds.
        """;

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        if (user == null || user.isBot() || !event.isFromGuild()) return;

        String reactionEmoji = event.getEmoji().getAsReactionCode();
        if (!reactionEmoji.equals(TARGET_EMOJI)) return;

        Guild guild = event.getGuild();
        String channelName = event.getChannel().getName();

        event.retrieveMessage()
             .flatMap(message -> {
                 EmbedBuilder embed = new EmbedBuilder()
                         .setColor(Color.CYAN)
                         .setAuthor(guild.getName() + " > " + channelName, null, guild.getIconUrl())
                         .setDescription(message.getContentRaw() + "\u200B");

                 return user.openPrivateChannel()
                            .flatMap(dm -> dm.sendMessageEmbeds(embed.build()));
             })
             .queue(
                     null,
                     failure -> handleSendFailure(event, user, failure)
             );
    }

    private void handleSendFailure(MessageReactionAddEvent event, User user, Throwable failure) {
        log.warn("failed to send DM to user={} | error={} | timestamp={}", user.getId(), failure.getMessage(), Instant.now());

        String failureMessage = FAILURE_MESSAGE.formatted(
                user.getAsMention(),
                event.getJDA().getSelfUser().getName(),
                FAILURE_MESSAGE_TIMEOUT
        );

        event.getChannel()
             .sendMessage(failureMessage)
             .setMessageReference(event.getMessageId())
             .queue(message -> {
                 message.delete().queueAfter(FAILURE_MESSAGE_TIMEOUT, TimeUnit.SECONDS);
             });
    }
}
