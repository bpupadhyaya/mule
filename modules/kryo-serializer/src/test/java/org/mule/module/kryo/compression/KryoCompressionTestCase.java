/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo.compression;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mule.api.lifecycle.LifecycleUtils.initialiseIfNeeded;
import org.mule.module.kryo.KryoObjectSerializer;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.util.ClassUtils;
import org.mule.util.compression.GZIPCompressorInputStream;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class KryoCompressionTestCase extends AbstractMuleContextTestCase
{

    private static final String PAYLOAD = RandomStringUtils.randomAlphabetic(1024);

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data()
    {
        return Arrays.asList(new Object[][] {
                {KryoCompressionMode.DEFLATE, DeflaterInputStream.class, InflaterInputStream.class},
                {KryoCompressionMode.GZIP, GZIPCompressorInputStream.class, GZIPInputStream.class},
        });
    }

    private final KryoCompressionMode compressionMode;
    private final Class<? extends InputStream> compressInputStreamType;
    private final Class<? extends InputStream> decompressInputStreamType;

    private KryoObjectSerializer vanillaSerializer;
    private KryoObjectSerializer serializerWithCompression;

    public KryoCompressionTestCase(KryoCompressionMode compressionMode, Class<? extends InputStream> compressInputStreamType, Class<? extends InputStream> decompressInputStreamType)
    {
        this.compressionMode = compressionMode;
        this.compressInputStreamType = compressInputStreamType;
        this.decompressInputStreamType = decompressInputStreamType;
    }

    @Before
    public void before() throws Exception
    {
        vanillaSerializer = new KryoObjectSerializer();
        initialiseIfNeeded(vanillaSerializer, muleContext);

        serializerWithCompression = new KryoObjectSerializer(compressionMode);
        initialiseIfNeeded(serializerWithCompression, muleContext);
    }

    @Test
    public void sizeIsLower()
    {
        byte[] compressedBytes = serializerWithCompression.serialize(PAYLOAD);
        byte[] uncompressedBytes = vanillaSerializer.serialize(PAYLOAD);

        assertThat(compressedBytes.length, is(lessThan(uncompressedBytes.length)));
    }

    @Test
    public void roundTrip() throws Exception
    {
        byte[] compresedBytes = serializerWithCompression.serialize(PAYLOAD);
        String deserealizedPayload = serializerWithCompression.deserialize(compresedBytes);
        assertThat(deserealizedPayload, is(equalTo(PAYLOAD)));
    }

    @Test
    public void serialize() throws Exception
    {
        byte[] compresedBytes = serializerWithCompression.serialize(PAYLOAD);
        String deserealizedPayload = vanillaSerializer.deserialize(getDecompressedInputStream(compresedBytes));
        assertThat(deserealizedPayload, is(equalTo(PAYLOAD)));
    }

    @Test
    public void deserealize() throws Exception
    {
        byte[] uncompressedBytes = vanillaSerializer.serialize(PAYLOAD);
        String deserealizedPayload = serializerWithCompression.deserialize(getCompressedInputStream(uncompressedBytes));
        assertThat(deserealizedPayload, is(equalTo(PAYLOAD)));
    }

    private InputStream getDecompressedInputStream(byte[] bytes) throws Exception
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return ClassUtils.instanciateClass(decompressInputStreamType, inputStream);
    }

    private InputStream getCompressedInputStream(byte[] bytes) throws Exception
    {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return ClassUtils.instanciateClass(compressInputStreamType, inputStream);
    }
}
