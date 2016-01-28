/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo.compression;

import org.mule.module.kryo.KryoObjectSerializer;

/**
 * A simple enum listing the supported compression modes for
 * the {@link KryoObjectSerializer}. Each of these
 * entries also provide access to a {@link KryoCompressor}
 * instance which implements the described compression mode
 *
 * @since 3.7.0
 */
public enum KryoCompressionMode
{
    /**
     * Use this mode if you desire no compression at all
     */
    NONE(new NullKryoCompressor()),

    /**
     * Use this mode if you desire to use the Deflate
     * compression algorithm
     */
    DEFLATE(new DeflateKryoCompressor()),

    /**
     * Use this mode if you desire to use
     * GZip compression
     */
    GZIP(new GzipKryoCompressor());

    private final KryoCompressor compressor;

    KryoCompressionMode(KryoCompressor compressor)
    {
        this.compressor = compressor;
    }

    /**
     * Returns a {@link KryoCompressor} which
     * implements {@code this} mode
     *
     * @return a {@link KryoCompressor}
     */
    public KryoCompressor getCompressor()
    {
        return compressor;
    }
}
