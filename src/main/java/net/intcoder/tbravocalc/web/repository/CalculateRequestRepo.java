package net.intcoder.tbravocalc.web.repository;

import net.intcoder.tbravocalc.web.domain.CalculateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CalculateRequestRepo extends JpaRepository<CalculateRequest, UUID> {
}
