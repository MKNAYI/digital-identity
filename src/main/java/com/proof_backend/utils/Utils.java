package com.proof_backend.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.proof_backend.exceptions.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class Utils {

    public static JsonNode stringToJsonNode(String jsonString) {
        log.info("Utils :: stringToJsonNode() invoked");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            log.error("Exception occurred while converting json string to JSON node: error-message: {}", e.getMessage(), e);
            throw new CustomException(String.format("Fail to covert verified document response to Json node: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static JsonNode objectToJsonNode(Object request) {
        log.info("Utils :: objectToJsonNode() invoked");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonString = objectMapper.writeValueAsString(request);
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            log.error("Exception occurred while converting object to JSON node: error-message: {}", e.getMessage(), e);
            throw new CustomException(String.format("Fail to covert object to Json node: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static List<Map<String, Object>> arrayNodeToList(JsonNode jsonNode) {
        log.info("Utils :: arrayNodeToList() invoked");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (jsonNode.isArray()) {
                ArrayNode warningArray = (ArrayNode) jsonNode;
                // Convert ArrayNode to List<JsonNode>
                return objectMapper.convertValue(warningArray, List.class);
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Exception occurred while converting JSON node to list of Json node: error-message: {}", e.getMessage(), e);
            throw new CustomException(String.format("Fail to covert Json node list of Json node: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static LocalDateTime toCovertLongDateToLocalDateTime(Long timestamp){
        log.info("Utils :: toCovertLongDateToLocalDateTime() invoked");
        try {
            log.info("timestamp: {}", timestamp);
            if(timestamp == null){
                return null;
            }
            Instant instant = Instant.ofEpochSecond(timestamp);
            LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
            log.info("Date: {}", dateTime);
            return dateTime;
        } catch (Exception e){
            log.error("Exception occurred while converting long date to Localdatetime: error-message: {}", e.getMessage(), e);
            throw new CustomException(String.format("Fail to covert requested long date to Localdatetime: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static String convertLocalDateTimeToEST(LocalDateTime dateTime){
        log.info("Utils :: convertLocalDateTimeToEST() invoked, dateTime: {}", dateTime);
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d'th' yyyy 'at' hh:mm a 'EST'", Locale.ENGLISH);
            return dateTime.atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneId.of("America/New_York")).format(formatter);
        } catch (Exception e){
            log.error("Exception occurred while converting Localdatetime to EST: error-message: {}", e.getMessage(), e);
            throw new CustomException(String.format("Fail to covert requested Localdatetime to EST: %s", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
