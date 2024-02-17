package com.jmlynarz.ksaccountbalance.serde;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;

public class JsonDeserializer<T> implements Deserializer<T> {

    private final Gson gson = new GsonBuilder().create();

    private final Class<T> destinationClass;

    public JsonDeserializer(Class<T> destinationClass) {
        this.destinationClass = destinationClass;
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;

        try {
            return gson.fromJson(new String(bytes, StandardCharsets.UTF_8), destinationClass);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing message", e);
        }
    }
}