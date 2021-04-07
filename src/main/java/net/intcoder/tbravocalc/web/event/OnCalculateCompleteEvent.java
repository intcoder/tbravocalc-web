package net.intcoder.tbravocalc.web.event;

import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class OnCalculateCompleteEvent  extends ApplicationEvent {
    public OnCalculateCompleteEvent(UUID requestId) {
        super(requestId);
    }

    public UUID getRequestId() {
        return (UUID) getSource();
    }
}
