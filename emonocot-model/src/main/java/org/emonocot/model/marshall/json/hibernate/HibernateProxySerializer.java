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

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

/**
 * Serializer to use for values proxied using {@link HibernateProxy}.
 *<p>
 * TODO: should try to make this work more like Jackson
 * <code>BeanPropertyWriter</code>, possibly sub-classing
 * it -- it handles much of functionality we need, and has
 * access to more information than value serializers (like
 * this one) have.
 */
public class HibernateProxySerializer
extends JsonSerializer<HibernateProxy>
{
	/**
	 * Property that has proxy value to handle
	 */
	protected final BeanProperty _property;

	protected final boolean _forceLazyLoading;

	/**
	 * For efficient serializer lookup, let's use this; most
	 * of the time, there's just one type and one serializer.
	 */
	protected PropertySerializerMap _dynamicSerializers;

	/*
    /**********************************************************************
    /* Life cycle
    /**********************************************************************
	 */

	public HibernateProxySerializer(boolean forceLazyLoading)
	{
		_property = null;
		_forceLazyLoading = forceLazyLoading;
		_dynamicSerializers = PropertySerializerMap.emptyMap();
	}

	/*
    /**********************************************************************
    /* JsonSerializer impl
    /**********************************************************************
	 */

	@Override
	public void serialize(HibernateProxy value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException
	{
		Object proxiedValue = findProxied(value);
		// TODO: figure out how to suppress nulls, if necessary? (too late for that here)
		if (proxiedValue == null) {
			provider.defaultSerializeNull(jgen);
			return;
		}
		findSerializer(provider, proxiedValue).serialize(proxiedValue, jgen, provider);
	}

	public void serializeWithType(HibernateProxy value, JsonGenerator jgen, SerializerProvider provider,
			TypeSerializer typeSer)
					throws IOException, JsonProcessingException
	{
		Object proxiedValue = findProxied(value);
		if (proxiedValue == null) {
			provider.defaultSerializeNull(jgen);
			return;
		}
		/* This isn't exactly right, since type serializer really refers to proxy
		 * object, not value. And we really don't either know static type (necessary
		 * to know how to apply additional type info) or other things;
		 * so it's not going to work well. But... we'll do out best.
		 */
		findSerializer(provider, proxiedValue).serializeWithType(proxiedValue, jgen, provider, typeSer);
	}

	/*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
	 */

	protected JsonSerializer<Object> findSerializer(SerializerProvider provider, Object value)
			throws IOException, JsonProcessingException
			{
		/* TODO: if Hibernate did use generics, or we wanted to allow use of Jackson
		 *  annotations to indicate type, should take that into account.
		 */
		Class<?> type = value.getClass();
		/* we will use a map to contain serializers found so far, keyed by type:
		 * this avoids potentially costly lookup from global caches and/or construction
		 * of new serializers
		 */
		PropertySerializerMap.SerializerAndMapResult result = _dynamicSerializers.findAndAddSerializer(type,
				provider, _property);
		if (_dynamicSerializers != result.map) {
			_dynamicSerializers = result.map;
		}
		return result.serializer;
			}


	/**
	 * Helper method for finding value being proxied, if it is available
	 * or if it is to be forced to be loaded.
	 */
	protected Object findProxied(HibernateProxy proxy)
	{
		LazyInitializer init = proxy.getHibernateLazyInitializer();
		if (!_forceLazyLoading && init.isUninitialized()) {
			return null;
		}
		return init.getImplementation();
	}
}
