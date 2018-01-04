/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.model.marshall.json.hibernate;

import java.io.IOException;

import org.hibernate.collection.PersistentCollection;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;


/**
 * Wrapper serializer used to handle aspects of lazy loading that can be used
 * for Hibernate collection datatypes.
 */
public class PersistentCollectionSerializer
extends JsonSerializer<PersistentCollection>
implements ContextualSerializer,
ResolvableSerializer
{
	/**
	 * Property that has collection value to handle
	 */
	protected final BeanProperty _property;

	protected final boolean _forceLazyLoading;

	/**
	 * This is the nominal type used to locate actual serializer to use
	 * for contents, if this collection is to be serialized.
	 */
	protected final JavaType _serializationType;

	/**
	 * Serializer to which we delegate if serialization is not blocked.
	 */
	protected JsonSerializer<Object> _serializer;

	/*
    /**********************************************************************
    /* Life cycle
    /**********************************************************************
	 */

	public PersistentCollectionSerializer(BeanProperty property, JavaType type,
			boolean forceLazyLoading)
	{
		_property = property;
		_serializationType = type;
		_forceLazyLoading = forceLazyLoading;
	}

	/**
	 * We need to resolve actual serializer once we know the context; specifically
	 * must know type of property being serialized.
	 * If not known
	 */
	@Override
	public JsonSerializer<?> createContextual(SerializerProvider provider,
			BeanProperty property)
					throws JsonMappingException
					{
		/* If we have property, should be able to get actual polymorphic type
		 * information.
		 * May need to refine in future, in case nested types are used, since
		 * 'property' refers to field/method and main type, but contents of
		 * that type may also be resolved... in which case this would fail.
		 */
		if (property != null) {
			return new PersistentCollectionSerializer(property, property.getType(),
					_forceLazyLoading);
		}
		return this;
					}

	public void resolve(SerializerProvider provider) throws JsonMappingException
	{
		_serializer = provider.findValueSerializer(_serializationType, _property);
	}

	/*
    /**********************************************************************
    /* JsonSerializer impl
    /**********************************************************************
	 */

	@Override
	public void serialize(PersistentCollection coll, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException
	{
		// If lazy-loaded, not yet loaded, may serialize as null?
		if (!_forceLazyLoading && !coll.wasInitialized()) {
			jgen.writeNull();
			return;
		}
		Object value = coll.getValue();
		if (value == null) {
			provider.defaultSerializeNull(jgen);
		} else {
			if (_serializer == null) { // sanity check...
				throw new JsonMappingException("PersitentCollection does not have serializer set");
			}
			_serializer.serialize(value, jgen, provider);
		}
	}

	public void serializeWithType(PersistentCollection coll, JsonGenerator jgen, SerializerProvider provider,
			TypeSerializer typeSer)
					throws IOException, JsonProcessingException
	{
		if (!_forceLazyLoading && !coll.wasInitialized()) {
			jgen.writeNull();
			return;
		}
		Object value = coll.getValue();
		if (value == null) {
			provider.defaultSerializeNull(jgen);
		} else {
			if (_serializer == null) { // sanity check...
				throw new JsonMappingException("PersitentCollection does not have serializer set");
			}
			_serializer.serializeWithType(value, jgen, provider, typeSer);
		}
	}
}
