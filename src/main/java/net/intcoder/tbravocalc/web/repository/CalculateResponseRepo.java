package net.intcoder.tbravocalc.web.repository;

import net.intcoder.tbravocalc.web.domain.CalculateResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CalculateResponseRepo extends JpaRepository<CalculateResponse, UUID> {
}

