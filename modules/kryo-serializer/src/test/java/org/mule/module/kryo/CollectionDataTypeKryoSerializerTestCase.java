/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mule.tck.size.SmallTest;
import org.mule.transformer.types.CollectionDataType;

import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;

import java.util.Collection;

@SmallTest
public class CollectionDataTypeKryoSerializerTestCase extends AbstractDataTypeKryoSerializerTestCase<CollectionDataType>
{

    private static final Class<? extends Collection> COLLECTION_TYPE = Collection.class;

    @Override
    protected CollectionDataType createDataType()
    {
        return new CollectionDataType(COLLECTION_TYPE, TYPE, MIME_TYPE);
    }

    @Override
    protected Serializer<CollectionDataType> createSerializer()
    {
        return new CollectionDataTypeKryoSerializer();
    }

    @Override
    protected Class<CollectionDataType> getDataTypeClass()
    {
        return CollectionDataType.class;
    }

    @Override
    protected void assertDeserializedDataType(CollectionDataType serializedDataType)
    {
        super.assertDeserializedDataType(serializedDataType);
        assertThat(TYPE, equalTo(serializedDataType.getItemType()));
    }

    @Override
    protected Class getExpectedType()
    {
        return COLLECTION_TYPE;
    }

    @Override
    protected Input initialiseReadMocks()
    {
        Input input = super.initialiseReadMocks();

        Registration collectionTypeRegistration = mock(Registration.class);
        when(collectionTypeRegistration.getType()).thenReturn(COLLECTION_TYPE);
        Registration itemTypeRegistration = mock(Registration.class);
        when(itemTypeRegistration.getType()).thenReturn(TYPE);

        when(kryo.readClass(input))
                .thenReturn(collectionTypeRegistration)
                .thenReturn(itemTypeRegistration);

        return input;
    }
}
