/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mule.module.kryo.CopyOnWriteCaseInsensitiveMapSerializer;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.tck.size.SmallTest;
import org.mule.util.CaseInsensitiveHashMap;
import org.mule.util.CopyOnWriteCaseInsensitiveMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class CopyOnWriteCaseInsensitiveMapSerializerTestCase extends AbstractMuleTestCase
{

    private static final String KEY = "FOO";
    private static final String VALUE = "value";

    @Mock
    private CopyOnWriteCaseInsensitiveMap map;

    private CaseInsensitiveHashMap backingMap;

    @Mock
    private Kryo kryo;

    private Serializer<CopyOnWriteCaseInsensitiveMap> serializer = new CopyOnWriteCaseInsensitiveMapSerializer();

    @Before
    public void before()
    {
        backingMap = new CaseInsensitiveHashMap();
        backingMap.put(KEY, VALUE);
    }

    @Test
    public void read()
    {
        Input input = mock(Input.class);
        when(kryo.readObjectOrNull(input, CaseInsensitiveHashMap.class)).thenReturn(backingMap);

        CopyOnWriteCaseInsensitiveMap<String, String> deserializedMap = serializer.read(kryo, input, CopyOnWriteCaseInsensitiveMap.class);
        assertThat(deserializedMap, is((notNullValue())));
        assertThat(deserializedMap.get(KEY.toLowerCase()), is(VALUE));
    }

    @Test
    public void readNull()
    {
        Input input = mock(Input.class);
        CopyOnWriteCaseInsensitiveMap<String, String> deserializedMap = serializer.read(kryo, input, CopyOnWriteCaseInsensitiveMap.class);

        assertThat(deserializedMap, is((nullValue())));
    }

    @Test
    public void write()
    {
        CopyOnWriteCaseInsensitiveMap map = mock(CopyOnWriteCaseInsensitiveMap.class);
        when(map.asHashMap()).thenReturn(backingMap);
        Output output = mock(Output.class);

        serializer.write(kryo, output, map);
        verify(kryo).writeObjectOrNull(output, backingMap, CaseInsensitiveHashMap.class);
    }

}
