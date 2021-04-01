package net.intcoder.tbravocalc.web.service;

import net.intcoder.tbravocalc.web.TBravoCalcWebIntegrationTest;
import net.intcoder.tbravocalc.web.domain.CalculateRequest;
import net.intcoder.tbravocalc.web.dto.PrintType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@TBravoCalcWebIntegrationTest
class ExternalCalculatorServiceTest {

    @Value("${calculator.bin.path}")
    private String pathToBin;

    private Path binPath;

    @SpyBean
    ExternalCalculatorService externalCalculatorService;

    @BeforeEach
    void setUp() throws IOException {
        byte[] mockData = RandomUtils.nextBytes(512);
        var path = Path.of(pathToBin);

        if (!Files.exists(path)) {
            Files.write(path, mockData);
        }

        binPath = Path.of(pathToBin);
    }

    @Test
    void updateBin() throws IOException {
        byte[] newData = RandomUtils.nextBytes(512);
        var tmp = Files.createTempFile("tbravocalcweb-unit", ".tmp");
        Files.write(tmp, newData);

        externalCalculatorService.updateBin(tmp);
        verify(externalCalculatorService, times(1)).setBinPath(any(Path.class));

        FileUtils.deleteQuietly(tmp.toFile());

        var data = Files.readAllBytes(binPath);
        assertArrayEquals(newData, data);
    }

    @Test
    void setBinPath() throws IOException {
        byte[] newData = RandomUtils.nextBytes(512);
        var tmp = Files.createTempFile("tbravocalcweb-unit", ".tmp");
        Files.write(tmp, newData);

        var before = externalCalculatorService.getBinPath();
        externalCalculatorService.setBinPath(tmp);
        FileUtils.deleteQuietly(tmp.toFile());
        var after = externalCalculatorService.getBinPath();

        var original = Path.of(externalCalculatorService.getPathToBin());

        assertNotEquals(before, after);
        assertNotEquals(original, after);
    }

    @Test
    void buildExecuteString() throws IOException {
        System.out.println(externalCalculatorService.buildExecuteString(Files.createTempFile(null, null),
                new CalculateRequest(
                        UUID.randomUUID(),
                        "",
                        3000,
                        true,
                        null,
                        PrintType.ALL,
                        LocalDateTime.now()
                )));
    }
}