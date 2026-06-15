package com.framework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String getValueFromFile(String filePath, String key) {
        try {
            JsonNode rootNode = mapper.readTree(new File("src/test/resources/" + filePath));
            JsonNode node = rootNode.get(key);
            
            if (node == null) {
                throw new IllegalArgumentException("Key '" + key + "' not found in file " + filePath);
            }
            
            if (node.isTextual()) {
                return node.asText();
            } else {
                // If it's a JSON array/object, return the JSON string representation
                return node.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file: " + filePath, e);
        }
    }
}
