/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.module.http.internal.ParameterMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.List;
import java.util.Map;

/**
 * An implementation of {@link Serializer} for instance of {@link ParameterMap}.
 * The reason why this serializer is needed is because the standard kryo serializer
 * for maps would use the {@link ParameterMap#get(Object)} method which only returns
 * the last value associated to a key, loosing the other values for that key.
 *
 * This serializer overcomes that problem by serializing using the
 * {@link ParameterMap#toListValuesMap()} method
 *
 * @since 3.7.0
 */
final class ParameterMapKryoSerializer extends Serializer<ParameterMap>
{

    @Override
    public void write(Kryo kryo, Output output, ParameterMap object)
    {
        kryo.writeClassAndObject(output, object.toListValuesMap());
    }

    @Override
    public ParameterMap read(Kryo kryo, Input input, Class<ParameterMap> type)
    {
        Map<String, ? extends List<String>> map = (Map<String, ? extends List<String>>) kryo.readClassAndObject(input);
        return new ParameterMap(map);
    }
}
