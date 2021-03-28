package net.intcoder.tbravocalc.web.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

class SpreadsheetTest {

    private Spreadsheet spreadsheet;
    private String json = "{\"array\":[2510.0,2014.0,2333.0,852.0,4707.0,1608.0,1688.0,1130.0,1204.0,1099.0,4967.0,397.0,1904.0,3665.0,3540.0,876.0,2391.0,3403.0,3920.0,4636.0,4978.0,688.0,4240.0,189.0,3298.0,4814.0,2863.0,4644.0,1348.0,3686.0]}";

    @BeforeEach
    void setUp() throws IOException {
        var tmp = Files.createTempFile(null, null);

        try (var in = SpreadsheetTest.class.getResourceAsStream("/spreadsheet.txt");
             var out = Files.newOutputStream(tmp)) {
            in.transferTo(out);
        }

        var mock = new MockMultipartFile("test.txt", Files.readAllBytes(tmp));

        Files.delete(tmp);

        this.spreadsheet = Spreadsheet.fromMultipartFile(mock);
    }

    @Test
    void fromMultipartFile() {
        assertEquals(2510, spreadsheet.array()[0]);
        assertEquals(2014, spreadsheet.array()[1]);
        assertEquals(2333, spreadsheet.array()[2]);
        assertEquals(852, spreadsheet.array()[3]);
        assertEquals(3686, spreadsheet.array()[29]);
    }

    @Test
    void toStringTest() {
        assertEquals(json, spreadsheet.toString());
    }

    @Test
    void fromString() {
        assertEquals(spreadsheet, Spreadsheet.fromString(json));
    }

    @Test
    void equalsTest() {
        assertEquals(new Spreadsheet(new double[] { 1.0, 2.0 }), new Spreadsheet(new double[] { 1.0, 2.0 }));
        assertNotEquals(new Spreadsheet(new double[] { 1.0, 2.0 }), new Spreadsheet(new double[] { 10.0, 2.0 }));
    }
}