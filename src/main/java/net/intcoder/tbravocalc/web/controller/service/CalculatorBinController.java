package net.intcoder.tbravocalc.web.controller.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import net.intcoder.tbravocalc.web.service.ExternalCalculatorService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;

@RestController
@RequestMapping("/service")
@Api("Calculator BIN Service")
public class CalculatorBinController {

    @Autowired
    private ExternalCalculatorService externalCalculatorService;

    @PostMapping("/update")
    @ApiOperation("Update BIN")
    @SneakyThrows
    public void update(@RequestPart MultipartFile multipartFile,
                       @RequestPart String updateToken) {
        var tmp = Files.createTempFile("calculator", ".tmp.jar");
        multipartFile.transferTo(tmp);

        try {
            externalCalculatorService.updateBin(tmp.toAbsolutePath(), updateToken);
        } catch (Throwable t) {
            throw t;
        } finally {
            FileUtils.deleteQuietly(tmp.toFile());
        }
    }
}
