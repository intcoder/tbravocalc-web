package net.intcoder.tbravocalc.web.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.intcoder.tbravocalc.web.domain.CalculateResponse;
import net.intcoder.tbravocalc.web.dto.PrintType;
import net.intcoder.tbravocalc.web.dto.Spreadsheet;
import net.intcoder.tbravocalc.web.service.CalculateRequestService;
import net.intcoder.tbravocalc.web.service.CalculateResponseService;
import net.intcoder.tbravocalc.web.service.ExternalCalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

@RestController
@RequestMapping("/")
@Api(tags = "Calculator")
public class CalculateController {

    @Autowired
    private CalculateRequestService calculateRequestService;
    @Autowired
    private CalculateResponseService calculateResponseService;

    @PostMapping("/sendFile")
    @ApiOperation("Send file")
    public UUID sendRequest(
            @RequestPart MultipartFile spreadsheet,
            @RequestParam double target,
            @RequestParam(defaultValue = "false") boolean reversed,
            @RequestParam(required = false) Optional<Long> depth,
            @RequestParam(defaultValue = "FIRST") PrintType printType) throws IOException {

        return calculateRequestService.saveRequest(target, depth.map(OptionalLong::of).orElseGet(OptionalLong::empty), printType, Spreadsheet.fromMultipartFile(spreadsheet));
    }

    @PostMapping("/send")
    @ApiOperation("Send")
    public UUID sendRequest(
            @RequestBody Spreadsheet spreadsheet,
            @RequestParam double target,
            @RequestParam(defaultValue = "false") boolean reversed,
            @RequestParam(required = false) Optional<Long> depth,
            @RequestParam(defaultValue = "FIRST") PrintType printType) throws IOException {

        return calculateRequestService.saveRequest(target, depth.map(OptionalLong::of).orElseGet(OptionalLong::empty), printType, spreadsheet);
    }

    @PostMapping("/sendArray")
    @ApiOperation("Send")
    public UUID sendRequest(
            @RequestBody double[] spreadsheet,
            @RequestParam double target,
            @RequestParam(defaultValue = "false") boolean reversed,
            @RequestParam(required = false) Optional<Long> depth,
            @RequestParam(defaultValue = "FIRST") PrintType printType) throws IOException {

        return calculateRequestService.saveRequest(target, depth.map(OptionalLong::of).orElseGet(OptionalLong::empty), printType, new Spreadsheet(spreadsheet));
    }

    @GetMapping("/getResponse")
    public String getResponse(String requestId) {
        UUID id;
        try {
            id = UUID.fromString(requestId);
        } catch (Exception e) {
            return "Bad request id";
        }



        return calculateResponseService.find(UUID.fromString(requestId))
                .map(CalculateResponse::getResponseString)
                .orElse("Request not found");
    }
}
