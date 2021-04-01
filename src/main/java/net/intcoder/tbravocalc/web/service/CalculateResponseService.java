package net.intcoder.tbravocalc.web.service;

import net.intcoder.tbravocalc.web.domain.CalculateResponse;
import net.intcoder.tbravocalc.web.event.OnCalculateCompleteEvent;
import net.intcoder.tbravocalc.web.repository.CalculateResponseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CalculateResponseService {

    @Autowired
    private CalculateResponseRepo repo;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void saveResponse(
            UUID requestId,
            String textResponse,
            boolean success) {

        var res = new CalculateResponse();
        res.setId(requestId);
        res.setResponseString(textResponse);
        res.setSuccess(success);
        res = repo.save(res);

        eventPublisher.publishEvent(new OnCalculateCompleteEvent(res.getId()));
    }

    public Optional<CalculateResponse> find(UUID id) {
        return repo.findById(id);
    }

    public boolean exists(UUID id) {
        return repo.existsById(id);
    }
}
