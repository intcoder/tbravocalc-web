package net.intcoder.tbravocalc.web.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

public class OnCalculateRequestEvent extends ApplicationEvent {
    public OnCalculateRequestEvent(UUID requestId) {
        super(requestId);
    }

    public UUID getRequestId() {
        return (UUID) getSource();
    }
}
