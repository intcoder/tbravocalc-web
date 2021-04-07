package net.intcoder.tbravocalc.web.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CalculateCompleteListener implements ApplicationListener<OnCalculateCompleteEvent> {

    @Override
    public void onApplicationEvent(OnCalculateCompleteEvent event) {
        var requestId = event.getRequestId();
        log.info("Calculate result {} is ready!", requestId);
    }
}