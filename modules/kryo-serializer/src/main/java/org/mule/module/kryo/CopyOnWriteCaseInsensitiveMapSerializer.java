/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.util.CaseInsensitiveHashMap;
import org.mule.util.CopyOnWriteCaseInsensitiveMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A {@link Serializer} for instances of {@link CopyOnWriteCaseInsensitiveMap}
 * which is able to read an instance without forcing the creation of a copy
 * per each value that is restored
 *
 * @since 3.7.0
 */
class CopyOnWriteCaseInsensitiveMapSerializer extends Serializer<CopyOnWriteCaseInsensitiveMap>
{

    @Override
    public void write(Kryo kryo, Output output, CopyOnWriteCaseInsensitiveMap map)
    {
        kryo.writeObjectOrNull(output, map.asHashMap(), CaseInsensitiveHashMap.class);
    }

    @Override
    public CopyOnWriteCaseInsensitiveMap<Object, Object> read(Kryo kryo, Input input, Class<CopyOnWriteCaseInsensitiveMap> type)
    {
        CaseInsensitiveHashMap map = kryo.readObjectOrNull(input, CaseInsensitiveHashMap.class);
        return map != null ? new CopyOnWriteCaseInsensitiveMap<>(map) : null;
    }
}
