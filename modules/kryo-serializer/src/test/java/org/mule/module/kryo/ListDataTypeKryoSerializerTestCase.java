/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.tck.size.SmallTest;
import org.mule.transformer.types.ListDataType;

import com.esotericsoftware.kryo.Serializer;

import java.util.List;

@SmallTest
public class ListDataTypeKryoSerializerTestCase extends AbstractDataTypeKryoSerializerTestCase<ListDataType>
{

    @Override
    protected Serializer<ListDataType> createSerializer()
    {
        return new ListDataTypeKryoSerializer();
    }

    @Override
    protected Class<ListDataType> getDataTypeClass()
    {
        return ListDataType.class;
    }

    @Override
    protected ListDataType createDataType()
    {
        return new ListDataType(TYPE, MIME_TYPE);
    }

    @Override
    protected Class getExpectedType()
    {
        return List.class;
    }
}
