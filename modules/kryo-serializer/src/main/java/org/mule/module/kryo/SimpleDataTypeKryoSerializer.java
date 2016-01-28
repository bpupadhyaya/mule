/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.api.metadata.SimpleDataType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A serializer to handle instances of {@link SimpleDataType}.
 * <p/>
 * Although Kryo can handle this class by itself, this serializer
 * provides a performance boost because it spares kryo the work of
 * resolving generic metadata
 *
 * @since 3.7.0
 */
class SimpleDataTypeKryoSerializer<T extends SimpleDataType> extends Serializer<T>
{

    @Override
    public void write(Kryo kryo, Output output, T object)
    {
        kryo.writeClass(output, object.getType());
        output.writeString(object.getMimeType());
        output.writeString(object.getEncoding());
    }

    @Override
    public T read(Kryo kryo, Input input, Class<T> clazz)
    {
        Class<?> type = kryo.readClass(input).getType();
        String mimeType = input.readString();
        String encoding = input.readString();

        T dataType = createDataType(kryo, input, type, mimeType);
        dataType.setEncoding(encoding);

        return dataType;
    }

    protected T createDataType(Kryo kryo, Input input, Class<?> type, String mimeType)
    {
        return (T) new SimpleDataType<>(type, mimeType);
    }
}
