package net.intcoder.tbravocalc.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.SneakyThrows;
import net.intcoder.tbravocalc.web.domain.CalculateResponse;
import net.intcoder.tbravocalc.web.dto.Spreadsheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ExternalCalculatorService {

    @Autowired
    private CalculateRequestService requestService;
    @Autowired
    private CalculateResponseService responseService;

    @Value("${path.to.external.java:java}")
    private String pathToJava;

    @Value("${path.to.external.calculator}")
    private String pathToCalculator;

    private volatile Path binPath = null;

    @PostConstruct
    public void start() {
        if (pathToCalculator != null) {
            var path = Path.of(pathToCalculator);
            setBinPath(path);
        }
    }

    @SneakyThrows
    public void setBinPath(Path binPath) {
        var tmp = Files.createTempFile("calculator", ".tmp.jar");

        try (var in = Files.newInputStream(binPath);
             var out = Files.newOutputStream(tmp)) {
            in.transferTo(out);
        }

        this.binPath = tmp;
    }

    @Async
    @SneakyThrows
    public void process(UUID requestId) {
        var req = requestService.get(requestId);

        var om = new ObjectMapper();

        var spreadsheet = om.readValue(req.getSpreadsheetJson(), Spreadsheet.class);
        var tmpSpreadSheetFile = Files.createTempFile("spreadsheet", ".txt.tmp");

        try (var w = Files.newBufferedWriter(tmpSpreadSheetFile)) {
            for (var n : spreadsheet.array()) {
                w.write(String.valueOf(n));
                w.newLine();
            }
        }

        String processStartString = pathToJava + " " + binPath.toAbsolutePath() + " -s " + tmpSpreadSheetFile.toAbsolutePath().toString() + " -t " + req.getTarget();
        Process p = Runtime.getRuntime().exec(processStartString);

        var processOutput = new StringBuilder();
        var processErrorOutput = new StringBuilder();

        try(var input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;

            while ((line = input.readLine()) != null) {
                processOutput.append(line).append("\n");
            }
        }

        try(var input = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String line;

            while ((line = input.readLine()) != null) {
                processErrorOutput.append(line).append("\n");
            }
        }

        responseService.saveResponse(requestId, processOutput.toString() + "\n" + processErrorOutput.toString());
    }
}
