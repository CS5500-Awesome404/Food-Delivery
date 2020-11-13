package edu.northeastern.cs5500.delivery.serialization;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.northeastern.cs5500.delivery.model.Meal;

public class MealKeyDeserializer extends KeyDeserializer {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object deserializeKey(final String key, final DeserializationContext ctxt)
            throws JsonMappingException, JsonParseException, JsonProcessingException {
        return mapper.readValue(key, Meal.class);
    }
}
