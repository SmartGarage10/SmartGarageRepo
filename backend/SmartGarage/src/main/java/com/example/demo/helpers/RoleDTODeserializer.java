package com.example.demo.helpers;

import com.example.demo.DTO.RoleDTO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;


import java.io.IOException;

public class RoleDTODeserializer extends JsonDeserializer<RoleDTO> {
    @Override
    public RoleDTO deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        // Handle string value case
        if (p.currentToken() == JsonToken.VALUE_STRING) {
            return new RoleDTO(p.getText());
        }

        // Handle object case
        JsonNode node = p.getCodec().readTree(p);
        String roleName = node.has("roleName") ? node.get("roleName").asText() : null;
        return new RoleDTO(roleName);
    }
}