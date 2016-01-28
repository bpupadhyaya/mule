/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;


import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mule.tck.junit4.matcher.DataTypeMatcher.like;
import org.mule.api.metadata.SimpleDataType;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.transformer.types.MimeTypes;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AbstractDataTypeKryoSerializerTestCase<T extends SimpleDataType> extends AbstractMuleTestCase
{

    protected static final Class<String> TYPE = String.class;
    protected static final String MIME_TYPE = MimeTypes.APPLICATION_JSON;
    protected static final String ENCODING = "UTF-8";

    private Serializer<T> serializer;
    private T dataType;

    @Mock
    protected Kryo kryo;

    @Before
    public void before()
    {
        serializer = createSerializer();
        dataType = createDataType();
    }

    protected abstract T createDataType();

    protected abstract Class<T> getDataTypeClass();

    protected abstract Serializer<T> createSerializer();

    @Test
    public void readWithoutEncoding()
    {
        Input input = initialiseReadMocks();

        T readDataType = serializer.read(kryo, input, getDataTypeClass());
        assertDeserializedDataType(readDataType);
    }

    @Test
    public void readWithEncoding()
    {
        dataType.setEncoding(ENCODING);

        Input input = initialiseReadMocks();
        when(input.readString())
                .thenReturn(MIME_TYPE)
                .thenReturn(ENCODING);

        T readDataType = serializer.read(kryo, input, getDataTypeClass());
        assertDeserializedDataType(readDataType);
    }

    @Test
    public void writeWithoutEncoding()
    {
        assertSerialization();
    }

    @Test
    public void writeWithEncodong()
    {
        dataType.setEncoding(ENCODING);
        assertSerialization();
    }

    protected void assertSerialization()
    {
        Output output = mock(Output.class);

        serializer.write(kryo, output, dataType);
        verify(kryo).writeClass(output, dataType.getType());
        verify(output).writeString(dataType.getMimeType());
        verify(output).writeString(dataType.getEncoding());
    }

    protected void assertDeserializedDataType(T serializedDataType)
    {
        assertThat(serializedDataType, like(getExpectedType(),
                                            dataType.getMimeType(),
                                            dataType.getEncoding()));
    }

    protected Class getExpectedType()
    {
        return TYPE;
    }

    protected Input initialiseReadMocks()
    {
        Input input = mock(Input.class);
        Registration registration = mock(Registration.class);
        when(registration.getType()).thenReturn(TYPE);
        when(kryo.readClass(input)).thenReturn(registration);
        when(input.readString())
                .thenReturn(MIME_TYPE)
                .thenReturn(null);
        return input;
    }
}
