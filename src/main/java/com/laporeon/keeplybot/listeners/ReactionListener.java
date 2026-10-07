package com.laporeon.keeplybot.listeners;

import com.laporeon.keeplybot.helpers.LanguageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
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

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        if (user == null || user.isBot() || !event.isFromGuild()) return;

        String reactionEmoji = event.getEmoji().getAsReactionCode();
        if (!reactionEmoji.equals(TARGET_EMOJI)) return;

        Guild guild = event.getGuild();
        DiscordLocale locale = guild.getLocale();
        MessageChannel channel = event.getChannel();

        event.retrieveMessage()
             .flatMap(message -> {
                 EmbedBuilder embed = new EmbedBuilder()
                         .setColor(Color.DARK_GRAY)
                         .setAuthor(guild.getName() + "\u2002›\u2002" + channel.getName(), channel.getJumpUrl(), guild.getIconUrl())
                         .setDescription(message.getContentRaw());

                 return user.openPrivateChannel()
                            .flatMap(dm -> dm.sendMessageEmbeds(embed.build())
                                             .addComponents(ActionRow.of(
                                                     Button.danger(
                                                             DELETE_BUTTON_ID,
                                                             LanguageManager.get(locale, "buttons.delete.cta.label")),
                                                     Button.link(
                                                             message.getJumpUrl(),
                                                             LanguageManager.get(locale, "buttons.link.cta.label"))
                                             ))
                            );
             })
             .queue(null, failure -> handleSendFailure(event, user, locale, failure));
    }

    private void handleSendFailure(MessageReactionAddEvent event, User user,
                                   DiscordLocale locale, Throwable failure) {
        log.warn("failed to send DM to user={} | error={} | timestamp={}", user.getId(), failure.getMessage(), Instant.now());

        String failureMessage = LanguageManager.get(
                locale,
                "reactions.send.failure.message",
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
