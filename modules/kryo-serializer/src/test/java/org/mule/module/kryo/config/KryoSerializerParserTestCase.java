/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo.config;

import static org.mule.module.kryo.compression.KryoCompressionMode.NONE;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.mule.functional.junit4.FunctionalTestCase;
import org.mule.module.kryo.config.KryoObjectSerializerFactoryBean;

import org.mule.module.kryo.compression.KryoCompressionMode;

import org.junit.Test;

public class KryoSerializerParserTestCase extends FunctionalTestCase
{

    private static final String GZIP = "gzip";
    private static final String DEFLATE = "deflate";
    private static final String NO_COMPRESSION = "noCompression";
    private static final String DEFAULT_KRYO = "defaultKryo";

    @Override
    protected String getConfigFile()
    {
        return "kryo-serializer-config.xml";
    }

    @Test
    public void defaultKryo() throws Exception
    {
        assertSerializerWithCompressioMode(DEFAULT_KRYO, NONE);
    }

    @Test
    public void noCompression() throws Exception
    {
        assertSerializerWithCompressioMode(NO_COMPRESSION, NONE);
    }

    @Test
    public void deflate() throws Exception
    {
        assertSerializerWithCompressioMode(DEFLATE, KryoCompressionMode.DEFLATE);
    }

    @Test
    public void gzip() throws Exception
    {
        assertSerializerWithCompressioMode(GZIP, KryoCompressionMode.GZIP);
    }

    private KryoObjectSerializerFactoryBean getFactoryBean(String name)
    {
        return muleContext.getRegistry().get("&" + name);
    }

    private void assertSerializerWithCompressioMode(String name, KryoCompressionMode compressionMode)
    {
        KryoObjectSerializerFactoryBean factoryBean = getFactoryBean(name);
        assertThat(factoryBean, is(notNullValue()));
        assertThat(factoryBean.getCompressionMode(), is(compressionMode));
    }
}
