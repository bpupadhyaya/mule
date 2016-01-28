/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.tck.size.SmallTest;
import org.mule.transformer.types.SetDataType;

import com.esotericsoftware.kryo.Serializer;

import java.util.Set;

@SmallTest
public class SetDataTypeKryoSerializerTestCase extends AbstractDataTypeKryoSerializerTestCase<SetDataType>
{
    @Override
    protected Serializer<SetDataType> createSerializer()
    {
        return new SetDataTypeKryoSerializer();
    }

    @Override
    protected Class<SetDataType> getDataTypeClass()
    {
        return SetDataType.class;
    }

    @Override
    protected SetDataType createDataType()
    {
        return new SetDataType(TYPE, MIME_TYPE);
    }

    @Override
    protected Class getExpectedType()
    {
        return Set.class;
    }
}
