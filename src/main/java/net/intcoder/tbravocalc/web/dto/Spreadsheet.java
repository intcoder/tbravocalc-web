package net.intcoder.tbravocalc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record Spreadsheet(
        @JsonProperty("array")
        double... array
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Spreadsheet that = (Spreadsheet) o;

        return Arrays.equals(array, that.array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(array);
    }

    @Override
    public String toString() {
        var om = new ObjectMapper();
        try {
            return om.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Spreadsheet fromString(String json) {
        var om = new ObjectMapper();
        try {
            return om.readValue(json, Spreadsheet.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static Spreadsheet fromMultipartFile(MultipartFile multipartFile) throws IOException {
        List<Double> list = new ArrayList<>();

        try (var in = multipartFile.getInputStream()) {
            var r = new BufferedReader(new InputStreamReader(in));

            String s;
            while ((s = r.readLine()) != null) {
                list.add(Double.valueOf(s));
            }
        }

        var arr = list.stream().mapToDouble(d -> d).toArray();

        return new Spreadsheet(arr);
    }
}
