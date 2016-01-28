/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.api.metadata.SimpleDataType;
import org.mule.tck.size.SmallTest;

import com.esotericsoftware.kryo.Serializer;

@SmallTest
public class SimpleDataTypeKryoSerializerTestCase extends AbstractDataTypeKryoSerializerTestCase<SimpleDataType>
{

    @Override
    protected Serializer<SimpleDataType> createSerializer()
    {
        return new SimpleDataTypeKryoSerializer();
    }

    @Override
    protected SimpleDataType createDataType()
    {
        return new SimpleDataType(TYPE, MIME_TYPE);
    }

    protected Class<SimpleDataType> getDataTypeClass()
    {
        return SimpleDataType.class;
    }
}
