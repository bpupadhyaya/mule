/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.kryo;

import org.mule.api.MuleContext;
import org.mule.api.context.MuleContextAware;
import org.mule.api.serialization.SerializationException;
import org.mule.util.store.DeserializationPostInitialisable;

import com.esotericsoftware.kryo.Serializer;

/**
 * Base class for Mule's custom implementations of {@link Serializer}
 * 
 * @since 3.7.0
 */
abstract class MuleKryoSerializerSupport<T> extends Serializer<T> implements MuleContextAware
{

    private MuleContext muleContext;

    protected void init(T object)
    {
        if (object instanceof DeserializationPostInitialisable)
        {
            try
            {
                DeserializationPostInitialisable.Implementation.init(object, muleContext);
            }
            catch (Exception e)
            {
                throw new SerializationException(String.format(
                    "Exception was found initializing object of class %s after deserealization",
                    object.getClass().getCanonicalName()), e);
            }
        }
    }

    @Override
    public void setMuleContext(MuleContext context)
    {
        muleContext = context;
    }
}
