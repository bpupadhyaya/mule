/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.transformer.types.ListDataType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

/**
 * A specialization of {@link SimpleDataTypeKryoSerializer} which
 * handles instances of {@link ListDataType}
 *
 * @since 3.7.0
 */
class ListDataTypeKryoSerializer extends SimpleDataTypeKryoSerializer<ListDataType>
{

    @Override
    protected ListDataType createDataType(Kryo kryo, Input input, Class<?> type, String mimeType)
    {
        return new ListDataType(type, mimeType);
    }
}
