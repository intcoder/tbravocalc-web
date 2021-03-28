package net.intcoder.tbravocalc.web.domain;

import lombok.Data;
import net.intcoder.tbravocalc.web.dto.PrintType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class CalculateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(columnDefinition = "TEXT")
    private String spreadsheetJson;
    private double target;
    private Long depth;
    @Enumerated(value = EnumType.STRING)
    private PrintType printType;

    private LocalDateTime requestTime = LocalDateTime.now();
}
