/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.mule.api.lifecycle.LifecycleUtils;
import org.mule.module.http.internal.ParameterMap;
import org.mule.module.kryo.KryoObjectSerializer;
import org.mule.tck.junit4.AbstractMuleContextTestCase;

import org.mule.module.kryo.compression.KryoCompressionMode;

import java.io.Serializable;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class ParameterMapKryoSerializerTestCase extends AbstractMuleContextTestCase
{

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String VALUE1 = "value1";
    private static final String VALUE2 = "value2";
    private static final String VALUE3 = "value3";

    private ParameterMap parameterMap = new ParameterMap();
    private KryoObjectSerializer objectSerializer;

    @Before
    public void setUp() throws Exception
    {
        parameterMap.put(KEY1, VALUE1);
        parameterMap.put(KEY2, VALUE2);
        parameterMap.put(KEY1, VALUE3);

        objectSerializer = new KryoObjectSerializer(KryoCompressionMode.NONE);
        LifecycleUtils.initialiseIfNeeded(objectSerializer, muleContext);
    }

    @Test
    public void testSerializationOfParameterMapWithMuleObjectSerializer() throws Exception
    {
        byte[] objectSerializedWithMule = objectSerializer.serialize(parameterMap);
        Object mapDeserializedWithMule = objectSerializer.deserialize(objectSerializedWithMule);
        assertRoundTripParameterMapSerialization((Serializable) mapDeserializedWithMule);
    }

    private void assertRoundTripParameterMapSerialization(Serializable mapDeserialized)
    {
        assertThat(mapDeserialized, is(instanceOf(ParameterMap.class)));

        ParameterMap parameterMap = (ParameterMap) mapDeserialized;
        List<String> listKey1 = parameterMap.getAll(KEY1);
        List<String> listKey2 = parameterMap.getAll(KEY2);

        assertThat(listKey1, Matchers.hasSize(2));
        assertThat(listKey1, Matchers.containsInAnyOrder(VALUE1, VALUE3));

        assertThat(listKey2, Matchers.hasSize(1));
        assertThat(listKey2, Matchers.containsInAnyOrder(VALUE2));
    }
}
