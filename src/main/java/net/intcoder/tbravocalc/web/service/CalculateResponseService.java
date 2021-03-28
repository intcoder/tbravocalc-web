package net.intcoder.tbravocalc.web.service;

import net.intcoder.tbravocalc.web.domain.CalculateResponse;
import net.intcoder.tbravocalc.web.repository.CalculateResponseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CalculateResponseService {

    @Autowired
    private CalculateResponseRepo repo;

    public void saveResponse(
            UUID requestId,
            String textResponse) {

        var res = new CalculateResponse();
        res.setId(requestId);
        res.setResponseString(textResponse);
        repo.save(res);
    }

    public Optional<CalculateResponse> find(UUID id) {
        return repo.findById(id);
    }
}
