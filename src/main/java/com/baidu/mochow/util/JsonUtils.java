/*
 * Copyright 2014-2020 Baidu, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.baidu.mochow.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.baidu.mochow.exception.MochowClientException;
import com.baidu.mochow.model.entity.Row;
import com.baidu.mochow.model.entity.RowDeserializer;
import com.baidu.mochow.model.entity.RowSerializer;

/**
 * JsonUtils for Serialization and deserialization of JSON
 */
public class JsonUtils {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        JsonUtils.OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JsonUtils.OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        JsonUtils.OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonUtils.OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Row.class, new RowSerializer());
        module.addDeserializer(Row.class, new RowDeserializer());
        OBJECT_MAPPER.registerModule(module);
    }

    private static final ObjectWriter WRITER = JsonUtils.OBJECT_MAPPER.writer();
    private static final ObjectWriter PRETTY_WRITER = JsonUtils.OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    public static String toJsonPrettyString(Object value) throws JsonProcessingException {
        return JsonUtils.PRETTY_WRITER.writeValueAsString(value);
    }

    public static String toJsonString(Object value) {
        try {
            return JsonUtils.WRITER.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Returns the deserialized object from the given json string and target class; or null if the given json string is
     * null.
     */
    public static <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        try {
            return JsonUtils.OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new MochowClientException("Unable to parse Json String.", e);
        }
    }

    public static <T> T loadFrom(File file, Class<T> clazz) throws IOException {
        try {
            return JsonUtils.OBJECT_MAPPER.readValue(file, clazz);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T loadFrom(InputStream input, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {
        return JsonUtils.OBJECT_MAPPER.readValue(input, clazz);
    }

    public static void load(InputStream input, Object obj) throws IOException, JsonProcessingException {
        OBJECT_MAPPER.readerForUpdating(obj).readValue(input);
    }

    public static ObjectMapper getObjectMapper() {
        return JsonUtils.OBJECT_MAPPER;
    }
}