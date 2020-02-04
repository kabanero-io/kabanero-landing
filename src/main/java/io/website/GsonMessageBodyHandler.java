/******************************************************************************
 *
 * Copyright 2019 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package io.website;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;


// The auto generated java mappings for the Kabanero CRD are built using Gson so
// we need a Gson handler that can de/serialize those Kabanero POJO's to/from JSON

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GsonMessageBodyHandler<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {
    private final Gson gson;

    @Context
    private UriInfo ui;

    public GsonMessageBodyHandler() {
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd hh:mm:ss.S")
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .registerTypeAdapter(DateTime.class, deserializeJodaTime)
                .registerTypeAdapter(DateTime.class, serializeJodaTime);

        this.gson = builder.create();
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {

        InputStreamReader isr = new InputStreamReader(entityStream, "UTF-8");
        try {
            return gson.fromJson(isr, type);
        } finally {
            isr.close();
        }
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {

        PrintWriter pw = new PrintWriter(entityStream);
        try {
            String json = gson.toJson(t);
            pw.write(json);
            pw.flush();
        } finally {
            pw.close();
        }
    }

    // The Kabanero CRD mappings have org.joda.time.DateTime objects that gson will try to deserialize the object the way down,
    // which is not what we want. This overrides that behavior for the Joda DateTime class so we get just a nice timestamp.
    JsonDeserializer<DateTime> deserializeJodaTime = new JsonDeserializer<DateTime>() {
        @Override
        public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return ISODateTimeFormat.dateTime().parseDateTime(json.getAsString());
        };
    };

    JsonSerializer<DateTime> serializeJodaTime = new JsonSerializer<DateTime>() {
        @Override
        public JsonElement serialize(DateTime time, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(ISODateTimeFormat.dateTime().print(time));
        };
    };

}