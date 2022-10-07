package com.hydratereminder.command.prev;

import com.hydratereminder.HydrateReminderPlugin;
import com.hydratereminder.chat.ChatMessageSender;
import com.hydratereminder.command.CommandHandler;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Singleton
public class PrevCommandHandler implements CommandHandler {

    private final transient ChatMessageSender chatMessageSender;
    private final transient HydrateReminderPlugin hydrateReminderPlugin;
    private final transient Clock clock;


    @Inject
    public PrevCommandHandler(ChatMessageSender chatMessageSender, HydrateReminderPlugin hydrateReminderPlugin, Clock clock) {
        this.chatMessageSender = chatMessageSender;
        this.hydrateReminderPlugin = hydrateReminderPlugin;
        this.clock = clock;
    }


    /**
     * <p>Handle the hydrate prev command by generating a chat message displaying the amount of time
     * since the last hydration break
     * </p>
     */
    @Override
    public void handle() {
        final Optional<Duration> timeSinceLastBreak = hydrateReminderPlugin
                .getDurationSinceLastBreak(hydrateReminderPlugin.getLastHydrateInstant(), Instant.now(clock));
        final String message = formatHandleHydratePrevCommand(timeSinceLastBreak);
        chatMessageSender.sendHydrateEmojiChatGameMessage(message);
    }


    /**
     * <p>Handle the format of the message of hydrate prev command.
     * </p>
     *
     * @param timeSinceLastBreak Optional with duration from last break till now, if not it is empty.
     * @return messageFormat generated by handling hydratePrevCommand.
     */
    protected String formatHandleHydratePrevCommand(Optional<Duration> timeSinceLastBreak) {
        if (timeSinceLastBreak.isPresent()) {
            final String timeString = hydrateReminderPlugin.getTimeDisplay(timeSinceLastBreak.get());
            if (hydrateReminderPlugin.isResetState()) {
                return timeString + " since the last hydration interval reset.";
            }
            return timeString + " since the last hydration break.";
        }
        return "No hydration breaks have been taken yet.";
    }

}
