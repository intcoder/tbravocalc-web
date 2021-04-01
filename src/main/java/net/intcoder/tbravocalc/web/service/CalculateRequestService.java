package net.intcoder.tbravocalc.web.service;

import net.intcoder.tbravocalc.web.domain.CalculateRequest;
import net.intcoder.tbravocalc.web.dto.PrintType;
import net.intcoder.tbravocalc.web.dto.Spreadsheet;
import net.intcoder.tbravocalc.web.event.OnCalculateRequestEvent;
import net.intcoder.tbravocalc.web.repository.CalculateRequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

@Service
public class CalculateRequestService {

    @Autowired
    private CalculateRequestRepo repo;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public UUID saveRequest(
            double target,
            OptionalLong depth,
            PrintType printType,
            Spreadsheet spreadsheet) {

        var req = new CalculateRequest();
        req.setSpreadsheetJson(spreadsheet.toString());
        req.setTarget(target);
        req.setDepth(depth.isPresent() ? depth.getAsLong() : null);
        req.setPrintType(printType);

        req = repo.save(req);

        eventPublisher.publishEvent(new OnCalculateRequestEvent(req.getId()));

        return req.getId();
    }

    public CalculateRequest get(UUID id) {
        return repo.findById(id).orElseThrow();
    }

    public Optional<CalculateRequest> find(UUID id) {
        return repo.findById(id);
    }

    public boolean exists(UUID id) {
        return repo.existsById(id);
    }
}
