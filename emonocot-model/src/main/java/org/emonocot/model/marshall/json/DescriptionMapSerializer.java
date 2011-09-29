package org.emonocot.model.marshall.json;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.emonocot.model.description.Content;
import org.emonocot.model.description.Feature;

/**
 *
 * @author ben
 *
 */
public class DescriptionMapSerializer extends
        JsonSerializer<Map<Feature, Content>> {

    @Override
    public final void serialize(final Map<Feature, Content> map,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializationProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (Content content : map.values()) {
            jsonGenerator.writeObject(content);
        }
        jsonGenerator.writeEndArray();

    }

}
