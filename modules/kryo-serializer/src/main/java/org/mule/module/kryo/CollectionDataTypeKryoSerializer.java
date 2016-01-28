/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.transformer.types.CollectionDataType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * A specialization of {@link SimpleDataTypeKryoSerializer} to handle
 * {@linke CollectionDataType} instances
 *
 * @since 3.7.0
 */
class CollectionDataTypeKryoSerializer extends SimpleDataTypeKryoSerializer<CollectionDataType>
{

    @Override
    public void write(Kryo kryo, Output output, CollectionDataType object)
    {
        super.write(kryo, output, object);
        kryo.writeClass(output, object.getItemType());
    }

    @Override
    protected CollectionDataType createDataType(Kryo kryo, Input input, Class<?> type, String mimeType)
    {
        Class itemType = kryo.readClass(input).getType();
        return new CollectionDataType(type, itemType, mimeType);
    }
}
