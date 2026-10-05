package com.laporeon.keeplybot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class ReactionListener extends ListenerAdapter  {
    private static final Logger log = LoggerFactory.getLogger(ReactionListener.class);
    private static final String TARGET_EMOJI = "⭐";
    private static final String DELETE_BUTTON_ID = "delete_saved_message";
    private static final int FAILURE_MESSAGE_TIMEOUT = 15;
    private static final String SEND_FAILURE_MESSAGE = """
        %s, %s couldn't send you a DM.

        Please enable direct messages from this server and try again.

        -# NOTE: This message will be deleted in %d seconds.
        """;
    private static final String DELETE_FAILURE_MESSAGE = """
            Couldn't delete this saved message.

            Please try again later.

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
                         .setColor(Color.DARK_GRAY)
                         .setAuthor(guild.getName() + " > " + channelName, null, guild.getIconUrl())
                         .setDescription(message.getContentRaw() + "\u200B");

                 return user.openPrivateChannel()
                            .flatMap(dm -> dm.sendMessageEmbeds(embed.build())
                                             .addComponents(ActionRow.of(
                                                     Button.danger(DELETE_BUTTON_ID, "\uD83D\uDDD1️ Delete"),
                                                     Button.link(message.getJumpUrl(), "\uD83D\uDD17 Open original")
                                             ))
                            );
             })
             .queue(
                     null,
                     failure -> handleSendFailure(event, user, failure)
             );
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (!event.getComponentId().equals(DELETE_BUTTON_ID)) return;

        event.deferEdit()
             .flatMap(InteractionHook::deleteOriginal)
             .queue(
                     null,
                     new ErrorHandler()
                             .ignore(ErrorResponse.UNKNOWN_MESSAGE)
                             .andThen(failure -> notifyDeleteFailure(event))
             );
    }

    private void handleSendFailure(MessageReactionAddEvent event, User user, Throwable failure) {
        log.warn("failed to send DM to user={} | error={} | timestamp={}", user.getId(), failure.getMessage(), Instant.now());

        String failureMessage = SEND_FAILURE_MESSAGE.formatted(
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

    private void notifyDeleteFailure(ButtonInteractionEvent event) {
        String failureMessage = DELETE_FAILURE_MESSAGE.formatted(FAILURE_MESSAGE_TIMEOUT);

        InteractionHook hook = event.getHook();
        hook.sendMessage(failureMessage)
            .queue(msg -> hook.deleteMessageById(msg.getId())
                              .queueAfter(FAILURE_MESSAGE_TIMEOUT, TimeUnit.SECONDS));
    }
}
