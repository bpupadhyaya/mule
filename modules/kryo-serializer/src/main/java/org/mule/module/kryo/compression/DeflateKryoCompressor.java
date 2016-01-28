/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo.compression;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * A {@link KryoCompressor} which uses the deflate compression
 * algorithm
 *
 * @since 3.7.0
 */
final class DeflateKryoCompressor implements KryoCompressor
{

    /**
     * @param outputStream the {@link OutputStream} to compress
     * @return a {@link DeflaterOutputStream}
     */
    @Override
    public OutputStream compress(OutputStream outputStream)
    {
        return new DeflaterOutputStream(outputStream);
    }

    /**
     * @param inputStream the {@link InputStream} to decompress
     * @return a {@link InflaterInputStream}
     */
    @Override
    public InputStream decompress(InputStream inputStream)
    {
        return new InflaterInputStream(inputStream);
    }
}
