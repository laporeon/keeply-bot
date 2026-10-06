package com.laporeon.keeplybot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.ErrorResponse;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class ButtonListener extends ListenerAdapter {
    private static final int FAILURE_MESSAGE_TIMEOUT = 15;
    private static final String DELETE_BUTTON_ID = "delete_saved_message";
    private static final String DELETE_FAILURE_MESSAGE = """
            Couldn't delete this saved message.

            Please try again later.

            -# NOTE: This message will be deleted in %d seconds.
            """;

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

    private void notifyDeleteFailure(ButtonInteractionEvent event) {
        String failureMessage = DELETE_FAILURE_MESSAGE.formatted(FAILURE_MESSAGE_TIMEOUT);

        InteractionHook hook = event.getHook();
        hook.sendMessage(failureMessage)
            .queue(msg -> hook.deleteMessageById(msg.getId())
                              .queueAfter(FAILURE_MESSAGE_TIMEOUT, TimeUnit.SECONDS));
    }
}
