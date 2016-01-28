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
 * A {@link KryoCompressor} which doesn't do anything.
 * Use this class when you don't want to any compression at all
 *
 * @since 3.7.0
 */
final class NullKryoCompressor implements KryoCompressor
{

    /**
     * @param outputStream an {@link OutputStream}
     * @return the same untouched {@code outputStream}
     */
    @Override
    public OutputStream compress(OutputStream outputStream)
    {
        return outputStream;
    }

    /**
     * @param inputStream a {@link InputStream}
     * @return the same untouched {@code inputStream}
     */
    @Override
    public InputStream decompress(InputStream inputStream)
    {
        return inputStream;
    }
}
