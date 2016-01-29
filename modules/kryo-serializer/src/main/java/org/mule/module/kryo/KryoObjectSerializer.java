/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import static org.mule.module.kryo.compression.KryoCompressionMode.NONE;
import static org.mule.util.Preconditions.checkArgument;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.serialization.ObjectSerializer;
import org.mule.api.serialization.SerializationException;
import org.mule.module.kryo.compression.KryoCompressionMode;
import org.mule.module.kryo.compression.KryoCompressor;
import org.mule.serialization.internal.AbstractObjectSerializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Implementation of {@link ObjectSerializer} that uses the Kryo library to
 * serialize/deserialize, thanks to which this serializer is faster than java's
 * standard and is not limited to objects implementing {@link Serializable}. Because
 * a Kryo instance is expensive to allocate, it keeps an internal pool of Kryo
 * instances
 *
 * @since 3.7.0
 */
public final class KryoObjectSerializer extends AbstractObjectSerializer implements Initialisable
{

    private final KryoInstanceFactory kryoInstanceFactory = new KryoInstanceFactory();
        private LoadingCache<Thread, Kryo> kryoInstances;
    private final KryoCompressor compressor;

    /**
     * Creates a new instance delegating into
     * {@link #KryoObjectSerializer(KryoCompressionMode)} using
     * {@link KryoCompressionMode#NONE} as a default
     * {@link KryoCompressionMode}
     */
    public KryoObjectSerializer()
    {
        this(NONE);
    }

    /**
     * Creates an instances which uses the given {@code compressionMode}
     *
     * @param compressionMode the desired {@link KryoCompressionMode}
     */
    public KryoObjectSerializer(KryoCompressionMode compressionMode)
    {
        checkArgument(compressionMode != null, "Cannot have a null compressionMode");
        compressor = compressionMode.getCompressor();
        //TODO(pablo.kraan): CCL - added just to make it work
        try
        {
            initialise();
        }
        catch (InitialisationException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void initialise() throws InitialisationException
    {
        kryoInstances = CacheBuilder.newBuilder()
                .weakKeys()
                .build(new CacheLoader<Thread, Kryo>()
                {
                    @Override
                    public Kryo load(Thread key) throws Exception
                    {
                        return kryoInstanceFactory.getInstance(muleContext);
                    }
                });
    }

    @Override
    public void serialize(Object object, OutputStream out) throws SerializationException
    {
        doSerialize(object, out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected byte[] doSerialize(Object object) throws Exception
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        doSerialize(object, outputStream);
        return outputStream.toByteArray();
    }

    private void doSerialize(Object object, OutputStream outputStream)
    {
        Output output = new Output(compressor.compress(outputStream));
        Kryo kryo = getKryo();
        kryo.writeClassAndObject(output, object);
        output.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <T> T doDeserialize(InputStream inputStream, ClassLoader classLoader) throws Exception
    {
        Kryo kryo = getKryo();
        kryo.setClassLoader(classLoader);
        return (T) kryo.readClassAndObject(new Input(compressor.decompress(inputStream)));
    }

    private Kryo getKryo() throws SerializationException
    {
        return kryoInstances.getUnchecked(Thread.currentThread());
    }
}
