/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.transformer.types.SetDataType;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

/**
 * A specialization of {@link SimpleDataTypeKryoSerializer}
 * for instances of {@link SetDataType}
 *
 * @since 3.7.0
 */
public class SetDataTypeKryoSerializer extends SimpleDataTypeKryoSerializer<SetDataType>
{

    @Override
    protected SetDataType createDataType(Kryo kryo, Input input, Class<?> type, String mimeType)
    {
        return new SetDataType(type, mimeType);
    }
}
