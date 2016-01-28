/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.api.metadata.DataType;
import org.mule.transformer.types.TypedValue;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class TypedValueKryoSerializer extends MuleKryoSerializerSupport<TypedValue>
{

    public void write(Kryo kryo, Output output, TypedValue object)
    {
        kryo.writeClassAndObject(output, object.getValue());
        kryo.writeClassAndObject(output, object.getDataType());
    }

    @Override
    public TypedValue read(Kryo kryo, Input input, Class<TypedValue> type)
    {
        Object value = kryo.readClassAndObject(input);
        DataType dataType = (DataType) kryo.readClassAndObject(input);

        return new TypedValue(value, dataType);
    }
}
