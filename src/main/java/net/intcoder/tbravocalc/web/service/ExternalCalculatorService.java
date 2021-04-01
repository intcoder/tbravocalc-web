package net.intcoder.tbravocalc.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.intcoder.tbravocalc.web.domain.CalculateRequest;
import net.intcoder.tbravocalc.web.dto.Spreadsheet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ExternalCalculatorService {

    @Autowired
    private CalculateRequestService requestService;
    @Autowired
    private CalculateResponseService responseService;

    @Value("${calculator.bin.executor:java}")
    @Getter(AccessLevel.PROTECTED)
    private String binExecutor;

    @Value("${calculator.bin.path}")
    @Getter(AccessLevel.PROTECTED)
    private String pathToBin;

    @Value("${calculator.bin.update.token}")
    private String calculatorBinUpdateToken;

    @Getter(AccessLevel.PROTECTED)
    private volatile Path binPath = null;

    @PostConstruct
    protected void init() {
        var path = Path.of(pathToBin);

        if (Files.exists(path)) {
            setBinPath(path);
        } else {
            log.warn("Calculator BIN doesn't exist");
        }
    }

    @SneakyThrows
    protected void setBinPath(Path binPath) {
        var tmp = Files.createTempFile("calculator", ".tmp.jar");
        Files.copy(binPath, tmp, StandardCopyOption.REPLACE_EXISTING);

        this.binPath = tmp;
    }

    public void updateBin(Path newFile, String token) throws IOException {
        if (token.equals(calculatorBinUpdateToken)) {
            updateBin(newFile);
        } else {
            throw new RuntimeException("Bad update token");
        }
    }

    protected void updateBin(Path newFile) throws IOException {
        var original = Path.of(pathToBin);
        Files.copy(newFile, original, StandardCopyOption.REPLACE_EXISTING);

        var oldPath = binPath;
        init();

        FileUtils.deleteQuietly(oldPath.toFile());
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

        String processStartString = buildExecuteString(tmpSpreadSheetFile, req);
        Process p = Runtime.getRuntime().exec(processStartString);

        var processOutput = new StringBuilder();
        var processErrorOutput = new StringBuilder();

        try (var input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;

            while ((line = input.readLine()) != null) {
                processOutput.append(line).append("\n");
            }
        }

        try (var input = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String line;

            while ((line = input.readLine()) != null) {
                processErrorOutput.append(line).append("\n");
            }
        }

        responseService.saveResponse(requestId, processOutput.toString() + "\n" + processErrorOutput.toString(), StringUtils.isBlank(processErrorOutput));
    }

    protected String buildExecuteString(Path spreadSheetFile, CalculateRequest request) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isNotBlank(binExecutor)) list.add(binExecutor);
        if (binPath != null && Files.exists(binPath)) {
            list.add(binPath.toAbsolutePath().toString());
        } else {
            throw new IllegalStateException();
        }
        list.addAll(List.of("-s", spreadSheetFile.toAbsolutePath().toString()));
        list.addAll(List.of("-t", String.valueOf(request.getTarget())));
        if (request.isReversed()) list.add("-r");
        if (request.getDepth() != null) list.addAll(List.of("-d", String.valueOf(request.getDepth())));

        switch (request.getPrintType()) {
            case ALL -> list.add("-a");
            case VERBOSE -> list.add("-v");
        }

        var sb = new StringBuilder();
        list.stream().map(s -> s + " ").forEach(sb::append);
        sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }
}
