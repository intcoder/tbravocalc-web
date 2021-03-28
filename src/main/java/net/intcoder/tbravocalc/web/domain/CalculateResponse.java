package net.intcoder.tbravocalc.web.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class CalculateResponse {
    @Id
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String responseString;

    private LocalDateTime responseTime = LocalDateTime.now();
}
