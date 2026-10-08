package com.laporeon.keeplybot.listeners;

import com.laporeon.keeplybot.helpers.LanguageManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.DiscordLocale;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ReactionListener extends ListenerAdapter  {
    private static final Logger log = LoggerFactory.getLogger(ReactionListener.class);
    private static final String TARGET_EMOJI = "⭐";
    private static final int FAILURE_MESSAGE_TIMEOUT = 15;
    private static final Set<ErrorResponse> DM_UNAVAILABLE = EnumSet.of(
            ErrorResponse.CANNOT_SEND_TO_USER,
            ErrorResponse.NO_MUTUAL_GUILDS
    );

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        if (user == null || user.isBot() || !event.isFromGuild()) return;
        if (!TARGET_EMOJI.equals(event.getEmoji().getAsReactionCode())) return;

        Guild guild = event.getGuild();
        DiscordLocale locale = guild.getLocale();

        event.retrieveMessage()
             .flatMap(message -> user.openPrivateChannel()
                                     .flatMap(dm -> dm.sendMessageEmbeds(buildSavedEmbed(event, message))
                                                      .addComponents(buildActions(locale, message))))
             .queue(null, failure -> handleSendFailure(event, user, locale, failure));
    }

    private MessageEmbed buildSavedEmbed(MessageReactionAddEvent event, Message message) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.DARK_GRAY)
                .setAuthor(event.getGuild().getName() + "\u2002›\u2002" + event.getChannel().getName(),
                        message.getJumpUrl(), event.getGuild().getIconUrl())
                .setDescription(message.getContentRaw());

        message.getAttachments().stream()
               .filter(Message.Attachment::isImage)
               .findFirst()
               .ifPresent(a -> embed.setImage(a.getUrl()));

        return embed.build();
    }

    private ActionRow buildActions(DiscordLocale locale, Message message) {
        return ActionRow.of(
                Button.danger("delete_saved_message", LanguageManager.get(locale, "buttons.delete.cta.label")),
                Button.link(message.getJumpUrl(), LanguageManager.get(locale, "buttons.link.cta.label")));
    }

    private void handleSendFailure(MessageReactionAddEvent event, User user,
                               DiscordLocale locale, Throwable failure) {
        if (failure instanceof ErrorResponseException ex
                && DM_UNAVAILABLE.contains(ex.getErrorResponse())) {
            log.warn("failed to send saved message to user={} due to privacy settings | reason={}", user.getId(), ex.getErrorResponse());
            notifyUser(event, user, locale, "reactions.send.failure.message");
            return;
        }

        log.error("failed to send saved message for user={}", user.getId(), failure);
        notifyUser(event, user, locale, "reactions.send.unexpected.failure.message");
    }

    private void notifyUser(MessageReactionAddEvent event, User user,
                            DiscordLocale locale, String key) {
        String text = LanguageManager.get(
                locale, key,
                user.getAsMention(),
                event.getJDA().getSelfUser().getName(),
                FAILURE_MESSAGE_TIMEOUT);

        event.getChannel()
             .sendMessage(text)
             .setMessageReference(event.getMessageId())
             .queue(message -> {
                 message.delete().queueAfter(FAILURE_MESSAGE_TIMEOUT, TimeUnit.SECONDS);
             });
    }
}
