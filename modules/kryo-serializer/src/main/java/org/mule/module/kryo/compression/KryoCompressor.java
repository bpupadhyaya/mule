/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo.compression;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Compresses and decompresses kryo serialization streams.
 * Instances of this class are expected to be reusable
 * and thread-safe
 *
 * @since 3.7.0
 */
public interface KryoCompressor
{

    /**
     * Returns a new {@link OutputStream} which
     * compresses the data written in the
     * {@code outputStream}
     *
     * @param outputStream an {@link OutputStream}
     * @return a compressing {@link OutputStream}
     */
    OutputStream compress(OutputStream outputStream);

    /**
     * Takes an {@code inputStream} with compressed data
     * and in decompresses it as it's read
     *
     * @param inputStream a {@link InputStream}
     * @return an {@link InputStream} with uncompressed data
     */
    InputStream decompress(InputStream inputStream);
}
