package net.intcoder.tbravocalc.web.event;

import net.intcoder.tbravocalc.web.service.ExternalCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CalculateRequestListener implements ApplicationListener<OnCalculateRequestEvent> {

    @Autowired
    private ExternalCalculatorService externalCalculatorService;

    @Override
    public void onApplicationEvent(OnCalculateRequestEvent event) {
        var requestId = event.getRequestId();

        externalCalculatorService.process(requestId);
    }
}
