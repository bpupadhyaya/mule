/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo.config;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;

import org.mule.module.kryo.KryoObjectSerializer;
import org.mule.module.kryo.compression.KryoCompressionMode;

import org.springframework.beans.factory.FactoryBean;

/**
 * A {@link FactoryBean} to construct instances of
 * {@link KryoObjectSerializer}
 *
 * @since 3.7.0
 */
final class KryoObjectSerializerFactoryBean implements FactoryBean<KryoObjectSerializer>, MuleContextAware
{

    private KryoCompressionMode compressionMode;
    private MuleContext muleContext;

    @Override
    public Class<?> getObjectType()
    {
        return KryoObjectSerializer.class;
    }

    @Override
    public KryoObjectSerializer getObject() throws Exception
    {
        KryoObjectSerializer serializer = new KryoObjectSerializer(compressionMode);
        serializer.setMuleContext(muleContext);

        return serializer;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setCompressionMode(KryoCompressionMode compressionMode)
    {
        this.compressionMode = compressionMode;
    }

    public KryoCompressionMode getCompressionMode()
    {
        return compressionMode;
    }

    @Override
    public void setMuleContext(MuleContext muleContext)
    {
        this.muleContext = muleContext;
    }
}
